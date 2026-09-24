import java.io.*;
import java.util.StringTokenizer;

public class StringCorrection {

    public static void main(String[] args) {
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);

            System.out.println("Enter text:");
            String text = br.readLine();

            String correctedText = correctText(text);

            System.out.println("\nOriginal text:");
            System.out.println(text);
            System.out.println("\nCorrected text:");
            System.out.println(correctedText);

        } catch (IOException e) {
            System.out.println("Keyboard reading error");
        }
    }

    public static String correctText(String text) {
        StringTokenizer st = new StringTokenizer(text, " \t\n\r.,;:!?-\"()[]{}", true);
        StringBuilder result = new StringBuilder();

        while (st.hasMoreTokens()) {
            String token = st.nextToken();

            if (isWord(token)) {
                result.append(correctWord(token));
            } else {
                result.append(token);
            }
        }

        return result.toString();
    }

    private static boolean isWord(String token) {
        for (int i = 0; i < token.length(); i++) {
            if (Character.isLetter(token.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static String correctWord(String word) {
        char[] chars = word.toCharArray();

        for (int i = 0; i < chars.length - 1; i++) {
            if ((chars[i] == 'Р' || chars[i] == 'р') && i < chars.length - 1) {
                if (chars[i + 1] == 'А') {
                    chars[i + 1] = 'О';
                } else if (chars[i + 1] == 'а') {
                    chars[i + 1] = 'о';
                }
            }
        }

        return new String(chars);
    }
}