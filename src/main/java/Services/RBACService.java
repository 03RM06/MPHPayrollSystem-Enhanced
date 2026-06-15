package Services;
 
import Model.Permission;
import Model.UserAccount;
import java.util.Collections;
import java.util.Set;
 
/**
 * Role-Based Access Control service.
 * Singleton — call RBACService.getInstance().
 */
public class RBACService extends BaseService {
 
    private static RBACService instance;
    private RBACService() {}
 
    public static synchronized RBACService getInstance() {
        if (instance == null) instance = new RBACService();
        return instance;
    }
 
    public boolean hasPermission(UserAccount user, Permission permission) {
        if (user == null || permission == null || user.getRole() == null) return false;
        return user.getRole().hasPermission(permission);
    }
 
    public boolean canAccessFeature(UserAccount user, String feature) {
        Permission p = featureToPermission(feature);
        return p != null && hasPermission(user, p);
    }
 
    public Set<Permission> getUserPermissions(UserAccount user) {
        if (user == null || user.getRole() == null) return Collections.emptySet();
        return user.getRole().getPermissions();
    }
 
    public boolean canEditEmployee(UserAccount user, String targetId) {
        if (user == null || targetId == null) return false;
        if (targetId.equals(user.getEmployeeID())
                && hasPermission(user, Permission.VIEW_OWN_EMPLOYEE_INFO)) return true;
        return hasPermission(user, Permission.EDIT_EMPLOYEE);
    }
 
    public boolean canViewEmployee(UserAccount user, String targetId) {
        if (user == null || targetId == null) return false;
        if (targetId.equals(user.getEmployeeID())
                && hasPermission(user, Permission.VIEW_OWN_EMPLOYEE_INFO)) return true;
        return hasPermission(user, Permission.VIEW_ALL_EMPLOYEES);
    }
 
    public boolean canViewPayslip(UserAccount user, String targetId) {
        if (user == null || targetId == null) return false;
        if (targetId.equals(user.getEmployeeID())
                && hasPermission(user, Permission.VIEW_OWN_PAYSLIP)) return true;
        return hasPermission(user, Permission.VIEW_ALL_PAYSLIPS);
    }
 
    private Permission featureToPermission(String feature) {
        if (feature == null) return null;
        return switch (feature.toLowerCase()) {
            case "create_employee"      -> Permission.CREATE_EMPLOYEE;
            case "edit_employee"        -> Permission.EDIT_EMPLOYEE;
            case "delete_employee"      -> Permission.DELETE_EMPLOYEE;
            case "view_all_employees"   -> Permission.VIEW_ALL_EMPLOYEES;
            case "process_payroll"      -> Permission.PROCESS_PAYROLL;
            case "approve_payroll"      -> Permission.APPROVE_PAYROLL;
            case "view_all_payslips"    -> Permission.VIEW_ALL_PAYSLIPS;
            case "edit_salary_deduction"-> Permission.EDIT_SALARY_DEDUCTION;
            case "approve_leave"        -> Permission.APPROVE_LEAVE;
            case "generate_reports"     -> Permission.GENERATE_REPORTS;
            case "system_maintenance"   -> Permission.SYSTEM_MAINTENANCE;
            default                     -> null;
        };
    }
}
