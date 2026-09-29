import java.util.ArrayList;
import java.util.List;

public class GradeBook {
    private String studentName;
    private int course;
    private String group;
    private List<Session> sessions;

    public GradeBook(String studentName, int course, String group) {
        if (studentName == null || studentName.trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty.");
        }
        if (course < 1 || course > 6) {
            throw new IllegalArgumentException("Invalid course number: " + course);
        }
        if (group == null || group.trim().isEmpty()) {
            throw new IllegalArgumentException("Group cannot be empty.");
        }

        this.studentName = studentName;
        this.course = course;
        this.group = group;
        this.sessions = new ArrayList<>();
    }

    public String getStudentName() {
        return studentName;
    }

    public int getCourse() {
        return course;
    }

    public String getGroup() {
        return group;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void addSession(Session session) {
        if (session == null) {
            throw new IllegalArgumentException("Session cannot be null.");
        }
        this.sessions.add(session);
    }

    public Session getSessionByNumber(int sessionNumber) {
        for (Session s : sessions) {
            if (s.getSessionNumber() == sessionNumber) {
                return s;
            }
        }
        return null;
    }

    public class Session {
        private int sessionNumber;
        private List<ExamRecord> records;

        public Session(int sessionNumber) {
            if (sessionNumber < 1 || sessionNumber > 9) {
                throw new IllegalArgumentException("Invalid session number: " + sessionNumber);
            }
            this.sessionNumber = sessionNumber;
            this.records = new ArrayList<>();
        }

        public int getSessionNumber() {
            return sessionNumber;
        }

        public List<ExamRecord> getRecords() {
            return records;
        }

        public void addRecord(String subjectName, int grade, boolean isExam) {
            if (subjectName == null || subjectName.trim().isEmpty()) {
                throw new IllegalArgumentException("Subject name cannot be empty.");
            }
            if (isExam && (grade < 1 || grade > 10)) {
                throw new IllegalArgumentException("Invalid exam grade: " + grade);
            }
            if (!isExam && (grade != 0 && grade != 1)) {
                throw new IllegalArgumentException("Credit status must be 1 (passed) or 0 (failed).");
            }
            this.records.add(new ExamRecord(subjectName, grade, isExam));
        }

        public boolean isAllExamsExcellent() {
            boolean hasExams = false;
            for (ExamRecord record : records) {
                if (record.isExam()) {
                    hasExams = true;
                    if (record.getGrade() < 9) {
                        return false;
                    }
                }
            }
            return hasExams;
        }

        public boolean isAllCreditsPassed() {
            for (ExamRecord record : records) {
                if (!record.isExam() && record.getGrade() != 1) {
                    return false;
                }
            }
            return true;
        }

        public boolean isPassingSession() {
            for (ExamRecord record : records) {
                if (record.isExam() && record.getGrade() < 4) {
                    return false;
                }
                if (!record.isExam() && record.getGrade() != 1) {
                    return false;
                }
            }
            return true;
        }

        public double calculateAverageGrade() {
            int total = 0;
            int count = 0;
            for (ExamRecord record : records) {
                if (record.isExam()) {
                    total += record.getGrade();
                    count++;
                }
            }
            if (count == 0) {
                return 0.0;
            }
            return (double) total / count;
        }
    }

    public static class ExamRecord {
        private String subjectName;
        private int grade;
        private boolean isExam;

        public ExamRecord(String subjectName, int grade, boolean isExam) {
            this.subjectName = subjectName;
            this.grade = grade;
            this.isExam = isExam;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public int getGrade() {
            return grade;
        }

        public boolean isExam() {
            return isExam;
        }
    }
}