import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OlympiadAwardsApp extends JFrame {

    private static final String OUTPUT_FILE = "order.txt";
    private static final double REGULAR_CLIENT_DISCOUNT = 0.10;

    private record GiftItem(String id, double price) {
    }

    private record PresenterSpec(String id, double fee, List<GiftItem> gifts) {
    }

    private record LocaleOption(Locale locale, String displayText) {
        @Override
        public String toString() {
            return displayText;
        }
    }

    private static final Map<String, PresenterSpec> PRESENTERS = createPresenters();
    private static final Map<String, Map<String, String>> TRANSLATIONS = createTranslations();

    private final JComboBox<LocaleOption> localeComboBox;
    private final Map<String, JCheckBox> presenterCheckBoxes = new LinkedHashMap<>();
    private final DefaultListModel<GiftDisplayItem> giftListModel = new DefaultListModel<>();
    private final JList<GiftDisplayItem> giftList = new JList<>(giftListModel);

    private final JRadioButton concertYesRadio = new JRadioButton();
    private final JRadioButton concertNoRadio = new JRadioButton();
    private final JRadioButton regularYesRadio = new JRadioButton();
    private final JRadioButton regularNoRadio = new JRadioButton();

    private final JLabel localeTitleLabel = new JLabel();
    private final JLabel presentersTitleLabel = new JLabel();
    private final JLabel giftsTitleLabel = new JLabel();
    private final JLabel concertTitleLabel = new JLabel();
    private final JLabel regularTitleLabel = new JLabel();
    private final JLabel longDateTimeLabel = new JLabel();
    private final JLabel shortDateTimeLabel = new JLabel();
    private final JLabel totalCostLabel = new JLabel();
    private final JLabel statusLabel = new JLabel(" ");
    private final JButton saveOrderButton = new JButton();

    private TitledBorder presentersBorder;
    private TitledBorder giftsBorder;
    private TitledBorder concertBorder;
    private TitledBorder regularBorder;

    private Locale currentLocale = Locale.of("ru", "RU");

    private record GiftDisplayItem(String presenterId, GiftItem gift, String title) {
        @Override
        public String toString() {
            return title + " (" + String.format(Locale.US, "%.2f", gift.price()) + ")";
        }
    }

    public OlympiadAwardsApp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 620);
        setLocationRelativeTo(null);

        localeComboBox = new JComboBox<>(new LocaleOption[]{
                new LocaleOption(Locale.of("ru", "RU"), "Русский (ru, RU)"),
                new LocaleOption(Locale.of("en", "US"), "English (en, US)"),
                new LocaleOption(Locale.of("de", "DE"), "Deutsch (de, DE)")
        });

        initUi();
        bindEvents();
        refreshAvailableGifts();
        applyLocalization();
        updateDateTimeLabels();
        recalculateCost();

        Timer clockTimer = new Timer(1000, e -> updateDateTimeLabels());
        clockTimer.start();
    }

    private static Map<String, PresenterSpec> createPresenters() {
        Map<String, PresenterSpec> map = new LinkedHashMap<>();
        map.put("presenter_rector", new PresenterSpec(
                "presenter_rector",
                120.0,
                List.of(
                        new GiftItem("gift_medal_gold", 45.0),
                        new GiftItem("gift_diploma_frame", 20.0),
                        new GiftItem("gift_monograph", 35.0)
                )
        ));
        map.put("presenter_sponsor", new PresenterSpec(
                "presenter_sponsor",
                95.0,
                List.of(
                        new GiftItem("gift_laptop_stand", 55.0),
                        new GiftItem("gift_wireless_mouse", 30.0),
                        new GiftItem("gift_gift_voucher", 80.0),
                        new GiftItem("gift_thermal_mug", 18.0)
                )
        ));
        map.put("presenter_host", new PresenterSpec(
                "presenter_host",
                60.0,
                List.of(
                        new GiftItem("gift_badge_set", 12.0),
                        new GiftItem("gift_notebook", 15.0)
                )
        ));
        return map;
    }

    private static Map<String, Map<String, String>> createTranslations() {
        Map<String, Map<String, String>> map = new LinkedHashMap<>();

        Map<String, String> ru = new LinkedHashMap<>();
        ru.put("window_title", "Вычисление затрат на награждение победителей олимпиады");
        ru.put("locale_label", "Язык и страна:");
        ru.put("presenters_title", "1. Поздравители");
        ru.put("gifts_title", "2. Подарки (множественный выбор)");
        ru.put("concert_title", "3. Концерт");
        ru.put("regular_title", "4. Постоянный клиент (скидка 10%)");
        ru.put("yes", "Да");
        ru.put("no", "Нет");
        ru.put("concert_cost", "Стоимость концерта");
        ru.put("total_cost", "Итоговая стоимость: %.2f");
        ru.put("discount_applied", "Скидка 10% применена");
        ru.put("no_discount", "Скидка не применяется");
        ru.put("save_button", "Сохранить заказ в файл");
        ru.put("saved_ok", "Заказ сохранён в файл: %s");
        ru.put("saved_err", "Ошибка записи в файл: %s");
        ru.put("long_dt_prefix", "Полная дата и время: ");
        ru.put("short_dt_prefix", "Краткая дата и время: ");
        ru.put("order_header", "ЗАКАЗ НА НАГРАЖДЕНИЕ ПОБЕДИТЕЛЕЙ ОЛИМПИАДЫ");
        ru.put("order_presenters", "Выбранные поздравители:");
        ru.put("order_gifts", "Выбранные подарки:");
        ru.put("order_concert", "Концерт: %s");
        ru.put("order_regular", "Постоянный клиент: %s");
        ru.put("order_subtotal", "Сумма до скидки: %.2f");
        ru.put("order_discount", "Скидка: %.2f");
        ru.put("order_total", "Итого к оплате: %.2f");
        ru.put("none", "Не выбрано");
        ru.put("presenter_rector", "Ректор университета");
        ru.put("presenter_sponsor", "Генеральный спонсор");
        ru.put("presenter_host", "Ведущий церемонии");
        ru.put("gift_medal_gold", "Памятная медаль");
        ru.put("gift_diploma_frame", "Рамка для диплома");
        ru.put("gift_monograph", "Сборник научных трудов");
        ru.put("gift_laptop_stand", "Подставка для ноутбука");
        ru.put("gift_wireless_mouse", "Беспроводная мышь");
        ru.put("gift_gift_voucher", "Подарочный сертификат");
        ru.put("gift_thermal_mug", "Термокружка");
        ru.put("gift_badge_set", "Набор значков");
        ru.put("gift_notebook", "Ежедневник участника");
        map.put("ru", ru);

        Map<String, String> en = new LinkedHashMap<>();
        en.put("window_title", "Olympiad Winners Award Cost Calculator");
        en.put("locale_label", "Language and Country:");
        en.put("presenters_title", "1. Presenters");
        en.put("gifts_title", "2. Gifts (Multiple Selection)");
        en.put("concert_title", "3. Concert");
        en.put("regular_title", "4. Regular Client (10% Discount)");
        en.put("yes", "Yes");
        en.put("no", "No");
        en.put("concert_cost", "Concert fee");
        en.put("total_cost", "Total Cost: %.2f");
        en.put("discount_applied", "10% discount applied");
        en.put("no_discount", "No discount applied");
        en.put("save_button", "Save Order to File");
        en.put("saved_ok", "Order saved to file: %s");
        en.put("saved_err", "File write error: %s");
        en.put("long_dt_prefix", "Full Date & Time: ");
        en.put("short_dt_prefix", "Short Date & Time: ");
        en.put("order_header", "OLYMPIAD WINNERS AWARD ORDER");
        en.put("order_presenters", "Selected Presenters:");
        en.put("order_gifts", "Selected Gifts:");
        en.put("order_concert", "Concert: %s");
        en.put("order_regular", "Regular Client: %s");
        en.put("order_subtotal", "Subtotal: %.2f");
        en.put("order_discount", "Discount: %.2f");
        en.put("order_total", "Total Due: %.2f");
        en.put("none", "None");
        en.put("presenter_rector", "University Rector");
        en.put("presenter_sponsor", "General Sponsor");
        en.put("presenter_host", "Ceremony Host");
        en.put("gift_medal_gold", "Commemorative Medal");
        en.put("gift_diploma_frame", "Diploma Frame");
        en.put("gift_monograph", "Proceedings Volume");
        en.put("gift_laptop_stand", "Laptop Stand");
        en.put("gift_wireless_mouse", "Wireless Mouse");
        en.put("gift_gift_voucher", "Gift Voucher");
        en.put("gift_thermal_mug", "Travel Mug");
        en.put("gift_badge_set", "Badge Set");
        en.put("gift_notebook", "Participant Notebook");
        map.put("en", en);

        Map<String, String> de = new LinkedHashMap<>();
        de.put("window_title", "Kostenrechner fuer die Auszeichnung der Olympiadesieger");
        de.put("locale_label", "Sprache und Land:");
        de.put("presenters_title", "1. Ueberbringer");
        de.put("gifts_title", "2. Geschenke (Mehrfachauswahl)");
        de.put("concert_title", "3. Konzert");
        de.put("regular_title", "4. Stammkunde (10% Rabatt)");
        de.put("yes", "Ja");
        de.put("no", "Nein");
        de.put("concert_cost", "Konzertgebuehr");
        de.put("total_cost", "Gesamtkosten: %.2f");
        de.put("discount_applied", "10% Rabatt angewendet");
        de.put("no_discount", "Kein Rabatt");
        de.put("save_button", "Bestellung in Datei speichern");
        de.put("saved_ok", "Bestellung gespeichert in: %s");
        de.put("saved_err", "Dateifehler: %s");
        de.put("long_dt_prefix", "Ausfuehrliches Datum und Zeit: ");
        de.put("short_dt_prefix", "Kurzes Datum und Zeit: ");
        de.put("order_header", "BESTELLUNG ZUR SIEGEREHRUNG DER OLYMPIADE");
        de.put("order_presenters", "Gewaehlte Ueberbringer:");
        de.put("order_gifts", "Gewaehlte Geschenke:");
        de.put("order_concert", "Konzert: %s");
        de.put("order_regular", "Stammkunde: %s");
        de.put("order_subtotal", "Zwischensumme: %.2f");
        de.put("order_discount", "Rabatt: %.2f");
        de.put("order_total", "Endbetrag: %.2f");
        de.put("none", "Keine Auswahl");
        de.put("presenter_rector", "Universitaetsrektor");
        de.put("presenter_sponsor", "Hauptsponsor");
        de.put("presenter_host", "Moderation");
        de.put("gift_medal_gold", "Gedenkmedaille");
        de.put("gift_diploma_frame", "Urkundenrahmen");
        de.put("gift_monograph", "Tagungsband");
        de.put("gift_laptop_stand", "Laptopstaender");
        de.put("gift_wireless_mouse", "Funkmaus");
        de.put("gift_gift_voucher", "Geschenkgutschein");
        de.put("gift_thermal_mug", "Thermobecher");
        de.put("gift_badge_set", "Abzeichen-Set");
        de.put("gift_notebook", "Teilnehmerheft");
        map.put("de", de);

        return map;
    }

    private String tr(String key) {
        Map<String, String> langMap = TRANSLATIONS.getOrDefault(currentLocale.getLanguage(), TRANSLATIONS.get("ru"));
        return langMap.getOrDefault(key, key);
    }

    private void initUi() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel topPanel = new JPanel(new BorderLayout(8, 6));
        JPanel localeBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        localeBar.add(localeTitleLabel);
        localeBar.add(localeComboBox);

        JPanel clockPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        clockPanel.add(longDateTimeLabel);
        clockPanel.add(shortDateTimeLabel);

        topPanel.add(localeBar, BorderLayout.NORTH);
        topPanel.add(clockPanel, BorderLayout.SOUTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 12, 0));

        JPanel presentersPanel = new JPanel(new GridLayout(0, 1, 4, 4));
        presentersBorder = BorderFactory.createTitledBorder("");
        presentersPanel.setBorder(presentersBorder);
        for (String id : PRESENTERS.keySet()) {
            JCheckBox cb = new JCheckBox();
            presenterCheckBoxes.put(id, cb);
            presentersPanel.add(cb);
        }

        JPanel giftsPanel = new JPanel(new BorderLayout(4, 4));
        giftsBorder = BorderFactory.createTitledBorder("");
        giftsPanel.setBorder(giftsBorder);
        giftList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        giftsPanel.add(new JScrollPane(giftList), BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 8, 8));

        ButtonGroup concertGroup = new ButtonGroup();
        concertGroup.add(concertYesRadio);
        concertGroup.add(concertNoRadio);
        concertNoRadio.setSelected(true);

        JPanel concertBox = new JPanel(new GridLayout(2, 1));
        concertBorder = BorderFactory.createTitledBorder("");
        concertBox.setBorder(concertBorder);
        concertBox.add(concertYesRadio);
        concertBox.add(concertNoRadio);

        ButtonGroup regularGroup = new ButtonGroup();
        regularGroup.add(regularYesRadio);
        regularGroup.add(regularNoRadio);
        regularNoRadio.setSelected(true);

        JPanel regularBox = new JPanel(new GridLayout(2, 1));
        regularBorder = BorderFactory.createTitledBorder("");
        regularBox.setBorder(regularBorder);
        regularBox.add(regularYesRadio);
        regularBox.add(regularNoRadio);

        optionsPanel.add(concertBox);
        optionsPanel.add(regularBox);

        centerPanel.add(presentersPanel);
        centerPanel.add(giftsPanel);
        centerPanel.add(optionsPanel);

        JPanel bottomPanel = new JPanel(new BorderLayout(8, 8));
        totalCostLabel.setFont(totalCostLabel.getFont().deriveFont(Font.BOLD, 16f));

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        saveOrderButton.setPreferredSize(new Dimension(240, 32));
        actionRow.add(saveOrderButton);
        actionRow.add(statusLabel);

        bottomPanel.add(totalCostLabel, BorderLayout.NORTH);
        bottomPanel.add(actionRow, BorderLayout.SOUTH);

        root.add(topPanel, BorderLayout.NORTH);
        root.add(centerPanel, BorderLayout.CENTER);
        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void bindEvents() {
        localeComboBox.addActionListener(e -> {
            LocaleOption selected = (LocaleOption) localeComboBox.getSelectedItem();
            if (selected != null) {
                currentLocale = selected.locale();
                applyLocalization();
                refreshAvailableGifts();
                updateDateTimeLabels();
                recalculateCost();
            }
        });

        ActionListener recalcListener = e -> {
            if (e.getSource() instanceof JCheckBox) {
                refreshAvailableGifts();
            }
            recalculateCost();
        };

        for (JCheckBox cb : presenterCheckBoxes.values()) {
            cb.addActionListener(recalcListener);
        }

        giftList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                recalculateCost();
            }
        });

        concertYesRadio.addActionListener(recalcListener);
        concertNoRadio.addActionListener(recalcListener);
        regularYesRadio.addActionListener(recalcListener);
        regularNoRadio.addActionListener(recalcListener);

        saveOrderButton.addActionListener(e -> saveOrderToFile());
    }

    private void refreshAvailableGifts() {
        List<String> previouslySelectedGiftIds = new ArrayList<>();
        for (GiftDisplayItem item : giftList.getSelectedValuesList()) {
            previouslySelectedGiftIds.add(item.gift().id());
        }

        giftListModel.clear();
        List<Integer> indicesToSelect = new ArrayList<>();

        for (Map.Entry<String, JCheckBox> entry : presenterCheckBoxes.entrySet()) {
            if (!entry.getValue().isSelected()) {
                continue;
            }
            PresenterSpec spec = PRESENTERS.get(entry.getKey());
            if (spec == null) {
                continue;
            }
            for (GiftItem gift : spec.gifts()) {
                int idx = giftListModel.getSize();
                giftListModel.addElement(new GiftDisplayItem(entry.getKey(), gift, tr(gift.id())));
                if (previouslySelectedGiftIds.contains(gift.id())) {
                    indicesToSelect.add(idx);
                }
            }
        }

        if (!indicesToSelect.isEmpty()) {
            int[] arr = indicesToSelect.stream().mapToInt(Integer::intValue).toArray();
            giftList.setSelectedIndices(arr);
        }
    }

    private void applyLocalization() {
        setTitle(tr("window_title"));
        localeTitleLabel.setText(tr("locale_label"));
        presentersTitleLabel.setText(tr("presenters_title"));
        giftsTitleLabel.setText(tr("gifts_title"));
        concertTitleLabel.setText(tr("concert_title"));
        regularTitleLabel.setText(tr("regular_title"));

        concertYesRadio.setText(tr("yes"));
        concertNoRadio.setText(tr("no"));
        regularYesRadio.setText(tr("yes"));
        regularNoRadio.setText(tr("no"));

        for (Map.Entry<String, JCheckBox> entry : presenterCheckBoxes.entrySet()) {
            String id = entry.getKey();
            PresenterSpec spec = PRESENTERS.get(id);
            entry.getValue().setText(tr(id) + " (" + String.format(Locale.US, "%.2f", spec.fee()) + ")");
        }

        saveOrderButton.setText(tr("save_button"));

        presentersBorder.setTitle(tr("presenters_title"));
        giftsBorder.setTitle(tr("gifts_title"));
        concertBorder.setTitle(tr("concert_title"));
        regularBorder.setTitle(tr("regular_title"));

        repaint();
    }

    private void updateDateTimeLabels() {
        ZonedDateTime now = ZonedDateTime.now();
        DateTimeFormatter fullFmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM).withLocale(currentLocale);
        DateTimeFormatter shortFmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(currentLocale);

        longDateTimeLabel.setText(tr("long_dt_prefix") + now.format(fullFmt));
        shortDateTimeLabel.setText(tr("short_dt_prefix") + now.format(shortFmt));
    }

    private double calculateSubtotal() {
        double sum = 0.0;
        for (Map.Entry<String, JCheckBox> entry : presenterCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                sum += PRESENTERS.get(entry.getKey()).fee();
            }
        }
        for (GiftDisplayItem giftItem : giftList.getSelectedValuesList()) {
            sum += giftItem.gift().price();
        }
        if (concertYesRadio.isSelected()) {
            sum += 250.0;
        }
        return sum;
    }

    private double calculateTotal() {
        double subtotal = calculateSubtotal();
        if (regularYesRadio.isSelected()) {
            return subtotal * (1.0 - REGULAR_CLIENT_DISCOUNT);
        }
        return subtotal;
    }

    private void recalculateCost() {
        double total = calculateTotal();
        String discountNote = regularYesRadio.isSelected() ? tr("discount_applied") : tr("no_discount");
        String concertNote = concertYesRadio.isSelected() ? " (" + tr("concert_cost") + ": 250.00)" : "";
        totalCostLabel.setText(String.format(currentLocale, tr("total_cost"), total) + concertNote + " - " + discountNote);
    }

    private void saveOrderToFile() {
        double subtotal = calculateSubtotal();
        double discountAmount = regularYesRadio.isSelected() ? subtotal * REGULAR_CLIENT_DISCOUNT : 0.0;
        double total = subtotal - discountAmount;

        List<String> lines = new ArrayList<>();
        lines.add(tr("order_header"));
        lines.add(longDateTimeLabel.getText());
        lines.add("");
        lines.add(tr("order_presenters"));

        boolean anyPresenter = false;
        for (Map.Entry<String, JCheckBox> entry : presenterCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                anyPresenter = true;
                double fee = PRESENTERS.get(entry.getKey()).fee();
                lines.add(" - " + tr(entry.getKey()) + ": " + String.format(currentLocale, "%.2f", fee));
            }
        }
        if (!anyPresenter) {
            lines.add(" - " + tr("none"));
        }

        lines.add("");
        lines.add(tr("order_gifts"));
        List<GiftDisplayItem> selectedGifts = giftList.getSelectedValuesList();
        if (selectedGifts.isEmpty()) {
            lines.add(" - " + tr("none"));
        } else {
            for (GiftDisplayItem item : selectedGifts) {
                lines.add(" - " + item.title() + " [" + tr(item.presenterId()) + "]: "
                        + String.format(currentLocale, "%.2f", item.gift().price()));
            }
        }

        lines.add("");
        lines.add(String.format(currentLocale, tr("order_concert"), concertYesRadio.isSelected() ? tr("yes") : tr("no")));
        lines.add(String.format(currentLocale, tr("order_regular"), regularYesRadio.isSelected() ? tr("yes") : tr("no")));
        lines.add(String.format(currentLocale, tr("order_subtotal"), subtotal));
        lines.add(String.format(currentLocale, tr("order_discount"), discountAmount));
        lines.add(String.format(currentLocale, tr("order_total"), total));

        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(OUTPUT_FILE), StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            statusLabel.setText(String.format(currentLocale, tr("saved_ok"), OUTPUT_FILE));
        } catch (IOException ex) {
            statusLabel.setText(String.format(currentLocale, tr("saved_err"), ex.getMessage()));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OlympiadAwardsApp().setVisible(true));
    }
}