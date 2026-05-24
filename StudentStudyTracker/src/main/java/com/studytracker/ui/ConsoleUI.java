package com.studytracker.ui;

import com.studytracker.exception.EntityNotFoundException;
import com.studytracker.exception.ValidationException;
import com.studytracker.model.*;
import com.studytracker.model.StudySession.FocusLevel;
import com.studytracker.model.StudySession.SessionType;
import com.studytracker.service.AnalyticsService;
import com.studytracker.service.StudentService;
import com.studytracker.service.StudySessionService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Console-based user interface for the Student Study Tracker.
 * Handles all user input/output and delegates to services.
 */
public class ConsoleUI {

    private static final String DIVIDER =
            "════════════════════════════════════════════════════";
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Scanner scanner;
    private final StudentService studentService;
    private final StudySessionService sessionService;
    private final AnalyticsService analyticsService;

    private Student currentStudent = null;

    public ConsoleUI(StudentService studentService,
                     StudySessionService sessionService,
                     AnalyticsService analyticsService) {
        this.scanner = new Scanner(System.in);
        this.studentService = studentService;
        this.sessionService = sessionService;
        this.analyticsService = analyticsService;
    }

    // ── Entry Point ──────────────────────────────────────────────────────────

    public void start() {
        printBanner();
        while (true) {
            if (currentStudent == null) {
                showAuthMenu();
            } else {
                showMainMenu();
            }
        }
    }

    // ── Menus ────────────────────────────────────────────────────────────────

    private void showAuthMenu() {
        println("\n" + DIVIDER);
        println("  WELCOME TO STUDENT STUDY TRACKER");
        println(DIVIDER);
        println("  [1] Register New Student");
        println("  [2] Login (by Student ID)");
        println("  [3] View Leaderboard");
        println("  [0] Exit");
        println(DIVIDER);
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> registerStudent();
            case 2 -> loginStudent();
            case 3 -> viewLeaderboard();
            case 0 -> exitApp();
            default -> println("  ⚠ Invalid option.");
        }
    }

    private void showMainMenu() {
        println("\n" + DIVIDER);
        println("  Logged in as: " + currentStudent.getName() +
                " | " + currentStudent.getMajor() + " (Year " + currentStudent.getYearLevel() + ")");
        println(DIVIDER);
        println("  [1] Log Study Session");
        println("  [2] View My Sessions");
        println("  [3] View Today's Sessions");
        println("  [4] Manage Subjects");
        println("  [5] View My Analytics Report");
        println("  [6] View Weekly Summary (chart)");
        println("  [7] View Leaderboard");
        println("  [8] Update My Profile");
        println("  [9] Delete Account");
        println("  [0] Logout");
        println(DIVIDER);
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> logStudySession();
            case 2 -> viewAllSessions();
            case 3 -> viewTodaySessions();
            case 4 -> manageSubjects();
            case 5 -> viewAnalyticsReport();
            case 6 -> viewWeeklyChart();
            case 7 -> viewLeaderboard();
            case 8 -> updateProfile();
            case 9 -> deleteAccount();
            case 0 -> logout();
            default -> println("  ⚠ Invalid option.");
        }
    }

    // ── Auth Actions ─────────────────────────────────────────────────────────

    private void registerStudent() {
        println("\n── Register New Student ──");
        String name    = readString("Full name: ");
        String email   = readString("Email: ");
        int year       = readInt("Year level (1-6): ");
        String major   = readString("Major/Course: ");

        try {
            Student s = studentService.registerStudent(name, email, year, major);
            println("\n✅ Registration successful!");
            println("   Your Student ID: " + s.getId());
            println("   Save this ID to log in later.");
            currentStudent = s;
        } catch (ValidationException e) {
            println("  ❌ " + e.getMessage());
        }
    }

    private void loginStudent() {
        println("\n── Student Login ──");
        String id = readString("Enter your Student ID: ");
        try {
            currentStudent = studentService.getStudentById(id);
            println("  ✅ Welcome back, " + currentStudent.getName() + "!");
        } catch (EntityNotFoundException e) {
            println("  ❌ Student not found. Check your ID.");
        }
    }

    private void logout() {
        println("  👋 Goodbye, " + currentStudent.getName() + "!");
        currentStudent = null;
    }

    // ── Session Actions ──────────────────────────────────────────────────────

    private void logStudySession() {
        println("\n── Log Study Session ──");

        List<Subject> subjects = currentStudent.getSubjects();
        if (subjects.isEmpty()) {
            println("  ⚠ No subjects found. Add subjects first (option 4).");
            return;
        }

        // Select subject
        println("  Your Subjects:");
        for (int i = 0; i < subjects.size(); i++) {
            println("  [" + (i + 1) + "] " + subjects.get(i));
        }
        int subjectIdx = readInt("Select subject number: ") - 1;
        if (subjectIdx < 0 || subjectIdx >= subjects.size()) {
            println("  ❌ Invalid selection.");
            return;
        }
        String subjectId = subjects.get(subjectIdx).getId();

        // Date/time
        String dateInput = readString("Date (yyyy-MM-dd, or press ENTER for today): ");
        LocalDate date;
        if (dateInput.isBlank()) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(dateInput, DATE_FMT);
            } catch (DateTimeParseException e) {
                println("  ❌ Invalid date format.");
                return;
            }
        }

        String timeInput = readString("Start time (HH:mm, or press ENTER for 00:00): ");
        LocalDateTime startTime;
        try {
            String combined = date + " " + (timeInput.isBlank() ? "00:00" : timeInput);
            startTime = LocalDateTime.parse(combined, DATETIME_FMT);
        } catch (DateTimeParseException e) {
            println("  ❌ Invalid time format.");
            return;
        }

        long duration = readLong("Duration (minutes): ");

        // Focus level
        println("  Focus level:");
        FocusLevel[] levels = FocusLevel.values();
        for (int i = 0; i < levels.length; i++) {
            println("  [" + (i + 1) + "] " + levels[i]);
        }
        int focusIdx = readInt("Select focus level: ") - 1;
        if (focusIdx < 0 || focusIdx >= levels.length) {
            println("  ❌ Invalid selection.");
            return;
        }
        FocusLevel focus = levels[focusIdx];

        // Session type
        println("  Session type:");
        SessionType[] types = SessionType.values();
        for (int i = 0; i < types.length; i++) {
            println("  [" + (i + 1) + "] " + types[i]);
        }
        int typeIdx = readInt("Select session type: ") - 1;
        if (typeIdx < 0 || typeIdx >= types.length) {
            println("  ❌ Invalid selection.");
            return;
        }
        SessionType type = types[typeIdx];

        try {
            StudySession session = sessionService.logSession(
                    currentStudent.getId(), subjectId, startTime, duration, focus, type);
            // Refresh student
            currentStudent = studentService.getStudentById(currentStudent.getId());
            println("\n  ✅ Session logged!");
            println("  " + session);
            println("  Productivity Score: " + (int)session.getProductivityScore() + "/100");
        } catch (ValidationException | EntityNotFoundException e) {
            println("  ❌ " + e.getMessage());
        }
    }

    private void viewAllSessions() {
        println("\n── All Study Sessions ──");
        List<StudySession> sessions = sessionService.getSessionsByStudent(currentStudent.getId());
        if (sessions.isEmpty()) {
            println("  No sessions recorded yet.");
            return;
        }
        sessions.forEach(s -> println("  • " + s));
    }

    private void viewTodaySessions() {
        println("\n── Today's Sessions ──");
        List<StudySession> sessions = sessionService.getTodaySessions(currentStudent.getId());
        if (sessions.isEmpty()) {
            println("  No sessions today. Start studying! 💪");
            return;
        }
        long total = sessions.stream().mapToLong(StudySession::getDurationMinutes).sum();
        sessions.forEach(s -> println("  • " + s));
        println("  ─────────────────────────────────");
        println(String.format("  Total today: %dh %dm", total / 60, total % 60));
    }

    // ── Subject Management ───────────────────────────────────────────────────

    private void manageSubjects() {
        println("\n── Manage Subjects ──");
        println("  [1] Add Subject");
        println("  [2] View Subjects");
        println("  [3] Update Grade");
        println("  [0] Back");
        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> addSubject();
            case 2 -> viewSubjects();
            case 3 -> updateGrade();
            case 0 -> { /* back */ }
            default -> println("  ⚠ Invalid option.");
        }
    }

    private void addSubject() {
        println("\n── Add Subject ──");
        String name  = readString("Subject name: ");
        String code  = readString("Subject code (e.g. CS101): ");
        println("  Difficulty: [1] Easy  [2] Medium  [3] Hard  [4] Very Hard");
        int diff     = readInt("Select: ");
        Subject.Difficulty difficulty = switch (diff) {
            case 1 -> Subject.Difficulty.EASY;
            case 2 -> Subject.Difficulty.MEDIUM;
            case 3 -> Subject.Difficulty.HARD;
            case 4 -> Subject.Difficulty.VERY_HARD;
            default -> Subject.Difficulty.MEDIUM;
        };
        int credits  = readInt("Credit hours: ");

        Subject subject = new Subject(name, code, difficulty, credits);
        studentService.addSubjectToStudent(currentStudent.getId(), subject);
        currentStudent = studentService.getStudentById(currentStudent.getId());
        println("  ✅ Subject added: " + subject.getCode() + " " + subject.getName());
    }

    private void viewSubjects() {
        println("\n── Your Subjects ──");
        List<Subject> subjects = currentStudent.getSubjects();
        if (subjects.isEmpty()) {
            println("  No subjects added yet.");
            return;
        }
        subjects.forEach(s -> {
            String track = s.isOnTrack() ? "✅ On Track" : "⚠ Below Target";
            println("  " + s + " | " + track);
        });
    }

    private void updateGrade() {
        List<Subject> subjects = currentStudent.getSubjects();
        if (subjects.isEmpty()) {
            println("  No subjects to update.");
            return;
        }
        println("\n── Update Grade ──");
        for (int i = 0; i < subjects.size(); i++) {
            println("  [" + (i + 1) + "] " + subjects.get(i).getCode() + " " + subjects.get(i).getName());
        }
        int idx = readInt("Select subject: ") - 1;
        if (idx < 0 || idx >= subjects.size()) {
            println("  ❌ Invalid selection.");
            return;
        }
        double grade = readDouble("Current grade (0-100): ");
        subjects.get(idx).setCurrentGrade(Math.max(0, Math.min(100, grade)));
        studentService.getStudentById(currentStudent.getId()); // refresh check
        println("  ✅ Grade updated.");
    }

    // ── Analytics ────────────────────────────────────────────────────────────

    private void viewAnalyticsReport() {
        println("\n");
        AnalyticsService.StudentReport report =
                analyticsService.generateStudentReport(currentStudent.getId());
        println(report.getSummary());
    }

    private void viewWeeklyChart() {
        println("\n── Weekly Study Chart (last 7 days) ──");
        var summary = sessionService.getDailyStudySummary(currentStudent.getId(), 7);
        long maxMinutes = summary.values().stream().mapToLong(v -> v).max().orElse(1);

        summary.forEach((date, minutes) -> {
            int barLen = maxMinutes > 0 ? (int) ((minutes * 20) / maxMinutes) : 0;
            String bar = "█".repeat(barLen) + "░".repeat(20 - barLen);
            println(String.format("  %s │%s│ %dh%02dm",
                    date.format(DATE_FMT), bar, minutes / 60, minutes % 60));
        });
    }

    private void viewLeaderboard() {
        println("\n── 🏆 Leaderboard ──");
        List<AnalyticsService.LeaderboardEntry> entries = analyticsService.getLeaderboard();
        if (entries.isEmpty()) {
            println("  No students registered yet.");
            return;
        }
        println("  Rank  Name                 Total Time     Streak");
        println("  " + "─".repeat(50));
        for (int i = 0; i < entries.size(); i++) {
            println(entries.get(i).format(i + 1));
        }
    }

    // ── Profile ──────────────────────────────────────────────────────────────

    private void updateProfile() {
        println("\n── Update Profile ──");
        String name  = readString("New name (current: " + currentStudent.getName() + "): ");
        String email = readString("New email (current: " + currentStudent.getEmail() + "): ");
        int year     = readInt("New year level (current: " + currentStudent.getYearLevel() + "): ");
        String major = readString("New major (current: " + currentStudent.getMajor() + "): ");

        try {
            currentStudent = studentService.updateStudent(
                    currentStudent.getId(), name, email, year, major);
            println("  ✅ Profile updated.");
        } catch (ValidationException e) {
            println("  ❌ " + e.getMessage());
        }
    }

    private void deleteAccount() {
        println("\n  ⚠ WARNING: This will permanently delete your account and all data.");
        String confirm = readString("Type 'DELETE' to confirm: ");
        if ("DELETE".equals(confirm)) {
            studentService.deleteStudent(currentStudent.getId());
            println("  ✅ Account deleted. Goodbye.");
            currentStudent = null;
        } else {
            println("  Cancelled.");
        }
    }

    // ── Utility ──────────────────────────────────────────────────────────────

    private void printBanner() {
        println("""
                
                ╔══════════════════════════════════════════════════╗
                ║                                                  ║
                ║      📚  STUDENT STUDY TRACKER  📚               ║
                ║         Track. Analyze. Excel.                   ║
                ║                                                  ║
                ╚══════════════════════════════════════════════════╝
                """);
    }

    private void exitApp() {
        println("\n  Thanks for using Student Study Tracker. Keep learning! 🎓\n");
        System.exit(0);
    }

    private void println(String msg) { System.out.println(msg); }

    private String readString(String prompt) {
        System.out.print("  " + prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                System.out.print("  " + prompt);
                int val = Integer.parseInt(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                println("  ❌ Please enter a valid number.");
            }
        }
    }

    private long readLong(String prompt) {
        while (true) {
            try {
                System.out.print("  " + prompt);
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                println("  ❌ Please enter a valid number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print("  " + prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                println("  ❌ Please enter a valid number.");
            }
        }
    }
}
