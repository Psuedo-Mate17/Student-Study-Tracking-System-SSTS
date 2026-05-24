package com.studytracker.service;

import com.studytracker.exception.EntityNotFoundException;
import com.studytracker.exception.ValidationException;
import com.studytracker.model.Student;
import com.studytracker.repository.StudentRepository;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for StudentService.
 * Demonstrates test coverage for the service/business layer.
 */
@DisplayName("StudentService Tests")
class StudentServiceTest {

    private StudentRepository repo;
    private StudentService service;

    @BeforeEach
    void setUp() {
        repo = new StudentRepository();
        service = new StudentService(repo);
    }

    // ── Registration ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Register valid student succeeds")
    void registerStudent_validData_returnsStudent() {
        Student s = service.registerStudent("Alice Smith", "alice@example.com", 2, "Computer Science");
        assertNotNull(s);
        assertNotNull(s.getId());
        assertEquals("Alice Smith", s.getName());
        assertEquals("alice@example.com", s.getEmail());
        assertEquals(2, s.getYearLevel());
        assertEquals("Computer Science", s.getMajor());
    }

    @Test
    @DisplayName("Register with duplicate email throws ValidationException")
    void registerStudent_duplicateEmail_throwsValidationException() {
        service.registerStudent("Alice", "alice@example.com", 1, "CS");
        assertThrows(ValidationException.class, () ->
                service.registerStudent("Bob", "alice@example.com", 2, "Math"));
    }

    @Test
    @DisplayName("Register with invalid email throws ValidationException")
    void registerStudent_invalidEmail_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.registerStudent("Alice", "not-an-email", 1, "CS"));
    }

    @Test
    @DisplayName("Register with empty name throws ValidationException")
    void registerStudent_emptyName_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.registerStudent("", "alice@example.com", 1, "CS"));
    }

    @Test
    @DisplayName("Register with invalid year level throws ValidationException")
    void registerStudent_invalidYear_throwsValidationException() {
        assertThrows(ValidationException.class, () ->
                service.registerStudent("Alice", "alice@example.com", 7, "CS"));
    }

    // ── Retrieval ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Get student by valid ID returns student")
    void getStudentById_validId_returnsStudent() {
        Student saved = service.registerStudent("Bob", "bob@example.com", 3, "Physics");
        Student found = service.getStudentById(saved.getId());
        assertEquals(saved.getId(), found.getId());
    }

    @Test
    @DisplayName("Get student by non-existent ID throws EntityNotFoundException")
    void getStudentById_nonExistentId_throwsException() {
        assertThrows(EntityNotFoundException.class, () ->
                service.getStudentById("non-existent-id"));
    }

    // ── Deletion ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Delete existing student returns true")
    void deleteStudent_existingId_returnsTrue() {
        Student s = service.registerStudent("Carl", "carl@example.com", 1, "Biology");
        assertTrue(service.deleteStudent(s.getId()));
        assertEquals(0, repo.count());
    }

    @Test
    @DisplayName("Delete non-existent student throws EntityNotFoundException")
    void deleteStudent_nonExistentId_throwsException() {
        assertThrows(EntityNotFoundException.class, () ->
                service.deleteStudent("ghost-id"));
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Update student profile successfully")
    void updateStudent_validData_updatesFields() {
        Student s = service.registerStudent("Dave", "dave@example.com", 1, "Art");
        Student updated = service.updateStudent(s.getId(), "David", "david@example.com", 2, "Design");
        assertEquals("David", updated.getName());
        assertEquals("david@example.com", updated.getEmail());
        assertEquals(2, updated.getYearLevel());
        assertEquals("Design", updated.getMajor());
    }

    // ── Leaderboard ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Get all students returns correct count")
    void getAllStudents_multipleStudents_returnsAll() {
        service.registerStudent("A", "a@a.com", 1, "CS");
        service.registerStudent("B", "b@b.com", 2, "Math");
        service.registerStudent("C", "c@c.com", 3, "Physics");
        assertEquals(3, service.getAllStudents().size());
    }
}
