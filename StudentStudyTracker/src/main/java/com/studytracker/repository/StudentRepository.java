package com.studytracker.repository;

import com.studytracker.model.Student;
import java.util.*;
import java.util.stream.Collectors;

/**
 * In-memory repository for Student entities.
 * Uses a HashMap for O(1) ID lookups.
 */
public class StudentRepository implements Repository<Student, String> {

    private final Map<String, Student> store = new LinkedHashMap<>();

    @Override
    public Student save(Student student) {
        if (student == null) throw new IllegalArgumentException("Student cannot be null");
        store.put(student.getId(), student);
        return student;
    }

    @Override
    public Optional<Student> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Student> findAll() {
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
     * Finds a student by their email address (case-insensitive).
     */
    public Optional<Student> findByEmail(String email) {
        return store.values().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    /**
     * Finds all students in a given major.
     */
    public List<Student> findByMajor(String major) {
        return store.values().stream()
                .filter(s -> s.getMajor().equalsIgnoreCase(major))
                .collect(Collectors.toList());
    }

    /**
     * Finds all students in a given year level.
     */
    public List<Student> findByYearLevel(int year) {
        return store.values().stream()
                .filter(s -> s.getYearLevel() == year)
                .collect(Collectors.toList());
    }

    /**
     * Returns students sorted by total study time (descending).
     */
    public List<Student> findAllSortedByStudyTime() {
        return store.values().stream()
                .sorted(Comparator.comparingLong(Student::getTotalStudyMinutes).reversed())
                .collect(Collectors.toList());
    }
}
