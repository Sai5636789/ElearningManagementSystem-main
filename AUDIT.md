# Project Audit: E-Learning Management System

## Overview
This repository contains a desktop e-learning application written in Java Swing and backed by MySQL. The application supports three roles: Admin, Teacher, and Student. Core functionality includes managing subjects, course authoring, course enrollment, viewing participants, and student-to-student messaging.

This audit details the current state of the codebase prior to modernization.

---

## 1. Compilation Failures and Broken Code

1. **Syntax Error in `AddSubject.java`**
   - File: `src/javasemesterproject/Admin/AddSubject.java` (Line 66)
   - Issue: The instantiation `DBConnection c1 = new .DBConnection();` contains invalid syntax (`new .DBConnection()`) and prevents compilation.

2. **Empty `DBConnection.java` Class**
   - File: `src/javasemesterproject/DBConnection.java`
   - Issue: The file contains an empty class body. Every class across the project depends on `c1.s` (`Statement`), `c1.c` (`Connection`), and `c1.Close()`. Because those fields and methods do not exist, all calling classes fail with `cannot find symbol`.

3. **Stray and Misplaced Files in `src/`**
   - File: `src/DBConnection.java`: Located in the default (unnamed) package instead of `javasemesterproject`. Contains Azure SQL connection logic with a `main` method.
   - File: `src/javasemesterproject/AzureSQLConnection.java`: Empty class template.
   - File: `src/NewInterface.java`: Empty interface in the default package.

4. **Missing Build Configuration**
   - The repository lacks a standard build file (`pom.xml` or `build.gradle`).
   - The original project used NetBeans Ant build scripts (`nbproject/`, `build.xml`), which were partially deleted and staged in git. Compiling the project currently requires manually passing classpaths to JAR files in `lib/`.

---

## 2. Security Vulnerabilities

1. **SQL Injection Across the Application**
   - Almost every query in the codebase constructs SQL statements via string concatenation.
   - Examples:
     - `AdminLogin.java`: `"select * from Admin where username='"+u+"' and password='"+v+"'"`
     - `StudentLogin.java`, `TeacherLogin.java`: identical unescaped string concatenation for authentication.
     - `StudentChangePassword.java`: `"update Student SET password = '"+ password +"' Where stdID ='" + StudentLogin.currentStudentID + "'"`
     - `Message.java`: `"INSERT INTO Messages (message, time_Stamp, User_ID, toUser_ID) Values ('" + messageStr +"', ...)"`
     - Any single quote in message text or user input crashes the query or allows arbitrary SQL execution.

2. **Plaintext Passwords and Inadequate Storage**
   - Passwords for Admin, Teacher, and Student are stored in plaintext without hashing.
   - Schema defines `password varchar(20)` across `Admin`, `Student`, and `Teacher` tables. A standard BCrypt hash requires 60 characters, meaning hashed passwords cannot currently be stored without schema modification.

3. **Plaintext Default Credentials**
   - `ELearningSystem.sql` seeds a default admin account with plaintext credentials:
     `VALUES ('Suwaid', 'Aslam', 'Suwaid@admin.com', 'admin', 'admin', 'Male');`

4. **Hardcoded Secrets Committed to Version Control**
   - `src/DBConnection.java` contains committed database host, username (`admin123`), and password (`admin@1234`).
   - The original database connection string connects to `jdbc:mysql:///ELearningSystem` with user `root` and blank password `""`.

5. **Sensitive Information Exposure in UI**
   - `StudentAccountDetails.java`, `TeacherAccountDetails.java`, and `AdminAccountDetails.java` query and render user passwords into visible text fields.
   - `ViewStudents.java` and `ViewTeachers.java` run `SELECT * FROM Student` and `SELECT * FROM Teacher`, loading password strings into memory and table models.

6. **Missing Re-Authentication for Destructive Operations**
   - `ChangePassword` screens allow updating the account password without requiring the existing password.
   - `DeleteAccount` screens allow deleting records immediately from a simple dialog without password confirmation.

---

## 3. Database Schema Problems (`ELearningSystem.sql`)

1. **Legacy Encoding**
   - All tables use `DEFAULT CHARSET = latin1`. They need to be updated to `utf8mb4` to support Unicode and modern character sets.

2. **Ambiguous User Model and Broken Messaging Foreign Keys**
   - The system maintains three separate user tables: `Admin`, `Teacher`, and `Student`, each with independent `AUTO_INCREMENT` IDs.
   - `Messages` contains `User_ID int Not Null` and `toUser_ID int Not Null` without foreign keys. An ID of `1` cannot be disambiguated between Student 1, Teacher 1, or Admin 1.
   - `Inbox.java` explicitly queries:
     `Select fname, lname From Student Where stdID = '"+ messagesListData[i][2] +"'`
     If a teacher or admin sends a message, `Inbox` crashes because it assumes the sender is always in the `Student` table.

3. **Redundant `MessageUsers` Table**
   - `MessageUsers` maps `User_ID` and `Message_ID`. However, `Messages` already contains `User_ID` (sender) and `toUser_ID` (recipient).
   - This leads to dual inserts in `Message.java` and dual deletes in `Inbox.java` and `SentBox.java`.

4. **Concurrency Race Condition on ID Generation**
   - In `Message.java`, after inserting into `Messages`, the code retrieves the new ID via:
     `select Max(Message_ID) As Message_ID From Messages`
   - Under concurrent usage, this query retrieves the wrong message ID. It must use JDBC `Statement.RETURN_GENERATED_KEYS`.

5. **Missing Integrity Constraints and Indexes**
   - `Email_ID` is nullable and lacks a unique constraint.
   - `password` is nullable in all user tables.
   - Foreign keys (`Adminid_fk`, `teacherID_fk`, `subjectID_fk`, `courseID_fk`, `stdID_fk`) lack explicit indexes.

6. **Privileged DDL Statements**
   - `SET GLOBAL max_allowed_packet = 1024*1024*14;` is present at the top of the SQL file. This requires administrative server privileges and fails on managed database instances or restricted Docker init scripts.

---

## 4. Code Duplication Across Roles

The codebase has near-identical classes copied across `Admin`, `Teacher`, and `Student` packages:

1. **Authentication Screens**
   - `AdminLogin.java`, `TeacherLogin.java`, `StudentLogin.java`: Identical layouts, button bindings, and queries differing only by target table and variable names.
   - `AdminSignup.java`, `TeacherSignup.java`, `StudentSignup.java`: Identical registration forms (250+ lines each) with duplicate focus listeners, validation regexes, and file upload dialogs.

2. **Account Management**
   - `AdminManageAccount`, `TeacherManageAccount`, `StudentManageAccount`: Identical navigation menus.
   - `AdminAccountDetails`, `TeacherAccountDetails`, `StudentAccountDetails`: Identical form layouts. `StudentAccountDetails` contains copy-paste remnants such as variable name `lblTeacherDetails`.
   - `AdminChangeName`, `TeacherChangeName`, `StudentChangeName`: Identical single-field update dialogs.
   - `AdminChangeEmail`, `TeacherChangeEmail`, `StudentChangeEmail`: Identical email update dialogs.
   - `AdminChangePassword`, `TeacherChangePassword`, `StudentChangePassword`: Identical password update dialogs.
   - `AdminDeleteAccount`, `TeacherDeleteAccount`, `StudentDeleteAccount`: Identical confirmation prompts and delete queries.

3. **Dashboard Layouts**
   - `Admin.java`, `Teacher.java`, `Student.java`: Identical side-panel layout, circular avatar rendering logic, and window resizing handlers.

4. **Data Grid Views**
   - `ViewStudents.java` and `ViewTeachers.java`: 172-line identical implementations differing only by table name and title.
   - `Inbox.java` and `SentBox.java`: 193-line nearly identical implementations with duplicate list selection, text areas, and delete handling.

---

## 5. Architectural and Operational Blockers

1. **Blocking Operations on Swing Event Dispatch Thread (EDT)**
   - All database queries and image processing operations execute synchronously on the EDT. This causes UI freezes and unresponsiveness during network calls.
   - `LoadingScreen.java` invokes `Thread.sleep(100)` in a loop for 10 seconds directly inside the constructor on the EDT.

2. **Global Mutable State**
   - Session data is stored in public static fields:
     - `StudentLogin.currentStudentID`
     - `TeacherLogin.currentTeacherID`
     - `AdminLogin.currentAdminID`
     - `Main.main`
   - These fields can be overwritten or become inconsistent when logging out or switching views.

3. **Invalid ResultSet Handling (`TYPE_FORWARD_ONLY`)**
   - Multiple classes (`EnrollCourse`, `WithdrawCourse`, `StudyCourse`, `ViewCourses`, `ViewMyCourses`, `ViewMyStudents`, `Inbox`, `SentBox`) determine row counts by looping `while (rs.next()) rowCount++` and then calling `rs.beforeFirst()`.
   - Default JDBC statements return `TYPE_FORWARD_ONLY` result sets. Calling `beforeFirst()` throws `SQLException: Operation not allowed for a result set of type ResultSet.TYPE_FORWARD_ONLY`.

4. **Resource Leaks and Error Handling**
   - Connections, Statements, and ResultSets are not closed using try-with-resources.
   - Input streams (`FileInputStream`, `ByteArrayInputStream`) are frequently left unclosed.
   - Exceptions are handled via `e.printStackTrace()` or logged with `java.util.logging.Logger` pointing to incorrect class names, leaving the user with generic or silent failures.

5. **Rigid Layouts and Broken Scalability**
   - Windows use `setLayout(null)` with absolute `setBounds(...)` coordinates and `setResizable(false)`.
   - This causes elements to truncate or overlap on higher-DPI displays or non-Windows platforms.
   - String equality bugs exist: `ViewParticipants.java` checks `if (role == "Teacher")` using identity comparison `==` instead of `.equals()`.

6. **Repository Clutter**
   - Committed JARs in `lib/`: `mysql-connector-java-5.1.23-bin.jar` (outdated, released 2012), `rs2xml.jar` (unused), and `sqljdbc_12.8.1.0_enu.zip` (7.6 MB zip archive duplicated in `lib/` and `lib/Desktop/`).
   - OS and IDE artifacts: `.DS_Store`, empty `NewFolder/`, empty `test/` directory.
   - No container setup: Setting up the app requires manually installing MySQL, creating databases, and loading schema files.
