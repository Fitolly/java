import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class FileManager {

    public List<String> readFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public void writeFile(String filePath, List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (int i = 0; i < lines.size(); i++) {
                writer.write(lines.get(i));
                if (i < lines.size() - 1) {
                    writer.newLine();
                }
            }
        }
    }
}

public class Main {

    private static final String INPUT_FILE = "input.txt";
    private static final String OUTPUT_FILE = "output.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter line width: ");
        int width = scanner.nextInt();

        FileManager fileManager = new FileManager();

        try {
            List<String> inputLines = fileManager.readFile(INPUT_FILE);
            List<String> outputLines = new ArrayList<>();

            for (String rawLine : inputLines) {
                if (rawLine.trim().isEmpty()) {
                    outputLines.add("");
                    continue;
                }

                String indent = extractIndent(rawLine);
                String normalizedText = rawLine.trim().replaceAll("\\s+", " ");

                List<String> formattedLines = justifyParagraph(normalizedText, width, indent);
                outputLines.addAll(formattedLines);
            }

            fileManager.writeFile(OUTPUT_FILE, outputLines);
            System.out.println("Done!");

        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
        }
    }

    private static String extractIndent(String line) {
        StringBuilder indent = new StringBuilder();
        for (char ch : line.toCharArray()) {
            if (ch == ' ' || ch == '\t') {
                indent.append(ch);
            } else {
                break;
            }
        }
        return indent.toString();
    }

    private static List<String> justifyParagraph(String text, int width, String indent) {
        List<String> result = new ArrayList<>();
        String[] words = text.split(" ");
        List<String> currentLine = new ArrayList<>();
        int currentLength = indent.length();

        for (String word : words) {
            if (currentLength + word.length() + (currentLine.isEmpty() ? 0 : 1) > width) {
                result.add(formatLine(currentLine, width, indent));
                currentLine.clear();
                indent = "";
                currentLength = 0;
            }

            if (!currentLine.isEmpty()) {
                currentLength += 1;
            }
            currentLine.add(word);
            currentLength += word.length();
        }

        if (!currentLine.isEmpty()) {
            StringBuilder lastLine = new StringBuilder(indent);
            for (int i = 0; i < currentLine.size(); i++) {
                lastLine.append(currentLine.get(i));
                if (i < currentLine.size() - 1) {
                    lastLine.append(" ");
                }
            }
            result.add(lastLine.toString());
        }

        return result;
    }

    private static String formatLine(List<String> words, int width, String indent) {
        if (words.size() == 1) {
            return indent + words.get(0);
        }

        int totalWordsLength = 0;
        for (String word : words) {
            totalWordsLength += word.length();
        }

        int availableSpaces = width - indent.length() - totalWordsLength;
        int gaps = words.size() - 1;

        int spacesPerGap = availableSpaces / gaps;
        int extraSpaces = availableSpaces % gaps;

        StringBuilder sb = new StringBuilder(indent);
        for (int i = 0; i < words.size(); i++) {
            sb.append(words.get(i));
            if (i < gaps) {
                int spacesToAdd = spacesPerGap + (i < extraSpaces ? 1 : 0);
                for (int s = 0; s < spacesToAdd; s++) {
                    sb.append(" ");
                }
            }
        }

        return sb.toString();
    }
}