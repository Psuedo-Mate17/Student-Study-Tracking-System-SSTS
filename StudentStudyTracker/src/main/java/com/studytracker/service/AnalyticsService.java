package com.studytracker.service;

import com.studytracker.model.Student;
import com.studytracker.model.StudySession;
import com.studytracker.model.StudySession.FocusLevel;
import com.studytracker.repository.StudentRepository;
import com.studytracker.repository.StudySessionRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides analytics and reporting for study data.
 * Demonstrates OOP: single-responsibility, encapsulation of analytics logic.
 */
public class AnalyticsService {

    private final StudentRepository studentRepository;
    private final StudySessionRepository sessionRepository;

    public AnalyticsService(StudentRepository studentRepository,
                             StudySessionRepository sessionRepository) {
        this.studentRepository = studentRepository;
        this.sessionRepository = sessionRepository;
    }

    // ── Student Analytics ───────────────────────────────────────────────────

    /**
     * Generates a full analytics report for a student.
     */
    public StudentReport generateStudentReport(String studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        List<StudySession> allSessions = sessionRepository.findByStudentId(studentId);

        long totalMinutes   = allSessions.stream().mapToLong(StudySession::getDurationMinutes).sum();
        double avgScore     = allSessions.stream().mapToDouble(StudySession::getProductivityScore)
                                         .average().orElse(0.0);
        long weeklyMinutes  = getWeeklyMinutes(studentId);
        int streak          = calculateStreak(studentId);

        // Best focus level
        FocusLevel topFocus = allSessions.stream()
                .collect(Collectors.groupingBy(StudySession::getFocusLevel, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(FocusLevel.MEDIUM);

        return new StudentReport(student, allSessions.size(), totalMinutes,
                                 weeklyMinutes, avgScore, streak, topFocus);
    }

    /**
     * Returns the current consecutive-day study streak.
     */
    public int calculateStreak(String studentId) {
        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate checkDate = today;

        while (true) {
            long minutesOnDay = sessionRepository.getTotalMinutesForStudentOnDate(studentId, checkDate);
            if (minutesOnDay > 0) {
                streak++;
                checkDate = checkDate.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }

    /**
     * Returns global leaderboard (all students by total study time).
     */
    public List<LeaderboardEntry> getLeaderboard() {
        return studentRepository.findAllSortedByStudyTime().stream()
                .map(s -> new LeaderboardEntry(s.getName(), s.getTotalStudyMinutes(), calculateStreak(s.getId())))
                .collect(Collectors.toList());
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private long getWeeklyMinutes(String studentId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        return sessionRepository.findByStudentIdAndDateRange(studentId, weekStart, today)
                .stream().mapToLong(StudySession::getDurationMinutes).sum();
    }

    // ── Inner Report Classes (value objects) ─────────────────────────────────

    public static class StudentReport {
        public final Student student;
        public final int totalSessions;
        public final long totalMinutes;
        public final long weeklyMinutes;
        public final double avgProductivityScore;
        public final int currentStreak;
        public final FocusLevel dominantFocusLevel;

        public StudentReport(Student student, int totalSessions, long totalMinutes,
                             long weeklyMinutes, double avgScore, int streak, FocusLevel focus) {
            this.student = student;
            this.totalSessions = totalSessions;
            this.totalMinutes = totalMinutes;
            this.weeklyMinutes = weeklyMinutes;
            this.avgProductivityScore = avgScore;
            this.currentStreak = streak;
            this.dominantFocusLevel = focus;
        }

        public String getSummary() {
            return String.format(
                "╔══════════════════════════════════════╗\n" +
                "║  STUDY REPORT: %-22s║\n" +
                "╠══════════════════════════════════════╣\n" +
                "║  Total Sessions    : %-16d║\n" +
                "║  Total Study Time  : %-16s║\n" +
                "║  This Week         : %-16s║\n" +
                "║  Avg Productivity  : %-14.1f/100║\n" +
                "║  Current Streak    : %-11d days║\n" +
                "║  Dominant Focus    : %-16s║\n" +
                "╚══════════════════════════════════════╝",
                student.getName(),
                totalSessions,
                formatMinutes(totalMinutes),
                formatMinutes(weeklyMinutes),
                avgProductivityScore,
                currentStreak,
                dominantFocusLevel
            );
        }

        private String formatMinutes(long mins) {
            return String.format("%dh %dm", mins / 60, mins % 60);
        }
    }

    public static class LeaderboardEntry {
        public final String studentName;
        public final long totalMinutes;
        public final int streak;

        public LeaderboardEntry(String name, long minutes, int streak) {
            this.studentName = name;
            this.totalMinutes = minutes;
            this.streak = streak;
        }

        public String format(int rank) {
            return String.format("  #%-3d %-20s %5dh %02dm   🔥 %d day streak",
                    rank, studentName, totalMinutes / 60, totalMinutes % 60, streak);
        }
    }
}
