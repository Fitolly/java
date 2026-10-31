import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        MatrixInOut io = new MatrixInOut();
        try {
            int n = io.readInt("Enter number of rows n: ");
            int m = io.readInt("Enter number of columns m: ");

            int[][] matrix = io.readMatrix(n, m);

            System.out.println("\nSource matrix:");
            io.printMatrix(matrix);

            Task3.run(matrix, n, m);
            Task17.run(matrix, n, m);
            Task21.run(matrix, n, m);

        } catch (IOException e) {
            System.out.println("Input error: " + e.getMessage());
        }
    }
}