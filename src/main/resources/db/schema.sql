-- ============================================================
-- MotorPH Payroll System — Increment 0 Schema (DDL)
-- Engine: MySQL 8.0  |  Charset: utf8mb4
-- Idempotent: safe to re-run.
-- ============================================================
CREATE DATABASE IF NOT EXISTS payrollsystem_db
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE payrollsystem_db;

-- ---------- Lookup tables ----------
CREATE TABLE IF NOT EXISTS birthday (
    birthday_id INT AUTO_INCREMENT PRIMARY KEY,
    birth_date  DATE NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS address (
    address_id   INT AUTO_INCREMENT PRIMARY KEY,
    full_address VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS employment_status (
    status_id   INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `position` (
    position_id   INT AUTO_INCREMENT PRIMARY KEY,
    position_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS allowance (
    allowance_id   INT AUTO_INCREMENT PRIMARY KEY,
    allowance_name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS leave_type (
    leave_type_id INT AUTO_INCREMENT PRIMARY KEY,
    type_name     VARCHAR(50) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS approval_status (
    status_id   INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS roles (
    role_id   INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS account_status (
    status_id   INT AUTO_INCREMENT PRIMARY KEY,
    status_name VARCHAR(20) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- ---------- Core: employee ----------
CREATE TABLE IF NOT EXISTS employee (
    employee_id            VARCHAR(20)  PRIMARY KEY,
    last_name              VARCHAR(100) NOT NULL,
    first_name             VARCHAR(100) NOT NULL,
    birthday_id            INT          NULL,
    address_id             INT          NULL,
    phone_number           VARCHAR(30)  NULL,
    status_id              INT          NULL,
    position_id            INT          NULL,
    supervisor_employee_id VARCHAR(20)  NULL,
    CONSTRAINT fk_emp_birthday   FOREIGN KEY (birthday_id) REFERENCES birthday(birthday_id)            ON DELETE SET NULL,
    CONSTRAINT fk_emp_address    FOREIGN KEY (address_id)  REFERENCES address(address_id)              ON DELETE SET NULL,
    CONSTRAINT fk_emp_status     FOREIGN KEY (status_id)   REFERENCES employment_status(status_id),
    CONSTRAINT fk_emp_position   FOREIGN KEY (position_id) REFERENCES `position`(position_id),
    CONSTRAINT fk_emp_supervisor FOREIGN KEY (supervisor_employee_id) REFERENCES employee(employee_id) ON DELETE SET NULL,
    INDEX idx_emp_status   (status_id),
    INDEX idx_emp_position (position_id),
    INDEX idx_emp_super    (supervisor_employee_id)
) ENGINE=InnoDB;

-- ---------- One-to-one detail tables ----------
CREATE TABLE IF NOT EXISTS compensation (
    employee_id             VARCHAR(20) PRIMARY KEY,
    basic_salary            DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    gross_semi_monthly_rate DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    hourly_rate             DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_comp_emp FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS employee_government_id (
    employee_id       VARCHAR(20) PRIMARY KEY,
    sss_number        VARCHAR(20) NULL,
    philhealth_number VARCHAR(20) NULL,
    tin_number        VARCHAR(20) NULL,
    pagibig_number    VARCHAR(20) NULL,
    CONSTRAINT fk_gov_emp FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------- Many-to-many: allowances ----------
CREATE TABLE IF NOT EXISTS employee_allowance (
    employee_allowance_id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id           VARCHAR(20) NOT NULL,
    allowance_id          INT NOT NULL,
    amount                DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT uq_emp_allowance UNIQUE (employee_id, allowance_id),
    CONSTRAINT fk_ea_emp       FOREIGN KEY (employee_id)  REFERENCES employee(employee_id) ON DELETE CASCADE,
    CONSTRAINT fk_ea_allowance FOREIGN KEY (allowance_id) REFERENCES allowance(allowance_id),
    INDEX idx_ea_allowance (allowance_id)
) ENGINE=InnoDB;

-- ---------- Attendance ----------
CREATE TABLE IF NOT EXISTS time_and_attendance (
    attendance_id   INT AUTO_INCREMENT PRIMARY KEY,
    employee_id     VARCHAR(20) NOT NULL,
    attendance_date DATE NOT NULL,
    time_in         TIME NULL,
    time_out        TIME NULL,
    CONSTRAINT fk_att_emp FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE CASCADE,
    INDEX idx_att_emp  (employee_id),
    INDEX idx_att_date (attendance_date)
) ENGINE=InnoDB;

-- ---------- Leave ----------
CREATE TABLE IF NOT EXISTS leave_request (
    leave_id      VARCHAR(36) PRIMARY KEY,
    employee_id   VARCHAR(20)  NOT NULL,
    employee_name VARCHAR(200) NOT NULL,
    leave_type_id INT NOT NULL,
    start_date    DATE NOT NULL,
    end_date      DATE NOT NULL,
    reason        VARCHAR(500) NULL,
    status_id     INT NOT NULL,
    requested_at  DATETIME NOT NULL,
    reviewed_by   VARCHAR(100) NULL,
    reviewed_at   DATETIME NULL,
    CONSTRAINT fk_lr_emp    FOREIGN KEY (employee_id)   REFERENCES employee(employee_id) ON DELETE CASCADE,
    CONSTRAINT fk_lr_type   FOREIGN KEY (leave_type_id) REFERENCES leave_type(leave_type_id),
    CONSTRAINT fk_lr_status FOREIGN KEY (status_id)     REFERENCES approval_status(status_id),
    INDEX idx_lr_emp    (employee_id),
    INDEX idx_lr_type   (leave_type_id),
    INDEX idx_lr_status (status_id)
) ENGINE=InnoDB;

-- ---------- Users / RBAC ----------
CREATE TABLE IF NOT EXISTS user_account (
    account_id    INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    employee_id   VARCHAR(20)  NULL,
    status_id     INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_ua_emp    FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE SET NULL,
    CONSTRAINT fk_ua_status FOREIGN KEY (status_id)   REFERENCES account_status(status_id),
    INDEX idx_ua_emp (employee_id)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS user_role (
    account_id INT NOT NULL,
    role_id    INT NOT NULL,
    PRIMARY KEY (account_id, role_id),
    CONSTRAINT fk_ur_account FOREIGN KEY (account_id) REFERENCES user_account(account_id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role    FOREIGN KEY (role_id)    REFERENCES roles(role_id),
    INDEX idx_ur_role (role_id)
) ENGINE=InnoDB;

-- ---------- Audit ----------
CREATE TABLE IF NOT EXISTS audit_log (
    audit_id      BIGINT AUTO_INCREMENT PRIMARY KEY,
    performed_by  VARCHAR(100) NULL,
    action        VARCHAR(100) NULL,
    target_entity VARCHAR(100) NULL,
    target_id     VARCHAR(100) NULL,
    details       TEXT NULL,
    performed_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_when (performed_at),
    INDEX idx_audit_who  (performed_by)
) ENGINE=InnoDB;

-- ============================================================
-- INCREMENT 2 (DDL frozen now, not seeded in Increment 0)
-- ============================================================
CREATE TABLE IF NOT EXISTS payroll_period (
    period_id   INT AUTO_INCREMENT PRIMARY KEY,
    period_name VARCHAR(100) NOT NULL,
    start_date  DATE NOT NULL,
    end_date    DATE NOT NULL,
    status      ENUM('OPEN','CLOSED') NOT NULL DEFAULT 'OPEN',
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS payroll_record (
    record_id            INT AUTO_INCREMENT PRIMARY KEY,
    period_id            INT NOT NULL,
    employee_id          VARCHAR(20) NOT NULL,
    basic_salary         DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    gross_pay            DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    sss_deduction        DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    philhealth_deduction DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    pagibig_deduction    DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    withholding_tax      DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    total_deductions     DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    net_pay              DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    computed_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pr_period FOREIGN KEY (period_id)   REFERENCES payroll_period(period_id) ON DELETE CASCADE,
    CONSTRAINT fk_pr_emp    FOREIGN KEY (employee_id) REFERENCES employee(employee_id),
    CONSTRAINT uq_pr_period_emp UNIQUE (period_id, employee_id),
    INDEX idx_pr_emp (employee_id)
) ENGINE=InnoDB;
