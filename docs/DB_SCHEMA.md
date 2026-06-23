# Database Schema

Database: `payrollsystem_db` — MySQL 8.0, charset `utf8mb4_unicode_ci`

Schema source: `src/main/resources/db/schema.sql`  
Seed source: `src/main/resources/db/seed.sql`

> **Note on permissions:** The permission and role-permission mapping is implemented entirely in Java code (`Model/Role.java` and `Model/Permission.java`) and does not have corresponding database tables. There are no `permission` or `role_permission` tables in this schema.

---

## Table Index

**Lookup / Reference**
- [`birthday`](#birthday)
- [`address`](#address)
- [`employment_status`](#employment_status)
- [`position`](#position)
- [`allowance`](#allowance)
- [`leave_type`](#leave_type)
- [`approval_status`](#approval_status)
- [`roles`](#roles)
- [`account_status`](#account_status)

**Core Employee**
- [`employee`](#employee)
- [`compensation`](#compensation)
- [`employee_government_id`](#employee_government_id)
- [`employee_allowance`](#employee_allowance)

**Time and Leave**
- [`time_and_attendance`](#time_and_attendance)
- [`leave_request`](#leave_request)

**Users and Access**
- [`user_account`](#user_account)
- [`user_role`](#user_role)

**Audit**
- [`audit_log`](#audit_log)

**Payroll**
- [`payroll_period`](#payroll_period)
- [`payroll_record`](#payroll_record)

---

## Lookup / Reference Tables

### `birthday`

Stores birth dates as a separate table to normalize the `employee` record.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `birthday_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `birth_date` | DATE | No | Employee's date of birth |

---

### `address`

Stores full address strings for employees.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `address_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `full_address` | VARCHAR(255) | No | Complete address as a single string |

---

### `employment_status`

Lookup for employee employment statuses.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `status_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `status_name` | VARCHAR(50) | No (UNIQUE) | Status label |

**Seeded values:**

| status_id | status_name |
|-----------|-------------|
| 1 | PROBATIONARY |
| 2 | REGULAR |
| 3 | RESIGNED |
| 4 | TERMINATED |
| 5 | RETIRED |

---

### `position`

Lookup for job positions.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `position_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `position_name` | VARCHAR(100) | No (UNIQUE) | Job title |

**Seeded values:**

| position_id | position_name |
|-------------|--------------|
| 1 | CEO |
| 2 | HR Manager |
| 3 | Finance Officer |
| 4 | IT Support |
| 5 | Rank and File |

---

### `allowance`

Lookup for allowance types.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `allowance_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `allowance_name` | VARCHAR(100) | No (UNIQUE) | Allowance label |

**Seeded values:** Rice Subsidy (1), Phone Allowance (2), Clothing Allowance (3)

---

### `leave_type`

Lookup for types of leave.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `leave_type_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `type_name` | VARCHAR(50) | No (UNIQUE) | Leave category label |

**Seeded values:** Emergency Leave (1), Maternity Leave (2), Paternity Leave (3), Sick Leave (4), Vacation Leave (5)

---

### `approval_status`

Lookup for the lifecycle status of a leave request.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `status_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `status_name` | VARCHAR(20) | No (UNIQUE) | Status label |

**Seeded values:**

| status_id | status_name |
|-----------|-------------|
| 1 | Pending |
| 2 | Approved |
| 3 | Rejected |

---

### `roles`

Lookup for system user roles.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `role_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `role_name` | VARCHAR(20) | No (UNIQUE) | Role identifier |

**Seeded values:**

| role_id | role_name |
|---------|-----------|
| 1 | ADMIN |
| 2 | HR |
| 3 | FINANCE |
| 4 | IT |
| 5 | EMPLOYEE |

---

### `account_status`

Lookup for the active/inactive state of a user account.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `status_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `status_name` | VARCHAR(20) | No (UNIQUE) | Status label |

**Seeded values:**

| status_id | status_name |
|-----------|-------------|
| 1 | ACTIVE |
| 2 | INACTIVE |

---

## Core Employee Tables

### `employee`

Central employee record. Related detail tables (`compensation`, `employee_government_id`, `employee_allowance`) share the same `employee_id` primary key.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `employee_id` | VARCHAR(20) | No (PK) | Business key (e.g., `E001`) |
| `last_name` | VARCHAR(100) | No | Last name |
| `first_name` | VARCHAR(100) | No | First name |
| `birthday_id` | INT | Yes | FK → `birthday.birthday_id` |
| `address_id` | INT | Yes | FK → `address.address_id` |
| `phone_number` | VARCHAR(30) | Yes | Contact number |
| `status_id` | INT | Yes | FK → `employment_status.status_id` |
| `position_id` | INT | Yes | FK → `position.position_id` |
| `supervisor_employee_id` | VARCHAR(20) | Yes | Self-referencing FK → `employee.employee_id` |

**Foreign keys:**
- `fk_emp_birthday` → `birthday(birthday_id)` ON DELETE SET NULL
- `fk_emp_address` → `address(address_id)` ON DELETE SET NULL
- `fk_emp_status` → `employment_status(status_id)`
- `fk_emp_position` → `position(position_id)`
- `fk_emp_supervisor` → `employee(employee_id)` ON DELETE SET NULL

---

### `compensation`

One-to-one with `employee`. Holds salary figures.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `employee_id` | VARCHAR(20) | No (PK) | FK → `employee.employee_id` |
| `basic_salary` | DECIMAL(12,2) | No | Monthly basic salary (default 0.00) |
| `gross_semi_monthly_rate` | DECIMAL(12,2) | No | Semi-monthly gross rate (default 0.00) |
| `hourly_rate` | DECIMAL(12,2) | No | Derived hourly rate (default 0.00) |

**Foreign key:** `fk_comp_emp` → `employee(employee_id)` ON DELETE CASCADE

---

### `employee_government_id`

One-to-one with `employee`. Stores the four Philippine government ID numbers.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `employee_id` | VARCHAR(20) | No (PK) | FK → `employee.employee_id` |
| `sss_number` | VARCHAR(20) | Yes | Social Security System number |
| `philhealth_number` | VARCHAR(20) | Yes | Philippine Health Insurance number |
| `tin_number` | VARCHAR(20) | Yes | Tax Identification Number |
| `pagibig_number` | VARCHAR(20) | Yes | HDMF / Pag-IBIG number |

**Foreign key:** `fk_gov_emp` → `employee(employee_id)` ON DELETE CASCADE

---

### `employee_allowance`

Many-to-many junction between `employee` and `allowance`. Each row records the peso amount of a specific allowance for a specific employee.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `employee_allowance_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `employee_id` | VARCHAR(20) | No | FK → `employee.employee_id` |
| `allowance_id` | INT | No | FK → `allowance.allowance_id` |
| `amount` | DECIMAL(12,2) | No | Monthly allowance amount (default 0.00) |

**Unique constraint:** `uq_emp_allowance (employee_id, allowance_id)` — prevents duplicate allowance entries per employee.

**Foreign keys:**
- `fk_ea_emp` → `employee(employee_id)` ON DELETE CASCADE
- `fk_ea_allowance` → `allowance(allowance_id)`

---

## Time and Leave Tables

### `time_and_attendance`

Daily time-in / time-out records per employee.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `attendance_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `employee_id` | VARCHAR(20) | No | FK → `employee.employee_id` |
| `attendance_date` | DATE | No | The calendar date of the attendance record |
| `time_in` | TIME | Yes | Time the employee clocked in |
| `time_out` | TIME | Yes | Time the employee clocked out |

**Foreign key:** `fk_att_emp` → `employee(employee_id)` ON DELETE CASCADE

---

### `leave_request`

Leave request lifecycle, from submission to approval or rejection.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `leave_id` | VARCHAR(36) | No (PK) | UUID string |
| `employee_id` | VARCHAR(20) | No | FK → `employee.employee_id` |
| `employee_name` | VARCHAR(200) | No | Denormalized name at submission time |
| `leave_type_id` | INT | No | FK → `leave_type.leave_type_id` |
| `start_date` | DATE | No | First day of leave |
| `end_date` | DATE | No | Last day of leave |
| `reason` | VARCHAR(500) | Yes | Optional reason provided by the employee |
| `status_id` | INT | No | FK → `approval_status.status_id` |
| `requested_at` | DATETIME | No | When the request was submitted |
| `reviewed_by` | VARCHAR(100) | Yes | Username of the reviewer |
| `reviewed_at` | DATETIME | Yes | When the review decision was made |

**Foreign keys:**
- `fk_lr_emp` → `employee(employee_id)` ON DELETE CASCADE
- `fk_lr_type` → `leave_type(leave_type_id)`
- `fk_lr_status` → `approval_status(status_id)`

---

## Users and Access Tables

### `user_account`

System login accounts. Each account optionally links to one employee record.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `account_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `username` | VARCHAR(50) | No (UNIQUE) | Login username |
| `password_hash` | VARCHAR(255) | No | SHA-256 hex of the plaintext password |
| `employee_id` | VARCHAR(20) | Yes | FK → `employee.employee_id` (links account to an employee record) |
| `status_id` | INT | No | FK → `account_status.status_id` (default 1 = ACTIVE) |

**Foreign keys:**
- `fk_ua_emp` → `employee(employee_id)` ON DELETE SET NULL
- `fk_ua_status` → `account_status(status_id)`

---

### `user_role`

Assigns roles to user accounts. Composite primary key; a user can in principle hold multiple roles, though the application resolves to a single highest-priority role at login.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `account_id` | INT | No (PK) | FK → `user_account.account_id` |
| `role_id` | INT | No (PK) | FK → `roles.role_id` |

**Foreign keys:**
- `fk_ur_account` → `user_account(account_id)` ON DELETE CASCADE
- `fk_ur_role` → `roles(role_id)`

---

## Audit Table

### `audit_log`

Append-only log of every significant system event.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `audit_id` | BIGINT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `performed_by` | VARCHAR(100) | Yes | Username of the actor (or `SYSTEM`) |
| `action` | VARCHAR(100) | Yes | Action label (e.g., `LOGIN`, `CREATE_USER`, `DEACTIVATE_USER`) |
| `target_entity` | VARCHAR(100) | Yes | Entity type affected (e.g., `UserAccount`, `user_account`) |
| `target_id` | VARCHAR(100) | Yes | Identifier of the affected record |
| `details` | TEXT | Yes | Free-text description of the change |
| `performed_at` | TIMESTAMP | No | Timestamp set automatically by MySQL (`DEFAULT CURRENT_TIMESTAMP`) |

---

## Payroll Tables

### `payroll_period`

Defines a named pay period. Payroll is run against an OPEN period; the period is then CLOSED to prevent re-computation.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `period_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `period_name` | VARCHAR(100) | No | Descriptive name (e.g., "June 2025 — Period 1") |
| `start_date` | DATE | No | First day of the pay period |
| `end_date` | DATE | No | Last day of the pay period |
| `status` | ENUM('OPEN','CLOSED') | No | `OPEN` until closed; defaults to `OPEN` |
| `created_at` | TIMESTAMP | No | Row creation timestamp |

---

### `payroll_record`

One row per employee per payroll period. Stores the computed deduction breakdown and net pay.

| Column | Type | Nullable | Description |
|--------|------|----------|-------------|
| `record_id` | INT | No (PK, AUTO_INCREMENT) | Surrogate key |
| `period_id` | INT | No | FK → `payroll_period.period_id` |
| `employee_id` | VARCHAR(20) | No | FK → `employee.employee_id` |
| `basic_salary` | DECIMAL(12,2) | No | Employee's basic salary at time of run |
| `gross_pay` | DECIMAL(12,2) | No | Gross pay before deductions |
| `sss_deduction` | DECIMAL(12,2) | No | SSS employee contribution |
| `philhealth_deduction` | DECIMAL(12,2) | No | PhilHealth employee share |
| `pagibig_deduction` | DECIMAL(12,2) | No | Pag-IBIG / HDMF employee contribution |
| `withholding_tax` | DECIMAL(12,2) | No | Withholding tax (TRAIN Law) |
| `total_deductions` | DECIMAL(12,2) | No | Sum of all deductions |
| `net_pay` | DECIMAL(12,2) | No | Gross pay minus total deductions |
| `computed_at` | TIMESTAMP | No | When this record was computed |

**Unique constraint:** `uq_pr_period_emp (period_id, employee_id)` — each employee appears at most once per period. Re-running payroll for the same period uses `ON DUPLICATE KEY UPDATE`.

**Foreign keys:**
- `fk_pr_period` → `payroll_period(period_id)` ON DELETE CASCADE
- `fk_pr_emp` → `employee(employee_id)`

---

## Seed Data

All seed data is in `src/main/resources/db/seed.sql` and uses `INSERT IGNORE` so the script is safe to re-run.

### Sample Employees (E001–E005)

| employee_id | Name | Position | Basic Salary |
|-------------|------|----------|-------------|
| E001 | Manuel Garcia | CEO | ₱90,000 |
| E002 | Antonio Lim | HR Manager | ₱52,000 |
| E003 | Bianca Aquino | Finance Officer | ₱45,000 |
| E004 | Isabella Reyes | IT Support | ₱35,000 |
| E005 | Maria Santos | Rank and File | ₱25,000 |

All sample employees have `REGULAR` employment status. E002–E005 report to E001.

### Default User Accounts

All default accounts use password `password123` (stored as its SHA-256 hash).

| account_id | username | role | employee_id |
|------------|----------|------|------------|
| 1 | admin | ADMIN | E001 |
| 2 | hr | HR | E002 |
| 3 | finance | FINANCE | E003 |
| 4 | it | IT | E004 |
| 5 | employee | EMPLOYEE | E005 |

---

## ER Relationships Summary

```
employee (employee_id)
  ├── 1:1  compensation          (employee_id)
  ├── 1:1  employee_government_id (employee_id)
  ├── 1:N  employee_allowance    (employee_id)  → allowance (allowance_id)
  ├── 1:N  time_and_attendance   (employee_id)
  ├── 1:N  leave_request         (employee_id)  → leave_type, approval_status
  ├── 0:1  user_account          (employee_id)  [optional link]
  └── 0:N  payroll_record        (employee_id)  → payroll_period

user_account (account_id)
  ├── N:M  user_role             (account_id, role_id)  → roles (role_id)
  └── FK   account_status        (status_id)

payroll_period (period_id)
  └── 1:N  payroll_record        (period_id)

audit_log — standalone append-only table; no FK relationships
```

Key cascade rules:
- Deleting an `employee` cascades to `compensation`, `employee_government_id`, `employee_allowance`, `time_and_attendance`, and `leave_request`, but sets `user_account.employee_id` to NULL and `payroll_record` is protected by a regular FK (no cascade).
- Deleting a `user_account` cascades to `user_role`.
- Deleting a `payroll_period` cascades to its `payroll_record` rows.
