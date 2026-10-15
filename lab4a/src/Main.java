import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private double gpa;
    private boolean isEnrolled;

    public Student(String name, int age, double gpa, boolean isEnrolled) {
        this.name = name;
        this.age = age;
        this.gpa = gpa;
        this.isEnrolled = isEnrolled;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public double getGpa() { return gpa; }
    public boolean isEnrolled() { return isEnrolled; }
}

public class Main {

    private static final String VALID_JSON_FILE = "student.json";
    private static final String INVALID_JSON_FILE = "student_invalid.json";
    private static final String SCHEMA_FILE = "student_schema.json";

    public static void main(String[] args) {
        try {
            Student student = new Student("John Smith", 20, 3.75, true);
            String jsonContent = "{\n" +
                    "  \"name\": \"" + student.getName() + "\",\n" +
                    "  \"age\": " + student.getAge() + ",\n" +
                    "  \"gpa\": " + student.getGpa() + ",\n" +
                    "  \"isEnrolled\": " + student.isEnrolled() + "\n" +
                    "}";

            String schemaContent = "{\n" +
                    "  \"type\": \"object\",\n" +
                    "  \"properties\": {\n" +
                    "    \"name\": { \"type\": \"string\" },\n" +
                    "    \"age\": { \"type\": \"integer\", \"minimum\": 16 },\n" +
                    "    \"gpa\": { \"type\": \"number\" },\n" +
                    "    \"isEnrolled\": { \"type\": \"boolean\" }\n" +
                    "  },\n" +
                    "  \"required\": [\"name\", \"age\", \"gpa\", \"isEnrolled\"]\n" +
                    "}";

            Files.writeString(Paths.get(VALID_JSON_FILE), jsonContent);
            Files.writeString(Paths.get(SCHEMA_FILE), schemaContent);
            System.out.println("Files created successfully.\n");

            System.out.println("--- Valid File Check ---");
            processAndDisplayJson(VALID_JSON_FILE, SCHEMA_FILE);

            String invalidJsonContent = "{\n" +
                    "  \"name\": \"Peter Parker\",\n" +
                    "  \"age\": \"twenty one\",\n" +
                    "  \"gpa\": 3.80,\n" +
                    "  \"isEnrolled\": true\n" +
                    "}";
            Files.writeString(Paths.get(INVALID_JSON_FILE), invalidJsonContent);
            System.out.println("\nInvalid file created.\n");

            System.out.println("--- Invalid File Check ---");
            processAndDisplayJson(INVALID_JSON_FILE, SCHEMA_FILE);

        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
        }
    }

    private static void processAndDisplayJson(String jsonPath, String schemaPath) throws IOException {
        String json = Files.readString(Path.of(jsonPath));
        Map<String, String> data = parseSimpleJsonObject(json);

        List<String> errors = validateJsonAgainstSchema(data);

        if (!errors.isEmpty()) {
            System.out.println("Validation errors in " + jsonPath + ":");
            for (String error : errors) {
                System.out.println(" - " + error);
            }
        } else {
            System.out.println("File " + jsonPath + " passed validation!\n");
            System.out.println("Content:");
            displayReadableContent(data);
        }
    }

    private static Map<String, String> parseSimpleJsonObject(String json) {
        Map<String, String> map = new HashMap<>();
        Pattern pattern = Pattern.compile("\"(\\w+)\"\\s*:\\s*(\"[^\"]*\"|[^,\\}\\s]+)");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2).trim();
            map.put(key, value);
        }
        return map;
    }

    private static List<String> validateJsonAgainstSchema(Map<String, String> data) {
        List<String> errors = new ArrayList<>();

        String[] requiredKeys = {"name", "age", "gpa", "isEnrolled"};
        for (String key : requiredKeys) {
            if (!data.containsKey(key)) {
                errors.add("Missing field: " + key);
            }
        }

        if (data.containsKey("name")) {
            String val = data.get("name");
            if (!val.startsWith("\"") || !val.endsWith("\"")) {
                errors.add("Field 'name' must be String, got: " + val);
            }
        }

        if (data.containsKey("age")) {
            String val = data.get("age");
            try {
                int age = Integer.parseInt(val);
                if (age < 16) {
                    errors.add("Field 'age' must be >= 16, got: " + age);
                }
            } catch (NumberFormatException e) {
                errors.add("Field 'age' must be Integer, got: " + val);
            }
        }

        if (data.containsKey("gpa")) {
            String val = data.get("gpa");
            try {
                Double.parseDouble(val);
            } catch (NumberFormatException e) {
                errors.add("Field 'gpa' must be Number, got: " + val);
            }
        }

        if (data.containsKey("isEnrolled")) {
            String val = data.get("isEnrolled");
            if (!val.equals("true") && !val.equals("false")) {
                errors.add("Field 'isEnrolled' must be Boolean, got: " + val);
            }
        }

        return errors;
    }

    private static void displayReadableContent(Map<String, String> data) {
        Map<String, String> descriptions = Map.of(
                "name", "Full Name",
                "age", "Age (years)",
                "gpa", "Grade Point Average",
                "isEnrolled", "Enrollment Status"
        );

        data.forEach((key, value) -> {
            String cleanValue = value.replace("\"", "");
            if ("isEnrolled".equals(key)) {
                cleanValue = "true".equals(cleanValue) ? "Enrolled" : "Not Enrolled";
            }
            String desc = descriptions.getOrDefault(key, key);
            System.out.printf("  • %-20s (%s): %s%n", desc, key, cleanValue);
        });
    }
}