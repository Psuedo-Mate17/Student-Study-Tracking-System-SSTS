package com.studytracker;

import com.studytracker.repository.StudentRepository;
import com.studytracker.repository.StudySessionRepository;
import com.studytracker.service.AnalyticsService;
import com.studytracker.service.StudentService;
import com.studytracker.service.StudySessionService;
import com.studytracker.ui.ConsoleUI;

/**
 * ═══════════════════════════════════════════════════════════
 *  Student Study Tracker — Main Entry Point
 *  A fully Object-Oriented Java application demonstrating:
 *    • Encapsulation   (private fields, public accessors)
 *    • Abstraction     (Repository<T,ID> interface)
 *    • Polymorphism    (multiple repository implementations)
 *    • Inheritance     (exception hierarchy)
 *    • SRP             (model / service / repository / UI layers)
 * ═══════════════════════════════════════════════════════════
 */
public class Main {

    public static void main(String[] args) {

        // ── Dependency wiring (manual DI) ────────────────────────────────────
        StudentRepository     studentRepo  = new StudentRepository();
        StudySessionRepository sessionRepo = new StudySessionRepository();

        StudentService     studentService  = new StudentService(studentRepo);
        StudySessionService sessionService = new StudySessionService(sessionRepo, studentRepo);
        AnalyticsService   analyticsService= new AnalyticsService(studentRepo, sessionRepo);

        // ── Start console UI ─────────────────────────────────────────────────
        ConsoleUI ui = new ConsoleUI(studentService, sessionService, analyticsService);
        ui.start();
    }
}
