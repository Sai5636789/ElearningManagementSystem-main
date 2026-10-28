-- V2: Add foreign keys and soft-delete flags to Messages, drop redundant MessageUsers table, and add query indexes

-- 1. Drop redundant MessageUsers table
DROP TABLE IF EXISTS MessageUsers;

-- 2. Add per-user deletion flags to Messages so inbox/sentbox deletions do not require a separate join table
ALTER TABLE Messages
    ADD COLUMN deleted_by_sender BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN deleted_by_receiver BOOLEAN NOT NULL DEFAULT FALSE;

-- 3. Add foreign keys on Messages referencing Student sender and receiver
ALTER TABLE Messages
    ADD CONSTRAINT fk_messages_sender FOREIGN KEY (User_ID) REFERENCES Student(stdID) ON DELETE CASCADE,
    ADD CONSTRAINT fk_messages_receiver FOREIGN KEY (toUser_ID) REFERENCES Student(stdID) ON DELETE CASCADE;

-- 4. Add indexes on foreign keys and frequently filtered columns to avoid full table scans
CREATE INDEX idx_courses_teacher ON Courses(teacherID);
CREATE INDEX idx_courses_subject ON Courses(Subject_ID);
CREATE INDEX idx_enrollments_course ON Enrollments(course_ID);
CREATE INDEX idx_enrollments_student ON Enrollments(stdID);
CREATE INDEX idx_messages_sender ON Messages(User_ID);
CREATE INDEX idx_messages_receiver ON Messages(toUser_ID);
CREATE INDEX idx_messages_time ON Messages(time_Stamp);
