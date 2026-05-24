package com.studytracker.model;

import com.studytracker.model.StudySession.FocusLevel;
import com.studytracker.model.StudySession.SessionType;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StudySession Model Tests")
class StudySessionTest {

    private StudySession session;

    @BeforeEach
    void setUp() {
        session = new StudySession(
                "student-1", "subject-1",
                LocalDateTime.now().minusHours(2),
                90, FocusLevel.HIGH, SessionType.REVISION
        );
    }

    @Test
    @DisplayName("Duration is set correctly")
    void getDurationMinutes_returnsCorrectValue() {
        assertEquals(90, session.getDurationMinutes());
    }

    @Test
    @DisplayName("Formatted duration is correct")
    void getFormattedDuration_returnsHoursAndMinutes() {
        assertEquals("1h 30m", session.getFormattedDuration());
    }

    @Test
    @DisplayName("Productivity score is within 0-100")
    void getProductivityScore_isInValidRange() {
        double score = session.getProductivityScore();
        assertTrue(score >= 0 && score <= 100);
    }

    @Test
    @DisplayName("HIGH focus with 90min gives expected score")
    void getProductivityScore_highFocus90Min_calculatesCorrectly() {
        // Duration score: min(90/120, 1.0) * 50 = 37.5
        // Focus score: (3/4) * 50 = 37.5
        // Total = 75.0
        assertEquals(75.0, session.getProductivityScore(), 0.01);
    }

    @Test
    @DisplayName("Notes can be set and retrieved")
    void setNotes_storesAndReturnsValue() {
        session.setNotes("Studied chapter 5");
        assertEquals("Studied chapter 5", session.getNotes());
    }

    @Test
    @DisplayName("Session equality is based on ID")
    void equals_sameId_returnsTrue() {
        assertEquals(session, session);
    }

    @Test
    @DisplayName("End time is start time plus duration")
    void getEndTime_isStartPlusDuration() {
        LocalDateTime expected = session.getStartTime().plusMinutes(90);
        assertEquals(expected, session.getEndTime());
    }

    @Test
    @DisplayName("Deep work focus level gives maximum focus score")
    void productivity_deepWorkFocus_maxFocusContribution() {
        StudySession deep = new StudySession(
                "s1", "sub1", LocalDateTime.now().minusHours(3),
                120, FocusLevel.DEEP_WORK, SessionType.EXAM_PREP
        );
        // Duration: min(120/120,1)*50=50, Focus: (4/4)*50=50, Total=100
        assertEquals(100.0, deep.getProductivityScore(), 0.01);
    }
}
