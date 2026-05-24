package com.studytracker.repository;

import com.studytracker.model.StudySession;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory repository for StudySession entities.
 */
public class StudySessionRepository implements Repository<StudySession, String> {

    private final Map<String, StudySession> store = new LinkedHashMap<>();

    @Override
    public StudySession save(StudySession session) {
        if (session == null) throw new IllegalArgumentException("Session cannot be null");
        store.put(session.getId(), session);
        return session;
    }

    @Override
    public Optional<StudySession> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<StudySession> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean deleteById(String id) {
        return store.remove(id) != null;
    }

    @Override
    public int count() {
        return store.size();
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    /**
     * Returns all sessions for a specific student.
     */
    public List<StudySession> findByStudentId(String studentId) {
        return store.values().stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .sorted(Comparator.comparing(StudySession::getStartTime).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Returns all sessions for a specific subject.
     */
    public List<StudySession> findBySubjectId(String subjectId) {
        return store.values().stream()
                .filter(s -> s.getSubjectId().equals(subjectId))
                .collect(Collectors.toList());
    }

    /**
     * Returns sessions for a student within a date range.
     */
    public List<StudySession> findByStudentIdAndDateRange(String studentId,
                                                          LocalDate from, LocalDate to) {
        return store.values().stream()
                .filter(s -> s.getStudentId().equals(studentId))
                .filter(s -> {
                    LocalDate date = s.getStartTime().toLocalDate();
                    return !date.isBefore(from) && !date.isAfter(to);
                })
                .sorted(Comparator.comparing(StudySession::getStartTime))
                .collect(Collectors.toList());
    }

    /**
     * Returns today's sessions for a student.
     */
    public List<StudySession> findTodaySessionsByStudent(String studentId) {
        LocalDate today = LocalDate.now();
        return findByStudentIdAndDateRange(studentId, today, today);
    }

    /**
     * Returns total study minutes for a student on a given date.
     */
    public long getTotalMinutesForStudentOnDate(String studentId, LocalDate date) {
        return findByStudentIdAndDateRange(studentId, date, date)
                .stream()
                .mapToLong(StudySession::getDurationMinutes)
                .sum();
    }
}
