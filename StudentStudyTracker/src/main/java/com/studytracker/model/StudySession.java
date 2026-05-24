package com.studytracker.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Represents a single study session for a subject.
 * Tracks duration, focus level, notes, and outcomes.
 */
public class StudySession {

    public enum FocusLevel {
        LOW(1, "Low"),
        MEDIUM(2, "Medium"),
        HIGH(3, "High"),
        DEEP_WORK(4, "Deep Work");

        private final int value;
        private final String label;

        FocusLevel(int value, String label) {
            this.value = value;
            this.label = label;
        }

        public int getValue()  { return value; }
        public String getLabel(){ return label; }

        @Override
        public String toString() { return label; }
    }

    public enum SessionType {
        READING, PRACTICE, REVISION, EXAM_PREP, PROJECT, LECTURE_REVIEW, OTHER
    }

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final String id;
    private final String subjectId;
    private final String studentId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationMinutes;
    private FocusLevel focusLevel;
    private SessionType sessionType;
    private String notes;
    private boolean completed;
    private int pagesRead;
    private int problemsSolved;

    // Constructor for completed sessions
    public StudySession(String studentId, String subjectId, LocalDateTime startTime,
                        long durationMinutes, FocusLevel focusLevel, SessionType sessionType) {
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
        this.endTime = startTime.plusMinutes(durationMinutes);
        this.focusLevel = focusLevel;
        this.sessionType = sessionType;
        this.completed = true;
        this.notes = "";
        this.pagesRead = 0;
        this.problemsSolved = 0;
    }

    // Getters
    public String getId()              { return id; }
    public String getSubjectId()       { return subjectId; }
    public String getStudentId()       { return studentId; }
    public LocalDateTime getStartTime(){ return startTime; }
    public LocalDateTime getEndTime()  { return endTime; }
    public long getDurationMinutes()   { return durationMinutes; }
    public FocusLevel getFocusLevel()  { return focusLevel; }
    public SessionType getSessionType(){ return sessionType; }
    public String getNotes()           { return notes; }
    public boolean isCompleted()       { return completed; }
    public int getPagesRead()          { return pagesRead; }
    public int getProblemsSolved()     { return problemsSolved; }

    // Setters
    public void setNotes(String notes)            { this.notes = notes; }
    public void setCompleted(boolean completed)   { this.completed = completed; }
    public void setPagesRead(int pages)           { this.pagesRead = pages; }
    public void setProblemsSolved(int problems)   { this.problemsSolved = problems; }
    public void setFocusLevel(FocusLevel level)   { this.focusLevel = level; }

    /**
     * Returns a productivity score (0–100) based on focus and duration.
     */
    public double getProductivityScore() {
        double durationScore = Math.min(durationMinutes / 120.0, 1.0) * 50; // max 50 pts for 2h
        double focusScore    = (focusLevel.getValue() / 4.0) * 50;           // max 50 pts for deep work
        return Math.round(durationScore + focusScore);
    }

    /**
     * Returns formatted duration "Xh Ym".
     */
    public String getFormattedDuration() {
        return String.format("%dh %dm", durationMinutes / 60, durationMinutes % 60);
    }

    @Override
    public String toString() {
        return String.format(
                "Session{id='%.8s', date='%s', duration='%s', focus=%s, type=%s, score=%.0f}",
                id, startTime.format(FORMATTER), getFormattedDuration(),
                focusLevel, sessionType, getProductivityScore());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudySession)) return false;
        return id.equals(((StudySession) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
