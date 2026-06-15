package Model;
 
/** All discrete permissions in the system. */
public enum Permission {
    // User Management
    CREATE_USER("Create new user accounts"),
    EDIT_USER("Edit existing user accounts"),
    DELETE_USER("Delete user accounts"),
    VIEW_ALL_USERS("View all user accounts"),
 
    // Employee Management
    CREATE_EMPLOYEE("Create new employee records"),
    EDIT_EMPLOYEE("Edit employee records"),
    DELETE_EMPLOYEE("Delete employee records"),
    VIEW_ALL_EMPLOYEES("View all employee records"),
    VIEW_OWN_EMPLOYEE_INFO("View own employee information"),
 
    // Payroll Management
    PROCESS_PAYROLL("Process payroll runs"),
    APPROVE_PAYROLL("Approve payroll"),
    VIEW_ALL_PAYSLIPS("View all employee payslips"),
    VIEW_OWN_PAYSLIP("View own payslip"),
    EDIT_SALARY_DEDUCTION("Edit salary deductions"),
 
    // Leave Management
    APPROVE_LEAVE("Approve leave requests"),
    VIEW_ALL_LEAVE_REQUESTS("View all leave requests"),
    CREATE_LEAVE_REQUEST("Create leave requests"),
    VIEW_OWN_LEAVE("View own leave history"),
 
    // Reports
    GENERATE_REPORTS("Generate system reports"),
    VIEW_FINANCIAL_REPORTS("View financial reports"),
    VIEW_ATTENDANCE_REPORTS("View attendance reports"),
 
    // System Administration
    SYSTEM_MAINTENANCE("Perform system maintenance"),
    VIEW_SYSTEM_LOGS("View system logs"),
    MANAGE_PERMISSIONS("Manage user permissions"),
 
    // Basic Access
    LOGIN("Basic login access"),
    VIEW_DASHBOARD("View dashboard");
 
    private final String description;
    Permission(String description) { this.description = description; }
    public String getDescription() { return description; }
}
 