# E-Learning Management System

A desktop application for managing online courses, enrollments, and student–teacher communication. Built with Java Swing and MySQL, supporting three independent user roles: Admin, Teacher, and Student.

> **Original project**: [SuwaidAslam/E-Learning-System-Java-GUI-Application](https://github.com/SuwaidAslam/E-Learning-System-Java-GUI-Application).  
> This repository is a refactored and modernized version of that codebase. The original author retains credit per the MIT License included in this repo.

---

## What changed from the original

The original codebase was a working proof-of-concept that mixed SQL directly into UI event handlers, used plaintext passwords, and ran database calls on the Swing Event Dispatch Thread. This refactor focused on making the code maintainable without changing what the application does:

| Area | Before | After |
|------|--------|-------|
| Database access | Raw `Statement` + string concatenation in UI classes | `PreparedStatement` in dedicated DAO classes (`UserDao`, `CourseDao`, etc.) |
| Passwords | Stored and compared as plaintext | BCrypt via `jBCrypt`; legacy plaintext verified as fallback |
| Session state | Passed as constructor arguments between frames | `SessionContext` singleton — one place to get the logged-in user |
| EDT safety | `Thread.sleep()` in `LoadingScreen` blocking the EDT | `SwingWorker` for all blocking operations |
| DB credentials | Hardcoded strings | `.env` file loaded via `dotenv-java`; `DatabaseConfig` with HikariCP |
| Build | NetBeans project file | Maven (`pom.xml`) — reproducible on any machine with JDK 17+ |

---

## Architecture

```
com.elearning
├── config/
│   ├── DatabaseConfig.java   ← HikariCP pool, reads from .env
│   └── SessionContext.java   ← in-memory session (logged-in user)
├── dao/
│   ├── UserDao.java          ← register, authenticate, update, delete
│   ├── CourseDao.java        ← CRUD for courses
│   ├── SubjectDao.java       ← subject list
│   ├── EnrollmentDao.java    ← enroll / withdraw / list students
│   └── MessageDao.java       ← send and retrieve inbox messages
├── model/
│   ├── User.java, UserSession.java, Role.java
│   ├── Course.java, Subject.java, Message.java
│   └── Enrollment.java
├── ui/
│   ├── admin/    ← Admin login, signup, dashboard, management screens
│   ├── teacher/  ← Teacher login, signup, dashboard, course screens
│   └── student/  ← Student login, signup, dashboard, course/messaging screens
└── util/
    └── PasswordUtil.java     ← BCrypt hash + verify, with plaintext fallback
```

Each DAO receives a `Connection` from `DatabaseConfig.getConnection()` (HikariCP pool) and uses only `PreparedStatement`. No SQL appears in any UI class.

---

## Prerequisites

- JDK 17 or later
- MySQL 8.x (or Docker — see below)
- Maven 3.8+ (or use the included `./mvnw`)

---

## Quick start

### 1. Clone

```bash
git clone <your-fork-url>
cd ElearningManagementSystem-main
```

### 2. Database

**Option A – Docker (recommended)**

```bash
docker compose up -d
```

This starts MySQL on port 3306 with the schema already loaded.

**Option B – existing MySQL**

```sql
CREATE DATABASE elearningsystem;
USE elearningsystem;
source ELearningSystem.sql;
```

### 3. Environment

```bash
cp .env.example .env
# then edit .env with your DB credentials
```

```dotenv
DB_URL=jdbc:mysql://localhost:3306/elearningsystem
DB_USER=root
DB_PASSWORD=yourpassword
```

### 4. Build and run

```bash
./mvnw clean package -q
./mvnw exec:java
```

Or run `com.elearning.ui.LoadingScreen` from your IDE.

**Default admin credentials** (seeded by `ELearningSystem.sql`):

```
username: admin
password: admin
```

---

## Running tests

```bash
./mvnw test
```

Unit tests for `PasswordUtil` (BCrypt + legacy fallback) and `SessionContext` run with no database. Integration tests use H2 in-memory.

---

## Key decisions

**Why DAOs instead of helper methods?**  
Each DAO owns one table group. If the schema changes, there is exactly one file to update. It also makes the methods easy to test in isolation.

**Why `SessionContext` instead of passing the user around?**  
Swing frames get constructed all over the place. A singleton avoids threading issues and lets any screen call `SessionContext.getCurrentUser()` without needing constructor arguments.

**Why BCrypt with a plaintext fallback?**  
The original database had plaintext passwords. The fallback lets legacy accounts log in after importing the original SQL dump; hashes get upgraded on next write.

**Why `SwingWorker` instead of a plain thread?**  
`SwingWorker.done()` runs back on the EDT, so updating labels after a DB call is safe by default. `doInBackground()` is the only place where blocking work happens.

---

## License

MIT — see [LICENSE](LICENSE).  
Original work © Suwaid Aslam. Refactoring and modernization by [your name].
