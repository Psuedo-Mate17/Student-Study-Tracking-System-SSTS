package com.studytracker.service;

import com.studytracker.exception.EntityNotFoundException;
import com.studytracker.exception.ValidationException;
import com.studytracker.model.Student;
import com.studytracker.model.StudySession;
import com.studytracker.model.StudySession.FocusLevel;
import com.studytracker.model.StudySession.SessionType;
import com.studytracker.repository.StudentRepository;
import com.studytracker.repository.StudySessionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service layer for StudySession operations.
 * Handles session logging, retrieval, and statistics.
 */
public class StudySessionService {

    private final StudySessionRepository sessionRepository;
    private final StudentRepository studentRepository;

    public StudySessionService(StudySessionRepository sessionRepository,
                                StudentRepository studentRepository) {
        this.sessionRepository = sessionRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Logs a new study session for a student.
     */
    public StudySession logSession(String studentId, String subjectId,
                                    LocalDateTime startTime, long durationMinutes,
                                    FocusLevel focusLevel, SessionType sessionType) {
        // Validate student exists
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student", studentId));

        // Validate duration
        if (durationMinutes <= 0)
            throw new ValidationException("durationMinutes", "Duration must be positive.");
        if (durationMinutes > 720)
            throw new ValidationException("durationMinutes", "Session cannot exceed 12 hours.");

        // Validate start time
        if (startTime == null)
            throw new ValidationException("startTime", "Start time cannot be null.");
        if (startTime.isAfter(LocalDateTime.now()))
            throw new ValidationException("startTime", "Start time cannot be in the future.");

        StudySession session = new StudySession(studentId, subjectId, startTime,
                durationMinutes, focusLevel, sessionType);
        sessionRepository.save(session);

        // Link session to student
        student.addStudySession(session);
        studentRepository.save(student);

        return session;
    }

    /**
     * Retrieves all sessions for a student.
     */
    public List<StudySession> getSessionsByStudent(String studentId) {
        return sessionRepository.findByStudentId(studentId);
    }

    /**
     * Retrieves today's sessions for a student.
     */
    public List<StudySession> getTodaySessions(String studentId) {
        return sessionRepository.findTodaySessionsByStudent(studentId);
    }

    /**
     * Returns sessions within a date range for a student.
     */
    public List<StudySession> getSessionsInRange(String studentId,
                                                  LocalDate from, LocalDate to) {
        return sessionRepository.findByStudentIdAndDateRange(studentId, from, to);
    }

    /**
     * Returns total study minutes for a student this week.
     */
    public long getWeeklyStudyMinutes(String studentId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        return sessionRepository.findByStudentIdAndDateRange(studentId, weekStart, today)
                .stream()
                .mapToLong(StudySession::getDurationMinutes)
                .sum();
    }

    /**
     * Returns average productivity score for a student.
     */
    public double getAverageProductivityScore(String studentId) {
        List<StudySession> sessions = sessionRepository.findByStudentId(studentId);
        if (sessions.isEmpty()) return 0.0;
        return sessions.stream()
                .mapToDouble(StudySession::getProductivityScore)
                .average()
                .orElse(0.0);
    }

    /**
     * Returns study minutes grouped by subject for a student.
     */
    public Map<String, Long> getStudyTimeBySubject(String studentId) {
        return sessionRepository.findByStudentId(studentId)
                .stream()
                .collect(Collectors.groupingBy(
                        StudySession::getSubjectId,
                        Collectors.summingLong(StudySession::getDurationMinutes)
                ));
    }

    /**
     * Returns a daily study summary (date -> minutes) for the past N days.
     */
    public Map<LocalDate, Long> getDailyStudySummary(String studentId, int days) {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(days - 1);
        Map<LocalDate, Long> summary = new LinkedHashMap<>();

        // Pre-fill all dates with 0
        for (LocalDate d = from; !d.isAfter(today); d = d.plusDays(1)) {
            summary.put(d, 0L);
        }

        // Fill actual data
        sessionRepository.findByStudentIdAndDateRange(studentId, from, today)
                .forEach(s -> {
                    LocalDate date = s.getStartTime().toLocalDate();
                    summary.merge(date, s.getDurationMinutes(), Long::sum);
                });

        return summary;
    }

    /**
     * Deletes a session by ID.
     */
    public boolean deleteSession(String sessionId) {
        if (!sessionRepository.existsById(sessionId))
            throw new EntityNotFoundException("StudySession", sessionId);
        return sessionRepository.deleteById(sessionId);
    }

    /**
     * Updates session notes and focus level.
     */
    public StudySession updateSession(String sessionId, String notes, FocusLevel focusLevel) {
        StudySession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("StudySession", sessionId));
        session.setNotes(notes);
        session.setFocusLevel(focusLevel);
        return sessionRepository.save(session);
    }
}
