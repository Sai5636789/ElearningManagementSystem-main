-- E-Learning System schema
-- utf8mb4 throughout; password columns widened to 60 to accommodate BCrypt hashes.
-- Default admin password is the BCrypt hash of 'admin' (cost 10).

CREATE DATABASE IF NOT EXISTS ELearningSystem
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ELearningSystem;

-- ─── Core user tables ────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS Admin (
    Adminid   INT          NOT NULL AUTO_INCREMENT,
    fname     VARCHAR(150) NOT NULL,
    lname     VARCHAR(150),
    Email_ID  VARCHAR(250),
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(60)  NOT NULL,
    Gender    VARCHAR(10),
    picture   MEDIUMBLOB,
    PRIMARY KEY (Adminid)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- BCrypt hash of 'admin' (cost 10) — change immediately after first login.
INSERT INTO Admin (fname, lname, Email_ID, username, password, Gender)
VALUES ('Suwaid', 'Aslam', 'Suwaid@admin.com', 'admin',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Male');

CREATE TABLE IF NOT EXISTS Student (
    stdID             INT          NOT NULL AUTO_INCREMENT,
    fname             VARCHAR(150) NOT NULL,
    lname             VARCHAR(150),
    Email_ID          VARCHAR(250),
    username          VARCHAR(50)  NOT NULL UNIQUE,
    password          VARCHAR(60)  NOT NULL,
    Gender            VARCHAR(10),
    Registration_Date DATE         NOT NULL,
    Last_Login        DATETIME,
    picture           MEDIUMBLOB,
    PRIMARY KEY (stdID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Teacher (
    teacherID         INT          NOT NULL AUTO_INCREMENT,
    fname             VARCHAR(150) NOT NULL,
    lname             VARCHAR(150),
    Email_ID          VARCHAR(250),
    username          VARCHAR(50)  NOT NULL UNIQUE,
    password          VARCHAR(60)  NOT NULL,
    Gender            VARCHAR(10),
    Registration_Date DATE         NOT NULL,
    Last_Login        DATETIME,
    picture           MEDIUMBLOB,
    PRIMARY KEY (teacherID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ─── Subjects & Courses ──────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS Subjects (
    Subject_ID INT          NOT NULL AUTO_INCREMENT,
    Name       VARCHAR(150) NOT NULL UNIQUE,
    Adminid    INT          NOT NULL,
    PRIMARY KEY (Subject_ID),
    INDEX idx_subjects_admin (Adminid),
    CONSTRAINT Adminid_fk FOREIGN KEY (Adminid) REFERENCES Admin (Adminid) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Courses (
    course_ID   INT           NOT NULL AUTO_INCREMENT,
    Name        VARCHAR(250)  NOT NULL UNIQUE,
    Description VARCHAR(1000) NOT NULL,
    Content     VARCHAR(3000) NOT NULL,
    teacherID   INT           NOT NULL,
    Subject_ID  INT           NOT NULL,
    PRIMARY KEY (course_ID),
    INDEX idx_courses_teacher  (teacherID),
    INDEX idx_courses_subject  (Subject_ID),
    CONSTRAINT teacherID_fk FOREIGN KEY (teacherID)  REFERENCES Teacher  (teacherID)  ON DELETE CASCADE,
    CONSTRAINT subjectID_fk FOREIGN KEY (Subject_ID) REFERENCES Subjects (Subject_ID) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ─── Enrollments ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS Enrollments (
    Enrollment_ID   INT  NOT NULL AUTO_INCREMENT,
    Enrollment_Date DATE NOT NULL,
    course_ID       INT  NOT NULL,
    stdID           INT  NOT NULL,
    PRIMARY KEY (Enrollment_ID),
    UNIQUE KEY unique_Enrollment (course_ID, stdID),
    INDEX idx_enrollments_course  (course_ID),
    INDEX idx_enrollments_student (stdID),
    CONSTRAINT courseID_fk FOREIGN KEY (course_ID) REFERENCES Courses (course_ID) ON DELETE CASCADE,
    CONSTRAINT stdID_fk    FOREIGN KEY (stdID)     REFERENCES Student (stdID)     ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ─── Messaging ───────────────────────────────────────────────────────────────
-- sender_role disambiguates User_ID across the three user tables.
-- deleted_by_sender / deleted_by_receiver implement per-user soft delete
-- so a message only physically disappears when both sides have deleted it.
-- MessageUsers has been removed; it was a redundant join of data already
-- in Messages.User_ID and Messages.toUser_ID.

CREATE TABLE IF NOT EXISTS Messages (
    Message_ID          INT          NOT NULL AUTO_INCREMENT,
    message             VARCHAR(2000) NOT NULL,
    time_Stamp          DATETIME      NOT NULL,
    User_ID             INT           NOT NULL,
    toUser_ID           INT           NOT NULL,
    sender_role         VARCHAR(10)   NOT NULL DEFAULT 'STUDENT',
    deleted_by_sender   TINYINT(1)    NOT NULL DEFAULT 0,
    deleted_by_receiver TINYINT(1)    NOT NULL DEFAULT 0,
    PRIMARY KEY (Message_ID),
    INDEX idx_messages_to   (toUser_ID),
    INDEX idx_messages_from (User_ID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

-- ─── Unified user view ───────────────────────────────────────────────────────
-- Lets any query resolve a user_id + role pair to a display name without
-- knowing which table to join. Used by MessageDao to look up sender names.

CREATE OR REPLACE VIEW Users AS
    SELECT Adminid  AS user_id, 'ADMIN'   AS role, fname, lname, Email_ID, username FROM Admin
    UNION ALL
    SELECT teacherID,           'TEACHER', fname, lname, Email_ID, username FROM Teacher
    UNION ALL
    SELECT stdID,               'STUDENT', fname, lname, Email_ID, username FROM Student;
