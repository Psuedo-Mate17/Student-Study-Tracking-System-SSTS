package com.studytracker.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a study goal set by a student (daily, weekly, or per-subject).
 */
public class StudyGoal {

    public enum GoalType {
        DAILY_HOURS, WEEKLY_HOURS, SUBJECT_HOURS, GRADE_TARGET
    }

    public enum GoalStatus {
        ACTIVE, COMPLETED, FAILED, PAUSED
    }

    private final String id;
    private final String studentId;
    private String description;
    private GoalType goalType;
    private double targetValue;      // hours or grade points
    private double currentProgress;
    private LocalDate deadline;
    private GoalStatus status;
    private String subjectId;        // null if not subject-specific

    public StudyGoal(String studentId, String description, GoalType goalType,
                     double targetValue, LocalDate deadline) {
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.description = description;
        this.goalType = goalType;
        this.targetValue = targetValue;
        this.deadline = deadline;
        this.currentProgress = 0.0;
        this.status = GoalStatus.ACTIVE;
    }

    // Getters
    public String getId()                { return id; }
    public String getStudentId()         { return studentId; }
    public String getDescription()       { return description; }
    public GoalType getGoalType()        { return goalType; }
    public double getTargetValue()       { return targetValue; }
    public double getCurrentProgress()   { return currentProgress; }
    public LocalDate getDeadline()       { return deadline; }
    public GoalStatus getStatus()        { return status; }
    public String getSubjectId()         { return subjectId; }

    // Setters
    public void setDescription(String d) { this.description = d; }
    public void setStatus(GoalStatus s)  { this.status = s; }
    public void setSubjectId(String sid) { this.subjectId = sid; }

    /**
     * Updates progress and auto-marks goal as completed if target reached.
     */
    public void addProgress(double amount) {
        this.currentProgress += amount;
        if (currentProgress >= targetValue && status == GoalStatus.ACTIVE) {
            this.status = GoalStatus.COMPLETED;
        }
    }

    /**
     * Returns completion percentage (0–100).
     */
    public double getCompletionPercentage() {
        if (targetValue == 0) return 100.0;
        return Math.min((currentProgress / targetValue) * 100.0, 100.0);
    }

    /**
     * Returns true if the goal's deadline has passed without completion.
     */
    public boolean isOverdue() {
        return LocalDate.now().isAfter(deadline) && status == GoalStatus.ACTIVE;
    }

    /**
     * Returns a simple ASCII progress bar.
     */
    public String getProgressBar() {
        int filled = (int) (getCompletionPercentage() / 10);
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 10; i++) bar.append(i < filled ? "█" : "░");
        bar.append(String.format("] %.1f%%", getCompletionPercentage()));
        return bar.toString();
    }

    @Override
    public String toString() {
        return String.format("Goal{%s | %s | %s | Deadline: %s}",
                description, goalType, getProgressBar(), deadline);
    }
}
