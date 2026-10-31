import java.io.*;

public class TextInOut {

    private final BufferedReader br;

    public TextInOut() {
        this.br = new BufferedReader(new InputStreamReader(System.in));
    }

    public String readTextUntilEmptyLine() throws IOException {
        System.out.println("Enter text ");
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    public void printText(String title, String text) {
        System.out.println(title);
        System.out.println(text);
    }
}