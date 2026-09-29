import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String inputFileName = "fin.txt";
        String outputFile1 = "fout1.txt";
        String outputFile2 = "fout2.txt";

        try {
            System.out.println("Reading input data from " + inputFileName + "...");
            List<GradeBook> gradeBooks = GradeBookManager.readGradeBooksFromFile(inputFileName);

            System.out.println("Writing honor students report to " + outputFile1 + "...");
            GradeBookManager.writeHonorStudentsToFile(gradeBooks, outputFile1);

            System.out.println("Writing passing students report to " + outputFile2 + "...");
            GradeBookManager.writePassingStudentsToFile(gradeBooks, outputFile2);

            System.out.println("Execution completed successfully.");

        } catch (IOException e) {
            System.err.println("File I/O error occurred: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Data format error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("Invalid state: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}