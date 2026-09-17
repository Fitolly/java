import java.io.*;

public class Lab2Matrix {

    private static BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in));

    public static void main(String[] args) {
        try {
            System.out.println("Enter number of rows n:");
            int n = readInt();

            System.out.println("Enter number of columns m:");
            int m = readInt();

            int[][] matrix = new int[n][m];
            System.out.println("Enter matrix elements:");
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.print("[" + i + "][" + j + "] = ");
                    matrix[i][j] = readInt();
                }
            }

            System.out.println("\nSource matrix:");
            printMatrix(matrix);

            System.out.println("\n===== Task 3 =====");

            System.out.println("Columns with all even elements:");
            boolean foundEvenCol = false;
            for (int j = 0; j < m; j++) {
                boolean allEven = true;
                for (int i = 0; i < n; i++) {
                    if (matrix[i][j] % 2 != 0) {
                        allEven = false;
                        break;
                    }
                }
                if (allEven) {
                    System.out.print((j + 1) + " ");
                    foundEvenCol = true;
                }
            }
            if (!foundEvenCol) {
                System.out.println("no such columns");
            } else {
                System.out.println();
            }

            System.out.println("Columns with negative element on main diagonal:");
            boolean foundNegDiag = false;
            for (int j = 0; j < m; j++) {
                if (j < n && matrix[j][j] < 0) {
                    int sum = 0;
                    for (int i = 0; i < n; i++) {
                        sum += matrix[i][j];
                    }
                    System.out.println("Column " + (j + 1) + ": sum = " + sum);
                    foundNegDiag = true;
                }
            }
            if (!foundNegDiag) {
                System.out.println("no such columns");
            }

            System.out.println("\n===== Task 17 =====");
            System.out.println("Sums of diagonals parallel to main:");

            int maxDiagSum = Integer.MIN_VALUE;

            for (int d = -(m - 1); d <= n - 1; d++) {
                int sum = 0;
                for (int i = 0; i < n; i++) {
                    int j = i - d;
                    if (j >= 0 && j < m) {
                        sum += matrix[i][j];
                    }
                }
                System.out.println("Diagonal d = " + d + ": sum = " + sum);
                if (sum > maxDiagSum) {
                    maxDiagSum = sum;
                }
            }
            System.out.println("Maximum among sums = " + maxDiagSum);

            System.out.println("\n===== Task 31 =====");

            Integer[] colOrder = new Integer[m];
            for (int j = 0; j < m; j++) colOrder[j] = j;

            for (int a = 1; a < m; a++) {
                Integer key = colOrder[a];
                int keyMin = columnMin(matrix, key, n);
                int b = a - 1;
                while (b >= 0 &&
                        columnMin(matrix, colOrder[b], n) > keyMin) {
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
            printMatrix(sorted);

        } catch (IOException e) {
            System.out.println("Input error");
        }
    }

    private static int readInt() throws IOException {
        while (true) {
            String line = br.readLine();
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Not an integer, try again:");
            }
        }
    }

    private static int columnMin(int[][] a, int j, int n) {
        int min = a[0][j];
        for (int i = 1; i < n; i++) {
            if (a[i][j] < min) min = a[i][j];
        }
        return min;
    }

    private static void printMatrix(int[][] a) {
        for (int[] row : a) {
            for (int v : row) {
                System.out.printf("%5d", v);
            }
            System.out.println();
        }
    }
}