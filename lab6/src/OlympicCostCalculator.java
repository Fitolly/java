import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class OlympicCostCalculator extends JFrame {

    private static final double PRICE_CONGRATULATOR_1 = 500.0;
    private static final double PRICE_CONGRATULATOR_2 = 1000.0;
    private static final double PRICE_GIFT_1 = 300.0;
    private static final double PRICE_GIFT_2 = 150.0;
    private static final double PRICE_GIFT_3 = 50.0;
    private static final double PRICE_CONCERT = 1200.0;
    private static final double DISCOUNT_RATE = 0.10;

    private JLabel labelCongratulatorHeader;
    private JCheckBox cbCongratulator1, cbCongratulator2;

    private JLabel labelGiftHeader;
    private JCheckBox cbGift1, cbGift2, cbGift3;

    private JLabel labelConcertHeader;
    private JRadioButton rbConcertYes, rbConcertNo;
    private ButtonGroup concertGroup;

    private JLabel labelCustomerHeader;
    private JRadioButton rbCustomerYes, rbCustomerNo;
    private ButtonGroup customerGroup;

    private JLabel labelTotalCost;
    private JButton btnSaveFile;

    private JLabel labelLanguageHeader;
    private JComboBox<String> comboLocales;

    private JLabel labelDateShortHeader, labelDateShortValue;
    private JLabel labelDateLongHeader, labelDateLongValue;

    private ResourceBundle bundle;
    private Locale currentLocale;

    public OlympicCostCalculator() {
        currentLocale = new Locale("ru", "RU");
        bundle = ResourceBundle.getBundle("messages", currentLocale);

        setTitle(bundle.getString("title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(12, 2, 5, 5));

        initComponents();
        buildLayout();
        updateLocalization();

        pack();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        labelCongratulatorHeader = new JLabel();
        labelGiftHeader = new JLabel();
        labelConcertHeader = new JLabel();
        labelCustomerHeader = new JLabel();
        labelLanguageHeader = new JLabel();
        labelDateShortHeader = new JLabel();
        labelDateLongHeader = new JLabel();
        labelDateShortValue = new JLabel();
        labelDateLongValue = new JLabel();
        labelTotalCost = new JLabel();

        cbCongratulator1 = new JCheckBox();
        cbCongratulator2 = new JCheckBox();

        cbGift1 = new JCheckBox();
        cbGift2 = new JCheckBox();
        cbGift3 = new JCheckBox();

        rbConcertYes = new JRadioButton();
        rbConcertNo = new JRadioButton();
        rbConcertNo.setSelected(true);
        concertGroup = new ButtonGroup();
        concertGroup.add(rbConcertYes);
        concertGroup.add(rbConcertNo);

        rbCustomerYes = new JRadioButton();
        rbCustomerNo = new JRadioButton();
        rbCustomerNo.setSelected(true);
        customerGroup = new ButtonGroup();
        customerGroup.add(rbCustomerYes);
        customerGroup.add(rbCustomerNo);

        btnSaveFile = new JButton();

        String[] localesList = {
                "Russian (ru_RU)",
                "English (en_US)",
                "German (de_DE)"
        };
        comboLocales = new JComboBox<>(localesList);

        ActionListener calculateListener = e -> calculateCost();
        cbCongratulator1.addActionListener(calculateListener);
        cbCongratulator2.addActionListener(calculateListener);
        cbGift1.addActionListener(calculateListener);
        cbGift2.addActionListener(calculateListener);
        cbGift3.addActionListener(calculateListener);
        rbConcertYes.addActionListener(calculateListener);
        rbConcertNo.addActionListener(calculateListener);
        rbCustomerYes.addActionListener(calculateListener);
        rbCustomerNo.addActionListener(calculateListener);

        comboLocales.addActionListener(e -> {
            int selectedIndex = comboLocales.getSelectedIndex();
            switch (selectedIndex) {
                case 0:
                    currentLocale = new Locale("ru", "RU");
                    break;
                case 1:
                    currentLocale = new Locale("en", "US");
                    break;
                case 2:
                    currentLocale = new Locale("de", "DE");
                    break;
            }
            bundle = ResourceBundle.getBundle("messages", currentLocale);
            updateLocalization();
        });

        btnSaveFile.addActionListener(e -> saveOrderToFile());
    }

    private void buildLayout() {
        add(labelLanguageHeader);
        add(comboLocales);

        add(labelCongratulatorHeader);
        add(cbCongratulator1);
        add(new JLabel(""));
        add(cbCongratulator2);

        add(labelGiftHeader);
        add(cbGift1);
        add(new JLabel(""));
        add(cbGift2);
        add(new JLabel(""));
        add(cbGift3);

        add(labelConcertHeader);
        JPanel concertPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        concertPanel.add(rbConcertYes);
        concertPanel.add(rbConcertNo);
        add(concertPanel);

        add(labelCustomerHeader);
        JPanel customerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        customerPanel.add(rbCustomerYes);
        customerPanel.add(rbCustomerNo);
        add(customerPanel);

        add(labelDateShortHeader);
        add(labelDateShortValue);

        add(labelDateLongHeader);
        add(labelDateLongValue);

        add(labelTotalCost);
        add(btnSaveFile);
    }

    private void updateLocalization() {
        setTitle(bundle.getString("title"));
        labelLanguageHeader.setText(bundle.getString("locales"));

        labelCongratulatorHeader.setText(bundle.getString("congratulator"));
        cbCongratulator1.setText(bundle.getString("congratulator1"));
        cbCongratulator2.setText(bundle.getString("congratulator2"));

        labelGiftHeader.setText(bundle.getString("gift"));
        cbGift1.setText(bundle.getString("gift1"));
        cbGift2.setText(bundle.getString("gift2"));
        cbGift3.setText(bundle.getString("gift3"));

        labelConcertHeader.setText(bundle.getString("concert"));
        labelCustomerHeader.setText(bundle.getString("regularCustomer"));

        rbConcertYes.setText(bundle.getString("yes"));
        rbConcertNo.setText(bundle.getString("no"));
        rbCustomerYes.setText(bundle.getString("yes"));
        rbCustomerNo.setText(bundle.getString("no"));

        btnSaveFile.setText(bundle.getString("saveToFile"));

        labelDateShortHeader.setText(bundle.getString("dateShort"));
        labelDateLongHeader.setText(bundle.getString("dateLong"));

        Date now = new Date();

        DateFormat shortFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, currentLocale);
        labelDateShortValue.setText(shortFormat.format(now));

        DateFormat longFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, currentLocale);
        labelDateLongValue.setText(longFormat.format(now));

        calculateCost();
    }

    private double calculateCost() {
        double total = 0.0;

        if (cbCongratulator1.isSelected()) total += PRICE_CONGRATULATOR_1;
        if (cbCongratulator2.isSelected()) total += PRICE_CONGRATULATOR_2;

        if (cbGift1.isSelected()) total += PRICE_GIFT_1;
        if (cbGift2.isSelected()) total += PRICE_GIFT_2;
        if (cbGift3.isSelected()) total += PRICE_GIFT_3;

        if (rbConcertYes.isSelected()) total += PRICE_CONCERT;

        if (rbCustomerYes.isSelected()) {
            total -= total * DISCOUNT_RATE;
        }

        String costText = String.format("%.2f", total);
        labelTotalCost.setText(bundle.getString("totalCost").replaceAll("[0-9,.]+", costText));

        return total;
    }

    private void saveOrderToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("order.txt"))) {
            writer.println(bundle.getString("title"));
            writer.println(labelDateLongHeader.getText() + " " + labelDateLongValue.getText());
            writer.println();

            writer.println(labelCongratulatorHeader.getText());
            if (cbCongratulator1.isSelected()) writer.println(" - " + cbCongratulator1.getText());
            if (cbCongratulator2.isSelected()) writer.println(" - " + cbCongratulator2.getText());

            writer.println(labelGiftHeader.getText());
            if (cbGift1.isSelected()) writer.println(" - " + cbGift1.getText());
            if (cbGift2.isSelected()) writer.println(" - " + cbGift2.getText());
            if (cbGift3.isSelected()) writer.println(" - " + cbGift3.getText());

            writer.println(labelConcertHeader.getText() + " " + (rbConcertYes.isSelected() ? rbConcertYes.getText() : rbConcertNo.getText()));
            writer.println(labelCustomerHeader.getText() + " " + (rbCustomerYes.isSelected() ? rbCustomerYes.getText() : rbCustomerNo.getText()));

            writer.println();
            writer.println(labelTotalCost.getText());

            JOptionPane.showMessageDialog(this, bundle.getString("orderSaved"), "Info", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, bundle.getString("errorSave"), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OlympicCostCalculator app = new OlympicCostCalculator();
            app.setVisible(true);
        });
    }
}