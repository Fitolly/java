import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TextJustificationLab {

    private static final String INPUT_FILE = "input.txt";
    private static final String OUTPUT_FILE = "output.txt";
    private static final int DEFAULT_PARAGRAPH_INDENT = 4;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter target line width: ");
        int width = scanner.nextInt();

        if (width <= 0) {
            System.out.println("Width must be positive.");
            return;
        }

        TextFileManager fileManager = new TextFileManager(INPUT_FILE, OUTPUT_FILE);

        try {
            List<String> rawLines = fileManager.readLines();
            List<String> formattedLines = formatDocument(rawLines, width);
            fileManager.writeLines(formattedLines);
            System.out.println("Done. Result written to " + OUTPUT_FILE);
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }

    private static List<String> formatDocument(List<String> rawLines, int width) {
        List<String> result = new ArrayList<>();

        for (String rawLine : rawLines) {
            if (rawLine.trim().isEmpty()) {
                result.add("");
                continue;
            }

            String indent = extractIndent(rawLine);
            String normalizedBody = rawLine.trim().replaceAll("\\s+", " ");
            List<String> words = Arrays.asList(normalizedBody.split(" "));

            List<String> wrappedLines = wrapAndJustifyParagraph(words, indent, width);
            result.addAll(wrappedLines);
        }

        return result;
    }

    private static String extractIndent(String line) {
        int count = 0;
        while (count < line.length() && (line.charAt(count) == ' ' || line.charAt(count) == '\t')) {
            if (line.charAt(count) == '\t') {
                count += 4;
            } else {
                count++;
            }
        }
        int indentLength = count > 0 ? count : DEFAULT_PARAGRAPH_INDENT;
        return " ".repeat(indentLength);
    }

    private static List<String> wrapAndJustifyParagraph(List<String> words, String indent, int width) {
        List<String> paragraphLines = new ArrayList<>();
        List<String> currentWords = new ArrayList<>();
        int currentWordsLength = 0;
        boolean isFirstLine = true;

        for (String word : words) {
            int prefixLen = isFirstLine ? indent.length() : 0;
            int gapsIfAdded = currentWords.size();
            int nextLengthIfAdded = prefixLen + currentWordsLength + word.length() + gapsIfAdded;

            if (!currentWords.isEmpty() && nextLengthIfAdded > width) {
                paragraphLines.add(justifyLine(currentWords, currentWordsLength, isFirstLine ? indent : "", width, false));
                currentWords.clear();
                currentWordsLength = 0;
                isFirstLine = false;
            }

            currentWords.add(word);
            currentWordsLength += word.length();
        }

        if (!currentWords.isEmpty()) {
            paragraphLines.add(justifyLine(currentWords, currentWordsLength, isFirstLine ? indent : "", width, true));
        }

        return paragraphLines;
    }

    private static String justifyLine(
            List<String> words,
            int wordsLength,
            String leadingIndent,
            int width,
            boolean isLastLine
    ) {
        if (isLastLine || words.size() == 1) {
            StringBuilder sb = new StringBuilder(leadingIndent);
            for (int i = 0; i < words.size(); i++) {
                if (i > 0) {
                    sb.append(' ');
                }
                sb.append(words.get(i));
            }
            return sb.toString();
        }

        int gaps = words.size() - 1;
        int totalSpacesNeeded = Math.max(gaps, width - leadingIndent.length() - wordsLength);
        int baseSpaces = totalSpacesNeeded / gaps;
        int extraSpaces = totalSpacesNeeded % gaps;

        StringBuilder sb = new StringBuilder(leadingIndent);
        for (int i = 0; i < words.size(); i++) {
            sb.append(words.get(i));
            if (i < gaps) {
                int spacesToAdd = baseSpaces + (i < extraSpaces ? 1 : 0);
                sb.append(" ".repeat(spacesToAdd));
            }
        }
        return sb.toString();
    }
}