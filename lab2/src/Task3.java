public class Task3 {

    public static void run(int[][] matrix, int n, int m) {
        System.out.println("\nTask 3");
        printAllEvenColumns(matrix, n, m);
        printColumnsWithNegativeDiag(matrix, n, m);
    }

    private static void printAllEvenColumns(int[][] matrix, int n, int m) {
        System.out.println("Columns with all even elements:");
        boolean found = false;
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
                found = true;
            }
        }
        System.out.println(found ? "" : "no such columns");
    }

    private static void printColumnsWithNegativeDiag(int[][] matrix, int n, int m) {
        System.out.println("Columns with negative element on main diagonal:");
        boolean found = false;
        for (int j = 0; j < m; j++) {
            if (j < n && matrix[j][j] < 0) {
                int sum = 0;
                for (int i = 0; i < n; i++) {
                    sum += matrix[i][j];
                }
                System.out.println("Column " + (j + 1) + ": sum = " + sum);
                found = true;
            }
        }
        if (!found) {
            System.out.println("no such columns");
        }
    }
}