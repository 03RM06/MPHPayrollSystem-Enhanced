# User Guide

## Table of Contents

1. [Login](#1-login)
2. [Dashboard](#2-dashboard)
3. [Employee Management](#3-employee-management)
4. [Payroll](#4-payroll)
5. [Audit Log](#5-audit-log)
6. [User Management (Admin only)](#6-user-management-admin-only)
7. [Roles and Permissions](#7-roles-and-permissions)

---

## 1. Login

Open the application. The Login window is the first screen displayed.

- Enter your **username** and **password** and click **Login**.
- On success the main window opens, showing the Dashboard and a sidebar with the navigation items your role is permitted to access.
- On failure (wrong credentials or inactive account) an error message is shown and the login fields are cleared. No session is created.
- Logging in is recorded in the audit log (`LOGIN` action on `user_account`).

---

## 2. Dashboard

The Dashboard is the landing screen after login. It is visible to every role.

### KPI Cards

Four summary cards are displayed across the top of the panel. Values are loaded from the database in a background thread each time the panel is opened or when **Refresh** is clicked.

| Card | What it shows |
|------|--------------|
| **Total Employees (Regular)** | Count of employees whose employment status is `REGULAR` |
| **Open Payroll Period** | Name of the currently open payroll period, or `None` if no period is open |
| **Pending Leave Requests** | Count of leave requests with a status of `Pending` |
| **Total Payroll Records** | Total count of payroll record rows across all periods |

### Recent Activity

A table below the KPI cards shows the 10 most recent entries from the audit log.

| Column | Description |
|--------|-------------|
| Timestamp | Date and time the event was recorded (`yyyy-MM-dd HH:mm:ss`) |
| User | Username of the actor |
| Action | Audit action label (e.g., `LOGIN`, `CREATE_USER`) |
| Details | Free-text description of what changed |

### Refresh

Click **Refresh** (top-right of the panel) to reload all KPI values and the activity table from the database.

---

## 3. Employee Management

Navigate to **Employees** in the sidebar. This section is available to roles with the `VIEW_ALL_EMPLOYEES` permission (ADMIN, HR, FINANCE, IT).

### Employee Table

The table lists all employees with the following columns:

`Emp ID` · `Last Name` · `First Name` · `Birthday` · `Address` · `Phone No.` · `SSS No.` · `PhilHealth No.` · `TIN No.` · `Pag-IBIG No.` · `Status` · `Position` · `Supervisor` · `Basic Salary` · `Semi-Monthly` · `Hourly Rate`

Click a column header to sort. Click a row to populate the **Employee Details** panel below with the selected employee's key fields.

### Search

Type a name or employee ID in the **Search** field and click **Search** (or press Enter). The filter is case-insensitive and matches against the Employee ID, Last Name, and First Name columns simultaneously. Click **Clear** to remove the filter and show all employees again.

### Action Buttons

| Button | Visible to | Description |
|--------|------------|-------------|
| Refresh | All | Reloads the full employee list from the database |
| Create Employee | ADMIN, HR | Opens the Create Employee form in a new window |
| Edit Employee | ADMIN, HR | Currently under development — selecting an employee and clicking Edit will display an informational message |
| Delete Employee | ADMIN only | Permanently deletes the selected employee after a confirmation prompt. This action cannot be undone. |
| View Details | All (with a row selected) | Opens the employee detail view in a separate window |
| Clear Selection | All (with a row selected) | Deselects the current row and clears the detail fields |

Edit and Delete require a row to be selected first. The Delete button is only visible to ADMIN users.

---

## 4. Payroll

Navigate to **Payroll** in the sidebar. This section is visible to roles with `PROCESS_PAYROLL`, `APPROVE_PAYROLL`, or `VIEW_ALL_PAYSLIPS` (ADMIN, HR, FINANCE).

The Payroll panel is split into two sections: **Payroll Periods** (top) and **Payroll Records** (bottom).

### Payroll Periods Table

| Column | Description |
|--------|-------------|
| `#` | Internal period ID |
| `Period Name` | Descriptive name (e.g., "June 2025 — Period 1") |
| `Start Date` | First day of the pay period |
| `End Date` | Last day of the pay period |
| `Status` | `OPEN` or `CLOSED` |

### Toolbar Actions

| Button | Available when | Description |
|--------|----------------|-------------|
| **New Period** | Always | Opens a dialog to define a new payroll period (name, start date, end date). The new period starts in `OPEN` status. |
| **Run Payroll** | A period with `OPEN` status is selected | Computes payroll for every employee with `REGULAR` status and saves a record for each. Employees without a positive basic salary are skipped. A confirmation dialog is shown before the computation begins. |
| **Close Period** | A period with `OPEN` status is selected | Marks the period as `CLOSED`. Closed periods cannot be re-opened. |
| **View Records** | Any period is selected | Loads the per-employee payroll records for the selected period into the bottom table. |
| **Refresh** | Always | Reloads the list of payroll periods from the database. |

### Payroll Records Table

Populated after clicking **View Records** or immediately after a successful **Run Payroll** operation.

| Column | Description |
|--------|-------------|
| Employee ID | Employee's ID |
| Name | Last name, First name |
| Basic Salary | Monthly basic salary |
| Gross Pay | Salary plus allowances before deductions |
| SSS | SSS employee contribution (2024 schedule) |
| PhilHealth | PhilHealth employee share (2024 rates) |
| Pag-IBIG | HDMF employee contribution (2024 rules) |
| W/H Tax | Withholding tax computed on taxable income (TRAIN Law) |
| Total Deductions | Sum of SSS + PhilHealth + Pag-IBIG + W/H Tax |
| Net Pay | Gross Pay minus Total Deductions |
| Computed At | Timestamp when the record was saved |

---

## 5. Audit Log

Navigate to **Audit Logs** in the sidebar. This section is visible to **ADMIN** and **IT** roles (`VIEW_SYSTEM_LOGS` permission).

The panel is read-only. It displays all audit events recorded by the system.

| Column | Description |
|--------|-------------|
| Timestamp | Date and time the event was recorded |
| Performed By | Username of the actor (or `SYSTEM` for automated events) |
| Action | Action label, e.g. `LOGIN`, `CREATE_USER`, `DEACTIVATE_USER` |
| Target Entity | The type of record that was affected (e.g., `UserAccount`) |
| Target ID | Identifier of the affected record |
| Details | Free-text description |

### Filtering

Type a username in the **Filter by user** field and click **Filter** to show only events performed by that user. Click **Refresh** to clear the filter and reload all records (up to 500 most recent entries).

---

## 6. User Management (Admin only)

Navigate to **User Management** in the sidebar. This section is visible to roles that have `MANAGE_PERMISSIONS`, `CREATE_USER`, or `EDIT_USER` (ADMIN and IT).

The panel lists all system user accounts with the following columns:

`Account ID` · `Username` · `Employee ID` · `Role` · `Status`

### Adding a New User

1. Click **Add User**.
2. Fill in the dialog fields:
   - **Username** — required; must be unique
   - **Password** — required; stored as a SHA-256 hash
   - **Employee ID** — optional; links the account to an employee record
   - **Role** — select from ADMIN, HR, FINANCE, IT, EMPLOYEE
3. Click **Create**. The new account is created with `ACTIVE` status. The action is written to the audit log.

### Editing a User

1. Select a row in the table.
2. Click **Edit User**.
3. The dialog pre-populates with the current username, employee ID, and role. Modify as needed.
4. Click **Save**. Note that the edit form does **not** include a password field — use a separate password-reset flow if that is needed. The action is written to the audit log.

### Deactivating a User

1. Select a row whose **Status** is `ACTIVE`.
2. Click **Deactivate**.
3. The account's status is set to `INACTIVE`. The user can no longer log in. The account record is not deleted.

Two safety guards are enforced before deactivation is allowed:

- **Self-deactivation is blocked** — you cannot deactivate the account you are currently logged in with.
- **Last ADMIN guard** — if the selected account is the last active ADMIN account in the system, deactivation is blocked to prevent a lockout.

### Reactivating a User

1. Select a row whose **Status** is `INACTIVE`.
2. The **Deactivate** button label changes to **Reactivate**.
3. Click **Reactivate**. The account's status is set back to `ACTIVE`.

All deactivation and reactivation events are written to the audit log.

---

## 7. Roles and Permissions

The system defines five roles. Permissions are assigned at the code level; there is no in-app interface to change which permissions belong to a role.

### Role Summary

| Role | Display Name | Description |
|------|-------------|-------------|
| ADMIN | Administrator | Full system access — bypasses all permission checks |
| HR | Human Resources | Employee, leave, report, and partial payroll management |
| FINANCE | Finance | Payroll approval, financial reports, and salary deduction editing |
| IT | IT Support | System maintenance, user management, and audit log access |
| EMPLOYEE | Employee | Personal information, own payslip, and leave requests only |

### Navigation Visibility by Role

The sidebar shows only the sections the logged-in role can access.

| Section | ADMIN | HR | FINANCE | IT | EMPLOYEE |
|---------|:-----:|:--:|:-------:|:--:|:--------:|
| Dashboard | Yes | Yes | Yes | Yes | Yes |
| Employees | Yes | Yes | Yes | Yes | — |
| Attendance | Yes | Yes | — | — | — |
| Payroll | Yes | Yes | Yes | — | — |
| Leave Management | Yes | Yes | — | — | Yes |
| Reports | Yes | Yes | Yes | — | — |
| Audit Logs | Yes | — | — | Yes | — |
| User Management | Yes | Yes | — | Yes | — |

### Detailed Permission Map

| Permission | ADMIN | HR | FINANCE | IT | EMPLOYEE |
|-----------|:-----:|:--:|:-------:|:--:|:--------:|
| Create employee | Yes | Yes | — | — | — |
| Edit employee | Yes | Yes | — | — | — |
| Delete employee | Yes | — | — | — | — |
| View all employees | Yes | Yes | Yes | Yes | — |
| View own employee info | Yes | — | — | — | Yes |
| Process payroll | Yes | Yes | — | — | — |
| Approve payroll | Yes | — | Yes | — | — |
| View all payslips | Yes | Yes | Yes | — | — |
| View own payslip | Yes | — | — | — | Yes |
| Edit salary deduction | Yes | — | Yes | — | — |
| Approve leave | Yes | Yes | — | — | — |
| View all leave requests | Yes | Yes | — | — | — |
| Create leave request | Yes | Yes | — | — | Yes |
| View own leave | Yes | Yes | — | — | Yes |
| Generate reports | Yes | Yes | Yes | — | — |
| View financial reports | Yes | — | Yes | — | — |
| View attendance reports | Yes | Yes | — | — | — |
| View system logs | Yes | — | — | Yes | — |
| Manage permissions | Yes | — | — | Yes | — |
| Create user | Yes | Yes | — | Yes | — |
| Edit user | Yes | — | — | Yes | — |
| View all users | Yes | — | — | Yes | — |
| System maintenance | Yes | — | — | Yes | — |
