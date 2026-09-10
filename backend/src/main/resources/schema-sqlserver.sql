-- ===================================================================
-- AI Mock Interview Platform - Microsoft SQL Server Database Schema
-- ===================================================================

-- 1. Roles Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'roles')
BEGIN
    CREATE TABLE roles (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name VARCHAR(50) NOT NULL UNIQUE
    );

    INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');
END;

-- 2. Users Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
    CREATE TABLE users (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        email VARCHAR(255) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        full_name VARCHAR(255) NOT NULL,
        enabled BIT NOT NULL DEFAULT 1,
        account_non_locked BIT NOT NULL DEFAULT 1,
        headline VARCHAR(255) NULL,
        target_role VARCHAR(100) NULL,
        years_of_experience INT NULL DEFAULT 0,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );

    CREATE INDEX idx_users_email ON users(email);
END;

-- 3. User Roles Join Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'user_roles')
BEGIN
    CREATE TABLE user_roles (
        user_id UNIQUEIDENTIFIER NOT NULL,
        role_id BIGINT NOT NULL,
        PRIMARY KEY (user_id, role_id),
        CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
        CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );
END;

-- 4. Interviews Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'interviews')
BEGIN
    CREATE TABLE interviews (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        user_id UNIQUEIDENTIFIER NOT NULL,
        job_role VARCHAR(100) NOT NULL,
        experience_level VARCHAR(50) NOT NULL,
        difficulty VARCHAR(50) NOT NULL,
        interview_type VARCHAR(50) NOT NULL,
        status VARCHAR(50) NOT NULL DEFAULT 'CREATED',
        total_questions INT NOT NULL DEFAULT 5,
        completed_questions INT NOT NULL DEFAULT 0,
        overall_score DECIMAL(5,2) NULL,
        resume_extracted_text NVARCHAR(MAX) NULL,
        start_time DATETIME2 NULL,
        end_time DATETIME2 NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        updated_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_interviews_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    );

    CREATE INDEX idx_interviews_user_id ON interviews(user_id);
    CREATE INDEX idx_interviews_status ON interviews(status);
    CREATE INDEX idx_interviews_created_at ON interviews(created_at DESC);
END;

-- 5. Questions Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'questions')
BEGIN
    CREATE TABLE questions (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        interview_id UNIQUEIDENTIFIER NOT NULL,
        sequence_number INT NOT NULL,
        question_text NVARCHAR(MAX) NOT NULL,
        question_type VARCHAR(50) NOT NULL,
        technology VARCHAR(100) NOT NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_questions_interview FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE,
        CONSTRAINT uq_interview_seq UNIQUE (interview_id, sequence_number)
    );

    CREATE INDEX idx_questions_interview_id ON questions(interview_id);
END;

-- 6. Answers Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'answers')
BEGIN
    CREATE TABLE answers (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        question_id UNIQUEIDENTIFIER NOT NULL UNIQUE,
        answer_text NVARCHAR(MAX) NOT NULL,
        technical_score INT NOT NULL DEFAULT 0,
        relevance_score INT NOT NULL DEFAULT 0,
        clarity_score INT NOT NULL DEFAULT 0,
        depth_score INT NOT NULL DEFAULT 0,
        overall_score INT NOT NULL DEFAULT 0,
        feedback NVARCHAR(MAX) NOT NULL,
        strengths_json NVARCHAR(MAX) NULL,
        weaknesses_json NVARCHAR(MAX) NULL,
        submitted_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_answers_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
    );

    CREATE INDEX idx_answers_question_id ON answers(question_id);
END;

-- 7. Interview Reports Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'interview_reports')
BEGIN
    CREATE TABLE interview_reports (
        id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
        interview_id UNIQUEIDENTIFIER NOT NULL UNIQUE,
        overall_score DECIMAL(5,2) NOT NULL,
        technical_score DECIMAL(5,2) NOT NULL,
        communication_score DECIMAL(5,2) NOT NULL,
        relevance_score DECIMAL(5,2) NOT NULL,
        average_answer_score DECIMAL(5,2) NOT NULL,
        strengths_json NVARCHAR(MAX) NULL,
        weaknesses_json NVARCHAR(MAX) NULL,
        recommended_topics_json NVARCHAR(MAX) NULL,
        improvement_suggestions_json NVARCHAR(MAX) NULL,
        summary NVARCHAR(MAX) NOT NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME(),
        CONSTRAINT fk_reports_interview FOREIGN KEY (interview_id) REFERENCES interviews(id) ON DELETE CASCADE
    );

    CREATE INDEX idx_reports_interview_id ON interview_reports(interview_id);
END;

-- 8. Audit Logs Table
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'audit_logs')
BEGIN
    CREATE TABLE audit_logs (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        user_id UNIQUEIDENTIFIER NULL,
        action VARCHAR(100) NOT NULL,
        resource_type VARCHAR(100) NOT NULL,
        resource_id VARCHAR(255) NULL,
        details NVARCHAR(MAX) NULL,
        ip_address VARCHAR(100) NULL,
        created_at DATETIME2 NOT NULL DEFAULT SYSUTCDATETIME()
    );

    CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
    CREATE INDEX idx_audit_logs_action ON audit_logs(action);
    CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at DESC);
END;
