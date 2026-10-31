import java.util.StringTokenizer;

public class Task3 {

    private static final String DELIMITERS = " \t\n\r.,;:!?-\"()[]{}";

    public static String correct(String text) {
        StringTokenizer st = new StringTokenizer(text, DELIMITERS, true);
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
            if (chars[i] == 'Р' || chars[i] == 'р') {
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