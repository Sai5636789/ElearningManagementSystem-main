-- V1: Baseline schema with utf8mb4 encoding, widened password column for BCrypt, and NOT NULL constraints

CREATE TABLE IF NOT EXISTS Admin (
    Adminid INT NOT NULL AUTO_INCREMENT,
    fname VARCHAR(150) NOT NULL,
    lname VARCHAR(150),
    Email_ID VARCHAR(250) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    Gender VARCHAR(20),
    picture MEDIUMBLOB,
    PRIMARY KEY (Adminid)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS Student (
    stdID INT NOT NULL AUTO_INCREMENT,
    fname VARCHAR(150) NOT NULL,
    lname VARCHAR(150),
    Email_ID VARCHAR(250) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    Gender VARCHAR(20),
    Registration_Date DATE NOT NULL,
    Last_Login DATETIME,
    picture MEDIUMBLOB,
    PRIMARY KEY (stdID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS Teacher (
    teacherID INT NOT NULL AUTO_INCREMENT,
    fname VARCHAR(150) NOT NULL,
    lname VARCHAR(150),
    Email_ID VARCHAR(250) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    Gender VARCHAR(20),
    Registration_Date DATE NOT NULL,
    Last_Login DATETIME,
    picture MEDIUMBLOB,
    PRIMARY KEY (teacherID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS Subjects (
    Subject_ID INT NOT NULL AUTO_INCREMENT,
    Name VARCHAR(150) NOT NULL UNIQUE,
    Adminid INT NOT NULL,
    PRIMARY KEY (Subject_ID),
    CONSTRAINT Adminid_fk FOREIGN KEY (Adminid) REFERENCES Admin(Adminid) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS Courses (
    course_ID INT NOT NULL AUTO_INCREMENT,
    Name VARCHAR(250) NOT NULL UNIQUE,
    Description VARCHAR(1000) NOT NULL,
    Content VARCHAR(3000) NOT NULL,
    teacherID INT NOT NULL,
    Subject_ID INT NOT NULL,
    PRIMARY KEY (course_ID),
    CONSTRAINT teacherID_fk FOREIGN KEY (teacherID) REFERENCES Teacher(teacherID) ON DELETE CASCADE,
    CONSTRAINT subjectID_fk FOREIGN KEY (Subject_ID) REFERENCES Subjects(Subject_ID) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS Enrollments (
    Enrollment_ID INT NOT NULL AUTO_INCREMENT,
    Enrollment_Date DATE NOT NULL,
    course_ID INT NOT NULL,
    stdID INT NOT NULL,
    PRIMARY KEY (Enrollment_ID),
    CONSTRAINT courseID_fk FOREIGN KEY (course_ID) REFERENCES Courses(course_ID) ON DELETE CASCADE,
    CONSTRAINT stdID_fk FOREIGN KEY (stdID) REFERENCES Student(stdID) ON DELETE CASCADE,
    UNIQUE KEY unique_Enrollment (course_ID, stdID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS Messages (
    Message_ID INT NOT NULL AUTO_INCREMENT,
    message VARCHAR(2000) NOT NULL,
    time_Stamp DATETIME NOT NULL,
    User_ID INT NOT NULL,
    toUser_ID INT NOT NULL,
    PRIMARY KEY (Message_ID)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

CREATE TABLE IF NOT EXISTS MessageUsers (
    User_ID INT NOT NULL,
    Message_ID INT NOT NULL,
    CONSTRAINT message_ID_fk FOREIGN KEY (Message_ID) REFERENCES Messages(Message_ID) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- Seed default admin user with BCrypt hashed password 'admin'
INSERT INTO Admin (fname, lname, Email_ID, username, password, Gender)
SELECT 'Suwaid', 'Aslam', 'Suwaid@admin.com', 'admin', '$2a$12$e8gVwOQvO3q0n2qj6tW0teOQk0wW2c6.0UvF6B6K9bW4A1a7rK9.W', 'Male'
WHERE NOT EXISTS (SELECT 1 FROM Admin WHERE username = 'admin');
