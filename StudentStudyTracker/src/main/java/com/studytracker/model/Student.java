package com.studytracker.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a student in the study tracking system.
 * Encapsulates student identity and their associated study sessions.
 */
public class Student {

    private final String id;
    private String name;
    private String email;
    private int yearLevel;
    private String major;
    private final List<StudySession> studySessions;
    private final List<Subject> subjects;

    public Student(String name, String email, int yearLevel, String major) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.email = email;
        this.yearLevel = yearLevel;
        this.major = major;
        this.studySessions = new ArrayList<>();
        this.subjects = new ArrayList<>();
    }

    // Getters
    public String getId()           { return id; }
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public int getYearLevel()       { return yearLevel; }
    public String getMajor()        { return major; }
    public List<StudySession> getStudySessions() { return new ArrayList<>(studySessions); }
    public List<Subject> getSubjects()           { return new ArrayList<>(subjects); }

    // Setters
    public void setName(String name)       { this.name = name; }
    public void setEmail(String email)     { this.email = email; }
    public void setYearLevel(int year)     { this.yearLevel = year; }
    public void setMajor(String major)     { this.major = major; }

    // Session management
    public void addStudySession(StudySession session) {
        if (session != null) studySessions.add(session);
    }

    public boolean removeStudySession(String sessionId) {
        return studySessions.removeIf(s -> s.getId().equals(sessionId));
    }

    // Subject management
    public void addSubject(Subject subject) {
        if (subject != null && !subjects.contains(subject)) {
            subjects.add(subject);
        }
    }

    public boolean removeSubject(String subjectId) {
        return subjects.removeIf(s -> s.getId().equals(subjectId));
    }

    /**
     * Returns total study time in minutes across all sessions.
     */
    public long getTotalStudyMinutes() {
        return studySessions.stream()
                .mapToLong(StudySession::getDurationMinutes)
                .sum();
    }

    /**
     * Returns total study time formatted as "Xh Ym".
     */
    public String getFormattedTotalStudyTime() {
        long total = getTotalStudyMinutes();
        return String.format("%dh %dm", total / 60, total % 60);
    }

    @Override
    public String toString() {
        return String.format("Student{id='%s', name='%s', email='%s', year=%d, major='%s'}",
                id, name, email, yearLevel, major);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student other = (Student) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
