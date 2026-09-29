import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GradeBookManager {

    public static List<GradeBook> readGradeBooksFromFile(String inputFileName) throws IOException {
        List<GradeBook> gradeBooks = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            GradeBook currentGradeBook = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] tokens = line.split(";");
                if (tokens[0].equalsIgnoreCase("STUDENT")) {
                    if (tokens.length < 4) {
                        throw new IllegalArgumentException("Invalid STUDENT format in input file.");
                    }
                    String name = tokens[1].trim();
                    int course = Integer.parseInt(tokens[2].trim());
                    String group = tokens[3].trim();
                    currentGradeBook = new GradeBook(name, course, group);
                    gradeBooks.add(currentGradeBook);
                } else if (tokens[0].equalsIgnoreCase("RECORD")) {
                    if (currentGradeBook == null) {
                        throw new IllegalStateException("RECORD line found before STUDENT line.");
                    }
                    if (tokens.length < 5) {
                        throw new IllegalArgumentException("Invalid RECORD format in input file.");
                    }
                    int sessionNum = Integer.parseInt(tokens[1].trim());
                    String subject = tokens[2].trim();
                    int grade = Integer.parseInt(tokens[3].trim());
                    boolean isExam = Boolean.parseBoolean(tokens[4].trim());

                    GradeBook.Session session = currentGradeBook.getSessionByNumber(sessionNum);
                    if (session == null) {
                        session = currentGradeBook.new Session(sessionNum);
                        currentGradeBook.addSession(session);
                    }
                    session.addRecord(subject, grade, isExam);
                }
            }
        }
        return gradeBooks;
    }

    public static void writeHonorStudentsToFile(List<GradeBook> gradeBooks, String outputFileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            for (GradeBook gb : gradeBooks) {
                for (GradeBook.Session session : gb.getSessions()) {
                    if (session.isAllExamsExcellent() && session.isAllCreditsPassed()) {
                        for (GradeBook.ExamRecord record : session.getRecords()) {
                            if (record.isExam()) {
                                writer.write(String.format("%s; %d; %s; %d; %s; %d",
                                        gb.getStudentName(),
                                        gb.getCourse(),
                                        gb.getGroup(),
                                        session.getSessionNumber(),
                                        record.getSubjectName(),
                                        record.getGrade()));
                                writer.newLine();
                            }
                        }
                    }
                }
            }
        }
    }

    public static void writePassingStudentsToFile(List<GradeBook> gradeBooks, String outputFileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            for (GradeBook gb : gradeBooks) {
                for (GradeBook.Session session : gb.getSessions()) {
                    if (session.isPassingSession()) {
                        double avgGrade = session.calculateAverageGrade();
                        writer.write(String.format("%s; Course: %d; Group: %s; Session: %d; Average Grade: %.2f",
                                gb.getStudentName(),
                                gb.getCourse(),
                                gb.getGroup(),
                                session.getSessionNumber(),
                                avgGrade));
                        writer.newLine();
                    }
                }
            }
        }
    }
}