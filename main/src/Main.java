import java.io.*;

public class Main {

    private static final String FILE_NAME = "text.txt";

    public static void main(String[] args) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(FILE_NAME)));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append('\n');
            }
            br.close();

            String processed = analyze(sb.toString());

            FileWriter fw = new FileWriter(FILE_NAME);
            fw.write(processed);
            fw.close();

            String[] lines = processed.split("\n");
            for (String out : lines) {
                System.out.println(out);
            }

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }

    public static String analyze(String text) {
        String vowels = "аеёиоуыэюяАЕЁИОУЫЭЮЯ";
        StringBuilder result = new StringBuilder();

        int i = 0;
        int length = text.length();

        while (i < length) {
            int sentenceEnd = findSentenceEnd(text, i);

            String sentence = text.substring(i, sentenceEnd);
            result.append(sentence);

            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                result.append(buildLine(trimmed, vowels));
                result.append('\n');
            }

            i = sentenceEnd;
        }

        return result.toString();
    }

    private static int findSentenceEnd(String text, int from) {
        String enders = ".!?\n";
        for (int i = from; i < text.length(); i++) {
            if (enders.indexOf(text.charAt(i)) != -1) {
                return i + 1;
            }
        }
        return text.length();
    }

    private static String buildLine(String sentence, String vowels) {
        int vowelsCount = 0;
        int consonantsCount = 0;

        for (char c : sentence.toCharArray()) {
            if (Character.isLetter(c)) {
                if (vowels.indexOf(c) != -1) {
                    vowelsCount++;
                } else {
                    consonantsCount++;
                }
            }
        }

        if (vowelsCount > consonantsCount) {
            return "Vowels more: " + vowelsCount
                    + " and consonants are " + consonantsCount;
        } else if (consonantsCount > vowelsCount) {
            return "Consonants more: " + consonantsCount
                    + " and vowels are " + vowelsCount;
        } else {
            return "Vowels and consonants are equal: "
                    + vowelsCount + " each are";
        }
    }
}