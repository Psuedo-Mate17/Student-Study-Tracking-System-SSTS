package com.studytracker.model;

import java.util.UUID;

/**
 * Represents an academic subject/course being studied.
 */
public class Subject {

    public enum Difficulty {
        EASY, MEDIUM, HARD, VERY_HARD;

        @Override
        public String toString() {
            return name().charAt(0) + name().substring(1).toLowerCase().replace('_', ' ');
        }
    }

    private final String id;
    private String name;
    private String code;         // e.g. "CS101"
    private Difficulty difficulty;
    private double targetGrade;  // 0–100
    private double currentGrade;
    private int creditHours;

    public Subject(String name, String code, Difficulty difficulty, int creditHours) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.code = code;
        this.difficulty = difficulty;
        this.creditHours = creditHours;
        this.targetGrade = 85.0;
        this.currentGrade = 0.0;
    }

    // Getters
    public String getId()            { return id; }
    public String getName()          { return name; }
    public String getCode()          { return code; }
    public Difficulty getDifficulty(){ return difficulty; }
    public double getTargetGrade()   { return targetGrade; }
    public double getCurrentGrade()  { return currentGrade; }
    public int getCreditHours()      { return creditHours; }

    // Setters
    public void setName(String name)              { this.name = name; }
    public void setCode(String code)              { this.code = code; }
    public void setDifficulty(Difficulty d)       { this.difficulty = d; }
    public void setTargetGrade(double grade)      { this.targetGrade = grade; }
    public void setCurrentGrade(double grade)     { this.currentGrade = grade; }
    public void setCreditHours(int hours)         { this.creditHours = hours; }

    /**
     * Returns true if the student is on track (current >= target grade).
     */
    public boolean isOnTrack() {
        return currentGrade >= targetGrade;
    }

    /**
     * Returns the grade gap (positive means below target).
     */
    public double getGradeGap() {
        return targetGrade - currentGrade;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Difficulty: %s | Grade: %.1f/%.1f | Credits: %d",
                code, name, difficulty, currentGrade, targetGrade, creditHours);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subject)) return false;
        return id.equals(((Subject) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
