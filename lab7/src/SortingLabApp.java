import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Random;

public class SortingLabApp {

    private static final int DATA_SIZE = 60;
    private static final int MIN_VALUE = 10;
    private static final int MAX_VALUE = 500;
    private static final int DELAY_MS = 18;
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public static void main(String[] args) {
        int[] baseData = generateDataset(DATA_SIZE, MIN_VALUE, MAX_VALUE);
        int maxValue = Arrays.stream(baseData).max().orElse(MAX_VALUE);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int windowWidth = Math.max(320, screen.width / 3);
        int windowHeight = Math.min(720, (int) (screen.height * 0.78));

        SwingUtilities.invokeLater(() -> {
            SortVisualizerFrame quickFrame = new SortVisualizerFrame(
                    "QuickSort",
                    Arrays.copyOf(baseData, baseData.length),
                    maxValue,
                    new Color(59, 130, 246),
                    0,
                    windowWidth,
                    windowHeight
            );

            SortVisualizerFrame mergeFrame = new SortVisualizerFrame(
                    "Merge Sort",
                    Arrays.copyOf(baseData, baseData.length),
                    maxValue,
                    new Color(16, 185, 129),
                    windowWidth,
                    windowWidth,
                    windowHeight
            );

            SortVisualizerFrame insertionFrame = new SortVisualizerFrame(
                    "Insertion Sort",
                    Arrays.copyOf(baseData, baseData.length),
                    maxValue,
                    new Color(245, 158, 11),
                    windowWidth * 2,
                    windowWidth,
                    windowHeight
            );

            quickFrame.setVisible(true);
            mergeFrame.setVisible(true);
            insertionFrame.setVisible(true);

            Thread quickThread = new Thread(() -> runQuickSort(quickFrame), "QuickSort-Thread");
            Thread mergeThread = new Thread(() -> runMergeSort(mergeFrame), "MergeSort-Thread");
            Thread insertionThread = new Thread(() -> runInsertionSort(insertionFrame), "InsertionSort-Thread");

            quickThread.start();
            mergeThread.start();
            insertionThread.start();
        });
    }

    private static int[] generateDataset(int size, int min, int max) {
        Random random = new Random();
        int[] data = new int[size];
        for (int i = 0; i < size; i++) {
            data[i] = min + random.nextInt(max - min + 1);
        }
        return data;
    }

    private static void runQuickSort(SortVisualizerFrame frame) {
        frame.markStarted();
        long startNano = System.nanoTime();
        int[] arr = frame.getArray();
        quickSort(arr, 0, arr.length - 1, frame);
        long endNano = System.nanoTime();
        frame.markFinished(startNano, endNano);
    }

    private static void quickSort(int[] arr, int low, int high, SortVisualizerFrame frame) {
        if (low < high) {
            int pivotIndex = partition(arr, low, high, frame);
            quickSort(arr, low, pivotIndex - 1, frame);
            quickSort(arr, pivotIndex + 1, high, frame);
        }
    }

    private static int partition(int[] arr, int low, int high, SortVisualizerFrame frame) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                if (i != j) {
                    int temp = arr[i];
                    arr[i] = arr[j];
                    arr[j] = temp;
                    frame.notifyStep(i, j);
                }
            }
        }
        if (i + 1 != high) {
            int temp = arr[i + 1];
            arr[i + 1] = arr[high];
            arr[high] = temp;
            frame.notifyStep(i + 1, high);
        }
        return i + 1;
    }

    private static void runMergeSort(SortVisualizerFrame frame) {
        frame.markStarted();
        long startNano = System.nanoTime();
        int[] arr = frame.getArray();
        int[] temp = new int[arr.length];
        mergeSort(arr, temp, 0, arr.length - 1, frame);
        long endNano = System.nanoTime();
        frame.markFinished(startNano, endNano);
    }

    private static void mergeSort(int[] arr, int[] temp, int left, int right, SortVisualizerFrame frame) {
        if (left >= right) {
            return;
        }
        int mid = left + (right - left) / 2;
        mergeSort(arr, temp, left, mid, frame);
        mergeSort(arr, temp, mid + 1, right, frame);
        merge(arr, temp, left, mid, right, frame);
    }

    private static void merge(int[] arr, int[] temp, int left, int mid, int right, SortVisualizerFrame frame) {
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (temp[i] <= temp[j]) {
                arr[k] = temp[i++];
            } else {
                arr[k] = temp[j++];
            }
            frame.notifyStep(k, -1);
            k++;
        }

        while (i <= mid) {
            arr[k] = temp[i++];
            frame.notifyStep(k, -1);
            k++;
        }

        while (j <= right) {
            arr[k] = temp[j++];
            frame.notifyStep(k, -1);
            k++;
        }
    }

    private static void runInsertionSort(SortVisualizerFrame frame) {
        frame.markStarted();
        long startNano = System.nanoTime();
        int[] arr = frame.getArray();

        for (int i = 1; i < arr.length; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j];
                frame.notifyStep(j, j + 1);
                j--;
            }
            arr[j + 1] = key;
            frame.notifyStep(j + 1, i);
        }

        long endNano = System.nanoTime();
        frame.markFinished(startNano, endNano);
    }

    private static class SortVisualizerFrame extends JFrame {
        private final int[] array;
        private final int maxValue;
        private final Color barColor;
        private final BarChartPanel chartPanel;
        private final JLabel statsLabel;

        private volatile int activeIndexA = -1;
        private volatile int activeIndexB = -1;
        private volatile long totalDelayMs = 0;

        public SortVisualizerFrame(
                String algorithmName,
                int[] array,
                int maxValue,
                Color barColor,
                int xPos,
                int width,
                int height
        ) {
            super(algorithmName);
            this.array = array;
            this.maxValue = maxValue;
            this.barColor = barColor;

            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setBounds(xPos, 20, width, height);

            JPanel root = new JPanel();
            root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
            root.setBackground(Color.WHITE);

            this.chartPanel = new BarChartPanel();
            this.statsLabel = new JLabel("<html><b>Status:</b> pending start<br>Start: -<br>End: -<br>Net time: -</html>");
            this.statsLabel.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));

            root.add(chartPanel);
            root.add(statsLabel);
            setContentPane(root);
        }

        public int[] getArray() {
            return array;
        }

        public void markStarted() {
            String startStr = LocalTime.now().format(TIME_FMT);
            SwingUtilities.invokeLater(() -> statsLabel.setText(
                    "<html><b>Status:</b> sorting<br>"
                            + "Start: " + startStr + "<br>"
                            + "End: in progress<br>"
                            + "Net time: -</html>"
            ));
        }

        public void notifyStep(int idxA, int idxB) {
            activeIndexA = idxA;
            activeIndexB = idxB;
            chartPanel.repaint();

            try {
                Thread.sleep(DELAY_MS);
                totalDelayMs += DELAY_MS;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        public void markFinished(long startNano, long endNano) {
            activeIndexA = -1;
            activeIndexB = -1;
            chartPanel.repaint();

            String startStr = LocalTime.now().minusNanos(Math.max(0L, endNano - startNano)).format(TIME_FMT);
            String endStr = LocalTime.now().format(TIME_FMT);

            double totalElapsedMs = (endNano - startNano) / 1_000_000.0;
            double pureWorkMs = Math.max(0.0, totalElapsedMs - totalDelayMs);

            SwingUtilities.invokeLater(() -> statsLabel.setText(
                    String.format(
                            "<html><b>Status:</b> completed<br>"
                                    + "Start: %s<br>"
                                    + "End: %s<br>"
                                    + "Net time: %.3f ms (delays: %d ms)</html>",
                            startStr, endStr, pureWorkMs, totalDelayMs
                    )
            ));
        }

        private class BarChartPanel extends JPanel {
            public BarChartPanel() {
                setBackground(new Color(159, 206, 255));
                setPreferredSize(new Dimension(300, 500));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int panelWidth = getWidth();
                int panelHeight = getHeight();
                if (panelWidth <= 0 || panelHeight <= 0 || array.length == 0) {
                    return;
                }

                double barWidth = (double) panelWidth / array.length;
                for (int i = 0; i < array.length; i++) {
                    int barHeight = (int) Math.round(((double) array[i] / maxValue) * panelHeight);
                    int x = (int) Math.round(i * barWidth);
                    int w = Math.max(1, (int) Math.round((i + 1) * barWidth) - x - 1);
                    int y = panelHeight - barHeight;

                    if (i == activeIndexA || i == activeIndexB) {
                        g.setColor(new Color(80, 234, 22));
                    } else {
                        g.setColor(barColor);
                    }
                    g.fillRect(x, y, w, barHeight);
                }
            }
        }
    }
}