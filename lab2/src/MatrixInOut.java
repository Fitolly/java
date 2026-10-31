import java.io.*;

public class MatrixInOut {

    private final BufferedReader br;

    public MatrixInOut() {
        this.br = new BufferedReader(new InputStreamReader(System.in));
    }

    public int readInt(String prompt) throws IOException {
        while (true) {
            System.out.print(prompt);
            String line = br.readLine();
            if (line == null) throw new IOException("End of input");
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                System.out.println("Not an integer, try again:");
            }
        }
    }

    public int[][] readMatrix(int n, int m) throws IOException {
        int[][] matrix = new int[n][m];
        System.out.println("Enter matrix elements:");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = readInt("[" + i + "][" + j + "] = ");
            }
        }
        return matrix;
    }

    public void printMatrix(int[][] a) {
        for (int[] row : a) {
            for (int v : row) {
                System.out.printf("%5d", v);
            }
            System.out.println();
        }
    }
}