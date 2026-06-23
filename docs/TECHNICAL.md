# Technical Overview

## Architecture

MPHPayrollSystem-Enhanced is a layered desktop application built with Java Swing and JDBC. There is no web layer; all UI and business logic runs in a single JVM process against a MySQL 8.0 database.

The codebase is organized into four top-level packages (no `com.*` prefix):

```
src/main/java/
├── Model/      — POJOs / domain objects
├── DAO/        — JDBC data-access objects
├── Services/   — Session management, audit logging, payroll computation
└── ui/         — Swing panels and frames
```

The UI layer calls Services and DAOs directly; there is no intermediate HTTP or RPC layer.

---

## Package Summary

### `Model`

Plain Java objects that represent domain concepts. Key classes:

- `Employee` — full employee record with nested status, position, compensation, and government ID fields
- `UserAccount` — system user with username, password hash, optional employee link, role, and status
- `Role` (enum) — the five system roles; each enum constant carries its own `Set<Permission>`
- `Permission` (enum) — all discrete permission constants used at runtime
- `PayrollPeriod` — a named pay period with a start date, end date, and OPEN/CLOSED status
- `PayrollRecord` — a computed payroll result for one employee within one period
- `AuditLog` — a single audit event row

### `DAO`

JDBC data-access objects. Each DAO opens a connection from `Database.getInstance()`, executes prepared statements, and maps `ResultSet` rows to model objects.

- `Database` — singleton that holds (and lazily reconnects) a single `java.sql.Connection` for the session; credentials are read from `config.properties` at class load time
- `EmployeeDAO` — CRUD for `employee` and its related detail tables
- `UserAccountDAO` — CRUD for `user_account` and `user_role`; handles SHA-256 password hashing and role resolution
- `AuditLogDAO` — insert and query operations for `audit_log`
- `PayrollPeriodDAO` — CRUD for `payroll_period`; exposes `getOpenPeriod()` and `closePeriod()`
- `PayrollRecordDAO` — upsert (`INSERT ... ON DUPLICATE KEY UPDATE`) and read for `payroll_record`

### `Services`

Business logic and application-level services:

- `SessionManager` — singleton that stores the authenticated `UserAccount` for the session lifetime; provides `hasPermission(Permission)` for runtime permission checks; ADMIN bypasses all checks automatically
- `AuditLogService` — static facade over `AuditLogDAO`; resolves the current username from `SessionManager` so callers pass only `(action, targetEntity, targetId, details)`
- `PayrollService` — orchestrates payroll computation for a single employee or a full period batch
- `SalaryDeduction` — computes statutory deductions (SSS, PhilHealth, Pag-IBIG) by delegating to `StatutoryRates`
- `WithholdingTax` — computes Withholding Tax on taxable income (gross minus statutory deductions) using the TRAIN Law bracket table in `StatutoryRates`
- `StatutoryRates` — constant class holding the 2024 deduction rates and bracket tables (see below)
- `RBACService` — helper for fine-grained employee/payslip access checks

### `ui`

Swing panels and frames, each responsible for one section of the application:

- `Login` — credentials form and authentication entry point
- `MainShell` — primary `JFrame`; houses a `SidebarPanel` on the left and a swappable content area on the right
- `SidebarPanel` — navigation sidebar; builds its button list by checking permissions at load time
- `DashboardPanel` — KPI cards + recent activity table; loads data off the EDT via `SwingWorker`
- `EmployeePage` — employee search/management panel
- `PayrollHistoryPanel` — payroll period management and per-period record viewer
- `AuditLogPanel` — read-only audit log viewer with user filter
- `UserManagementPanel` — user account CRUD with activate/deactivate controls
- `LeaveManagement` — leave request submission and approval (embedded in `MainShell` via content-pane wrapping)

---

## Database Connection

Configuration is read from `src/main/resources/config.properties` on the classpath at startup:

| Key | Description |
|-----|-------------|
| `db.host` | MySQL hostname |
| `db.port` | MySQL port |
| `db.name` | Database name |
| `db.user` | MySQL username |
| `db.pass` | MySQL password |

`Database` is a singleton that holds a single `Connection` for the lifetime of the JVM. If `connection.isClosed()` returns true when a DAO requests it, the singleton reconnects transparently. The JDBC URL includes `useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true`.

Because all DAOs share the same connection instance, concurrent access is possible if Swing worker threads run simultaneously (which they do on the Dashboard). DAOs that need transactions (e.g., `UserAccountDAO.create`) temporarily set `autoCommit=false` and restore it in a `finally` block.

---

## Authentication Flow

1. The user enters credentials in the `Login` form.
2. `UserAccountDAO.login(username, plainPassword)` is called:
   a. `findByUsername` fetches the `user_account` row and resolves the user's highest-priority role from `user_role`.
   b. The supplied plain-text password is hashed with SHA-256 and compared to the stored `password_hash`. MySQL's `SHA2(text, 256)` and Java's `MessageDigest("SHA-256")` produce identical output for the same UTF-8 input.
   c. On match, a `LOGIN` audit event is written and the `UserAccount` object is returned.
3. `SessionManager.getInstance().setCurrentUser(userAccount)` stores the authenticated user.
4. `MainShell` opens, and `SidebarPanel` reads permissions from `SessionManager` to build the visible navigation set.
5. On logout, `SessionManager.logout()` sets `currentUser` to `null` and the Login form is re-displayed.

---

## Permission System

Permissions are defined as the `Permission` enum in `Model/Permission.java`. Each `Role` enum constant carries a fixed `EnumSet<Permission>` declared in `Model/Role.java`. There are no `permission` or `role_permission` database tables; the mapping exists entirely in code.

At runtime, `SessionManager.hasPermission(Permission p)` is the single point of permission evaluation:

- If the current user's role is `ADMIN`, the method returns `true` for every permission without checking.
- Otherwise it delegates to `role.hasPermission(p)`, which checks the role's `EnumSet`.

Panels call `hasPermission` in two places:
- **At build time** — `SidebarPanel` conditionally adds each navigation button.
- **At action time** — individual action handlers in `EmployeePage` and `UserManagementPanel` re-check permissions before executing write operations.

---

## Audit Logging

`AuditLogService.log(action, targetEntity, targetId, details)` is a static method. It:

1. Reads the current username from `SessionManager`; falls back to `"SYSTEM"` when no session is active.
2. Delegates to `AuditLogDAO.log(performedBy, action, targetEntity, targetId, details)`.
3. Writes an `INSERT` to the `audit_log` table. The `performed_at` timestamp is set by MySQL's `DEFAULT CURRENT_TIMESTAMP`.

Callers include `UserAccountDAO` (on `LOGIN`), `UserManagementPanel` (on `CREATE_USER`, `EDIT_USER`, `DEACTIVATE_USER`, `REACTIVATE_USER`), and any other panel that performs a write operation.

`AuditLogService.getAll()` returns up to 500 entries, newest first. `getByUser(username)` returns up to 200 entries for a specific actor.

---

## Statutory Deduction Rates

All 2024 statutory deduction rates are hardcoded in `Services/StatutoryRates.java`. They are **not** stored in the database.

### SSS — SSS Circular 2023-033 (effective January 2024)

- Employee share rate: **4.5%** of the Monthly Salary Credit (MSC)
- MSC is derived by rounding the monthly salary to the nearest ₱500 step
- MSC floor: ₱4,000 | MSC ceiling: ₱30,000

### PhilHealth — PhilHealth Circular 2023-0009 (2024 premium rate)

- Employee share rate: **2.5%** of the actual monthly basic salary
- Salary floor: ₱10,000 | Salary ceiling: ₱100,000

### Pag-IBIG / HDMF — HDMF Circular 274

- **1%** of salary for salaries ≤ ₱1,500
- **2%** of salary for salaries > ₱1,500
- Contribution base capped at ₱5,000; maximum employee contribution: **₱100**

### Withholding Tax — TRAIN Law (NIRC as amended, monthly brackets)

Taxable income is computed as gross pay minus statutory deductions. The monthly bracket table in `StatutoryRates.computeWithholdingTax()` follows the six-bracket schedule in effect from January 2023 onwards (₱0 = 0%, ₱20,833+ = 15%, ₱33,333+ = 20%, ₱66,667+ = 25%, ₱166,667+ = 30%, ₱666,667+ = 35%).

---

## Key Class Reference

| Class | Package | Role |
|-------|---------|------|
| `UserAccount` | Model | Represents a system user; holds role and permission accessors |
| `Role` | Model | Enum of the five roles, each with a fixed `Set<Permission>` |
| `Permission` | Model | Enum of all permission constants |
| `Database` | DAO | Singleton JDBC connection manager; reads `config.properties` |
| `UserAccountDAO` | DAO | User CRUD, SHA-256 password operations, role management |
| `SessionManager` | Services | Singleton session state; central `hasPermission()` check |
| `AuditLogService` | Services | Static facade for writing and querying audit log entries |
| `PayrollService` | Services | Orchestrates per-employee and per-period payroll computation |
| `StatutoryRates` | Services | 2024 rate constants and computation methods for SSS/PhilHealth/Pag-IBIG/WHT |
| `MainShell` | ui | Primary JFrame; routes navigation commands to panel instances |
| `SidebarPanel` | ui | Permission-filtered navigation sidebar |
| `DashboardPanel` | ui | KPI cards and recent activity table |
| `EmployeePage` | ui | Employee search, table, and role-gated CRUD actions |
| `AuditLogPanel` | ui | Read-only audit log viewer with user filter |
| `UserManagementPanel` | ui | User account management with activate/deactivate guards |
| `PayrollHistoryPanel` | ui | Period management, payroll run trigger, and record viewer |
