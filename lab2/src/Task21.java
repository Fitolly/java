public class Task21 {

    public static void run(int[][] matrix, int n, int m) {
        System.out.println("\nTask 21");

        Integer[] colOrder = new Integer[m];
        for (int j = 0; j < m; j++) colOrder[j] = j;

        for (int a = 1; a < m; a++) {
            Integer key = colOrder[a];
            int keyMin = columnMin(matrix, key, n);
            int b = a - 1;
            while (b >= 0 && columnMin(matrix, colOrder[b], n) > keyMin) {
                colOrder[b + 1] = colOrder[b];
                b--;
            }
            colOrder[b + 1] = key;
        }

        int[][] sorted = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                sorted[i][j] = matrix[i][colOrder[j]];
            }
        }

        System.out.println("Matrix after sorting columns:");
        new MatrixInOut().printMatrix(sorted);
    }

    private static int columnMin(int[][] a, int j, int n) {
        int min = a[0][j];
        for (int i = 1; i < n; i++) {
            if (a[i][j] < min) min = a[i][j];
        }
        return min;
    }
}