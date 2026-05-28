CREATE DATABASE UniversityComplaintDB;
GO

USE UniversityComplaintDB;
GO

CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(120) NOT NULL,
    email NVARCHAR(120) NOT NULL UNIQUE,
    password_hash NVARCHAR(255) NOT NULL,
    role NVARCHAR(20) NOT NULL CHECK (role IN ('STUDENT', 'ADMIN')),
    department NVARCHAR(100),
    is_active BIT NOT NULL DEFAULT 1,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME()
);
GO

CREATE TABLE complaints (
    id INT IDENTITY(1,1) PRIMARY KEY,
    student_id INT NOT NULL,
    category NVARCHAR(30) NOT NULL CHECK (category IN ('ACADEMIC', 'FACILITY', 'ADMINISTRATIVE', 'HARASSMENT', 'OTHERS')),
    title NVARCHAR(150) NOT NULL,
    description NVARCHAR(MAX) NOT NULL,
    attachment_path NVARCHAR(500),
    status NVARCHAR(20) NOT NULL CHECK (status IN ('PENDING', 'IN_PROGRESS', 'RESOLVED', 'REJECTED')) DEFAULT 'PENDING',
    submitted_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    last_updated_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_complaints_users FOREIGN KEY (student_id) REFERENCES users(id)
);
GO

CREATE TABLE complaint_responses (
    id INT IDENTITY(1,1) PRIMARY KEY,
    complaint_id INT NOT NULL,
    responder_user_id INT NOT NULL,
    message NVARCHAR(MAX) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT SYSDATETIME(),
    CONSTRAINT FK_responses_complaints FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE,
    CONSTRAINT FK_responses_users FOREIGN KEY (responder_user_id) REFERENCES users(id)
);
GO

CREATE INDEX IX_users_email ON users(email);
CREATE INDEX IX_complaints_student_id ON complaints(student_id);
CREATE INDEX IX_complaints_status ON complaints(status);
CREATE INDEX IX_complaints_submitted_at ON complaints(submitted_at);
CREATE INDEX IX_responses_complaint_id ON complaint_responses(complaint_id);
GO

-- Default admin account (password hash for plain text: admin123)
INSERT INTO users (full_name, email, password_hash, role, department)
VALUES ('System Admin', 'admin@university.edu', 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=', 'ADMIN', 'Administration');
GO
