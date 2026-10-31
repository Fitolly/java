public class Task17 {

    public static void run(int[][] matrix, int n, int m) {
        System.out.println("\nTask 17");
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
    }
}