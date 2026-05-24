# 📚 Student Study Tracker

> A fully **Object-Oriented Java** console application to track, analyze, and improve student study habits — built as a learning project demonstrating core OOP principles.

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=flat-square&logo=apachemaven)
![JUnit 5](https://img.shields.io/badge/JUnit-5.10-green?style=flat-square&logo=junit5)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

---

## ✨ Features

| Feature | Description |
|---|---|
| 🎓 **Student Management** | Register, update, and delete student profiles |
| 📖 **Subject Tracking** | Add subjects with difficulty, credits, and grade targets |
| ⏱️ **Session Logging** | Log study sessions with focus level, type, and notes |
| 📊 **Analytics Reports** | Detailed stats: total hours, weekly time, avg productivity |
| 🔥 **Streak Tracking** | Consecutive study-day streaks to keep you motivated |
| 📈 **Weekly Chart** | ASCII bar chart of daily study time for the last 7 days |
| 🏆 **Leaderboard** | Rank all students by total study hours |
| 🎯 **Goal System** | Set and track daily, weekly, and subject-specific goals |
| ✅ **Grade Monitoring** | Track current vs target grades per subject |

---

## 🏗️ OOP Concepts Demonstrated

This project was designed to showcase **all four pillars of OOP** in a real Java application:

### 1. Encapsulation
Every model class (`Student`, `Subject`, `StudySession`, `StudyGoal`) uses **private fields** with controlled public getters/setters. Business logic stays inside the class (e.g., `getTotalStudyMinutes()`, `getProductivityScore()`).

```java
public class Student {
    private final String id;      // immutable after creation
    private String name;          // mutable via setter
    private final List<StudySession> studySessions;

    public long getTotalStudyMinutes() {   // encapsulated computation
        return studySessions.stream()
                .mapToLong(StudySession::getDurationMinutes)
                .sum();
    }
}
```

### 2. Abstraction
The `Repository<T, ID>` interface hides storage implementation details. The UI only talks to **services**, never directly to repositories or data structures.

```java
public interface Repository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    boolean deleteById(ID id);
    int count();
    boolean existsById(ID id);
}
```

### 3. Inheritance
Custom exception hierarchy built on `RuntimeException`:

```
RuntimeException
├── EntityNotFoundException    // Resource not found
└── ValidationException        // Bad input data
```

### 4. Polymorphism
`StudentRepository` and `StudySessionRepository` both implement `Repository<T, ID>`, allowing code to depend on the interface, not the concrete class. Enum types (`FocusLevel`, `SessionType`, `Difficulty`) use `toString()` overriding for clean display.

---

## 📁 Project Structure

```
StudentStudyTracker/
├── src/
│   ├── main/java/com/studytracker/
│   │   ├── Main.java                          # Entry point & DI wiring
│   │   ├── model/
│   │   │   ├── Student.java                   # Student entity
│   │   │   ├── Subject.java                   # Academic subject entity
│   │   │   ├── StudySession.java              # Study session entity
│   │   │   └── StudyGoal.java                 # Goal entity
│   │   ├── repository/
│   │   │   ├── Repository.java                # Generic CRUD interface
│   │   │   ├── StudentRepository.java         # In-memory student store
│   │   │   └── StudySessionRepository.java    # In-memory session store
│   │   ├── service/
│   │   │   ├── StudentService.java            # Student business logic
│   │   │   ├── StudySessionService.java       # Session business logic
│   │   │   └── AnalyticsService.java          # Stats & reporting
│   │   ├── ui/
│   │   │   └── ConsoleUI.java                 # Interactive console interface
│   │   └── exception/
│   │       ├── EntityNotFoundException.java   # Not-found errors
│   │       └── ValidationException.java       # Input validation errors
│   └── test/java/com/studytracker/
│       ├── service/StudentServiceTest.java     # Service layer tests
│       └── model/StudySessionTest.java         # Model unit tests
├── pom.xml                                     # Maven build config
├── .gitignore
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)

Verify your setup:
```bash
java -version
mvn -version
```

### Clone & Run

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/StudentStudyTracker.git
cd StudentStudyTracker

# 2. Build the project
mvn clean package

# 3. Run the application
java -jar target/study-tracker.jar
```

### Running Tests

```bash
mvn test
```

---

## 🖥️ Application Walkthrough

### First Launch

```
╔══════════════════════════════════════════════════╗
║                                                  ║
║      📚  STUDENT STUDY TRACKER  📚               ║
║         Track. Analyze. Excel.                   ║
║                                                  ║
╚══════════════════════════════════════════════════╝

════════════════════════════════════════════════════
  WELCOME TO STUDENT STUDY TRACKER
════════════════════════════════════════════════════
  [1] Register New Student
  [2] Login (by Student ID)
  [3] View Leaderboard
  [0] Exit
```

### Analytics Report

```
╔══════════════════════════════════════╗
║  STUDY REPORT: Alice Smith          ║
╠══════════════════════════════════════╣
║  Total Sessions    : 14             ║
║  Total Study Time  : 32h 45m        ║
║  This Week         : 8h 30m         ║
║  Avg Productivity  : 78.5      /100 ║
║  Current Streak    : 5         days ║
║  Dominant Focus    : Deep Work      ║
╚══════════════════════════════════════╝
```

### Weekly Study Chart

```
── Weekly Study Chart (last 7 days) ──
  2025-05-18 │████████████████████│ 3h30m
  2025-05-19 │██████░░░░░░░░░░░░░░│ 1h15m
  2025-05-20 │████████████░░░░░░░░│ 2h00m
  2025-05-21 │░░░░░░░░░░░░░░░░░░░░│ 0h00m
  2025-05-22 │████████████████░░░░│ 2h45m
  2025-05-23 │████████████████████│ 3h30m
  2025-05-24 │██████████░░░░░░░░░░│ 1h45m
```

---

## 🧪 Testing

The project includes JUnit 5 unit tests covering:

- ✅ Student registration (valid & invalid data)
- ✅ Duplicate email detection
- ✅ Student retrieval and deletion
- ✅ Profile updates
- ✅ Productivity score calculation
- ✅ Session duration formatting
- ✅ Session equality and immutability

Run with:
```bash
mvn test
```

---

## 🛣️ Roadmap

- [ ] **File persistence** — save/load data to JSON or SQLite
- [ ] **Pomodoro timer** — built-in 25/5 minute study timer
- [ ] **Goal system UI** — full menu integration for `StudyGoal`
- [ ] **Export reports** — generate PDF or CSV study reports
- [ ] **JavaFX GUI** — graphical dashboard with charts
- [ ] **Multi-user login** — password-protected accounts

---

## 🤝 Contributing

Contributions are welcome! Here's how:

1. Fork the project
2. Create your branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

Please make sure all tests pass (`mvn test`) before submitting.

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 👨‍💻 Author

Built with ☕ and OOP principles.  
If this project helped you learn Java OOP, consider giving it a ⭐!
