package com.studytracker.service;

import com.studytracker.exception.EntityNotFoundException;
import com.studytracker.exception.ValidationException;
import com.studytracker.model.Student;
import com.studytracker.model.Subject;
import com.studytracker.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Student operations.
 * Enforces business rules and delegates storage to the repository.
 */
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    /**
     * Registers a new student after validating input.
     */
    public Student registerStudent(String name, String email, int yearLevel, String major) {
        validateName(name);
        validateEmail(email);
        validateYearLevel(yearLevel);

        if (studentRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("email", "A student with this email already exists.");
        }

        Student student = new Student(name, email, yearLevel, major);
        return studentRepository.save(student);
    }

    /**
     * Updates a student's profile details.
     */
    public Student updateStudent(String studentId, String name, String email,
                                  int yearLevel, String major) {
        Student student = getStudentById(studentId);
        validateName(name);
        validateEmail(email);
        validateYearLevel(yearLevel);

        // Check email uniqueness (excluding current student)
        Optional<Student> existing = studentRepository.findByEmail(email);
        if (existing.isPresent() && !existing.get().getId().equals(studentId)) {
            throw new ValidationException("email", "Email is already used by another student.");
        }

        student.setName(name);
        student.setEmail(email);
        student.setYearLevel(yearLevel);
        student.setMajor(major);
        return studentRepository.save(student);
    }

    /**
     * Retrieves a student or throws EntityNotFoundException.
     */
    public Student getStudentById(String id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student", id));
    }

    /**
     * Returns all registered students.
     */
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    /**
     * Deletes a student and all associated data.
     */
    public boolean deleteStudent(String studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new EntityNotFoundException("Student", studentId);
        }
        return studentRepository.deleteById(studentId);
    }

    /**
     * Adds a subject to a student's subject list.
     */
    public void addSubjectToStudent(String studentId, Subject subject) {
        Student student = getStudentById(studentId);
        student.addSubject(subject);
        studentRepository.save(student);
    }

    /**
     * Returns students ranked by total study hours.
     */
    public List<Student> getLeaderboard() {
        return studentRepository.findAllSortedByStudyTime();
    }

    // ── Validators ──────────────────────────────────────────────────────────

    private void validateName(String name) {
        if (name == null || name.isBlank())
            throw new ValidationException("name", "Name cannot be empty.");
        if (name.length() < 2 || name.length() > 60)
            throw new ValidationException("name", "Name must be between 2 and 60 characters.");
    }

    private void validateEmail(String email) {
        if (email == null || email.isBlank())
            throw new ValidationException("email", "Email cannot be empty.");
        if (!email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$"))
            throw new ValidationException("email", "Invalid email format.");
    }

    private void validateYearLevel(int year) {
        if (year < 1 || year > 6)
            throw new ValidationException("yearLevel", "Year level must be between 1 and 6.");
    }
}
