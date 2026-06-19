    package Model;

    import java.util.EnumSet;
    import java.util.Set;

    /** Role definitions with associated permission sets. */
    public enum Role {

        ADMIN("Administrator", "Full system access",
            EnumSet.allOf(Permission.class)),

        HR("Human Resources", "Employee, leave, and report management",
            EnumSet.of(
                Permission.CREATE_EMPLOYEE, Permission.EDIT_EMPLOYEE,
                Permission.VIEW_ALL_EMPLOYEES, Permission.APPROVE_LEAVE,
                Permission.VIEW_ALL_LEAVE_REQUESTS, Permission.GENERATE_REPORTS,
                Permission.VIEW_ATTENDANCE_REPORTS, Permission.LOGIN,
                Permission.VIEW_DASHBOARD, Permission.CREATE_USER,
                Permission.PROCESS_PAYROLL, Permission.VIEW_ALL_PAYSLIPS,
                Permission.CREATE_LEAVE_REQUEST, Permission.VIEW_OWN_LEAVE)),

        FINANCE("Finance", "Payroll approval and financial reports",
            EnumSet.of(
                Permission.APPROVE_PAYROLL, Permission.VIEW_ALL_PAYSLIPS,
                Permission.EDIT_SALARY_DEDUCTION, Permission.GENERATE_REPORTS,
                Permission.VIEW_FINANCIAL_REPORTS, Permission.VIEW_ALL_EMPLOYEES,
                Permission.LOGIN, Permission.VIEW_DASHBOARD)),

        IT("IT Support", "System maintenance and user management",
             EnumSet.of(
                Permission.EDIT_USER, Permission.VIEW_ALL_USERS,
                 Permission.SYSTEM_MAINTENANCE, Permission.VIEW_SYSTEM_LOGS,
                 Permission.MANAGE_PERMISSIONS, Permission.LOGIN,
                Permission.VIEW_DASHBOARD, Permission.CREATE_USER,
               Permission.VIEW_ALL_EMPLOYEES)),

        EMPLOYEE("Employee", "Personal info, payslips, and leave requests",
            EnumSet.of(
                Permission.VIEW_OWN_EMPLOYEE_INFO, Permission.VIEW_OWN_PAYSLIP,
                Permission.CREATE_LEAVE_REQUEST, Permission.VIEW_OWN_LEAVE,
                Permission.LOGIN, Permission.VIEW_DASHBOARD));

        private final String displayName;
        private final String description;
        private final Set<Permission> permissions;

        Role(String displayName, String description, Set<Permission> permissions) {
            this.displayName = displayName;
            this.description = description;
            this.permissions = permissions;
        }

        public String          getDisplayName()  { return displayName; }
        public String          getDescription()  { return description; }
        public Set<Permission> getPermissions()  { return permissions; }

        public boolean hasPermission(Permission p) { return permissions.contains(p); }

        public static Role fromString(String s) {
            if (s == null) return EMPLOYEE;
            try { return Role.valueOf(s.toUpperCase()); }
            catch (IllegalArgumentException e) { return EMPLOYEE; }
        }
    }
