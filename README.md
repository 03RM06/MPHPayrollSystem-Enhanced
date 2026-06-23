# MPHPayrollSystem — Enhanced

A Java desktop payroll system built for MotorPH, a Philippine SME. The application manages employee records, attendance, leave requests, and end-to-end payroll computation. Statutory deductions — SSS, PhilHealth, Pag-IBIG, and Withholding Tax — are computed automatically using 2024 government-mandated rates (SSS Circular 2023-033, PhilHealth Circular 2023-0009, HDMF Circular 274, and the TRAIN Law). Access to every feature is restricted by a five-tier role-based access control system, and every mutating operation is written to an audit log for accountability.

## Tech Stack

| Component | Details |
|-----------|---------|
| Language | Java 21 (LTS) |
| Build | Apache Maven 3.x |
| Database | MySQL 8.0 |
| UI | Java Swing |
| JDBC | mysql-connector-j 8.3.0 |
| Date picker | JCalendar 1.4 |
| Reports | JasperReports 7.0.7 |

## Features

- **Employee Management** — search, view, create, and delete employee records including compensation, government IDs (SSS/PhilHealth/TIN/Pag-IBIG), and allowances
- **Payroll Processing with statutory deduction computation** — run payroll for all REGULAR employees within a defined pay period; SSS, PhilHealth, Pag-IBIG, and Withholding Tax are calculated automatically from the employee's basic salary using 2024 rates
- **Payroll History** — browse all payroll periods and view the per-employee breakdown (gross pay, each deduction, and net pay) for any period
- **Audit Logging** — every create, update, deactivate, and login event is recorded with the actor's username, a timestamp, and descriptive details
- **Dashboard with KPI summary** — at-a-glance figures for regular employee headcount, the open payroll period, pending leave requests, and total payroll records computed
- **Role-Based Access Control (ADMIN / HR / FINANCE / IT / EMPLOYEE)** — the sidebar and action buttons are filtered at runtime so each user sees only what their role permits
- **Admin User Management** — create new system accounts, edit usernames and role assignments, and deactivate or reactivate accounts with built-in safety guards

## Prerequisites

- JDK 21 or higher
- MySQL 8.0
- Apache NetBeans (includes a bundled Maven instance) **or** a standalone Maven 3.x installation

## Quick Start

1. **Clone the repository**
   ```
   git clone <repo-url>
   cd MPHPayrollSystem-Enhanced
   ```

2. **Set up the database** — start your MySQL service, then run the schema and seed scripts
   ```
   mysql -u root -p < src/main/resources/db/schema.sql
   mysql -u root -p < src/main/resources/db/seed.sql
   ```

3. **Configure the connection** — edit `src/main/resources/config.properties` and set your MySQL credentials:
   ```properties
   db.host=localhost
   db.port=3306
   db.name=payrollsystem_db
   db.user=root
   db.pass=your_password
   ```

4. **Build** — from the project root:
   ```
   mvn clean package
   ```
   Or inside NetBeans: **Run → Clean and Build Project**.

5. **Run**
   ```
   mvn exec:java
   ```
   Default login: username `admin`, password `password123`.

For a full walkthrough of installation, configuration, and features see the [`docs/`](docs/) directory.
