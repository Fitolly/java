import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        TextInOut io = new TextInOut();
        try {
            String text = io.readTextUntilEmptyLine();

            String corrected = Task3.correct(text);

            io.printText("\nOriginal text", text);
            io.printText("\nCorrected text", corrected);

        } catch (IOException e) {
            System.out.println(" error " + e.getMessage());
        }
    }
}