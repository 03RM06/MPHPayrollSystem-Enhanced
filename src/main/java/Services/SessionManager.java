package Services;
 
import Model.Permission;
import Model.Role;
import Model.UserAccount;
import java.util.Set;
 
/**
 * Tracks the currently authenticated user for the session lifetime.
 * Singleton — use SessionManager.getInstance().
 */
public class SessionManager {
 
    private static SessionManager instance;
    private UserAccount currentUser;
 
    private SessionManager() {}
 
    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }
 
    public void        setCurrentUser(UserAccount user) { this.currentUser = user; }
    public UserAccount getCurrentUser()                 { return currentUser; }
    public boolean     isLoggedIn()                     { return currentUser != null; }
    public void        logout()                         { currentUser = null; }
 
    public Role   getCurrentUserRole()       { return currentUser != null ? currentUser.getRole() : null; }
    public String getCurrentUserEmployeeId() { return currentUser != null ? currentUser.getEmployeeID() : null; }
 
    public boolean hasPermission(Permission permission) {
        if (currentUser == null) return false;
        if (currentUser.getRole() == Role.ADMIN) return true;
        return currentUser.getRole().hasPermission(permission);
    }
 
    public boolean canAccessFeature(String feature) {
        if (currentUser == null) return false;
        if (currentUser.getRole() == Role.ADMIN) return true;
        return RBACService.getInstance().canAccessFeature(currentUser, feature);
    }
 
    public Set<Permission> getCurrentUserPermissions() {
        return currentUser != null ? currentUser.getPermissions() : Set.of();
    }
 
    public boolean canEditEmployee(String targetId) {
        if (currentUser == null) return false;
        return RBACService.getInstance().canEditEmployee(currentUser, targetId);
    }
 
    public boolean canViewEmployee(String targetId) {
        if (currentUser == null) return false;
        return RBACService.getInstance().canViewEmployee(currentUser, targetId);
    }
 
    public boolean canViewPayslip(String targetId) {
        if (currentUser == null) return false;
        return RBACService.getInstance().canViewPayslip(currentUser, targetId);
    }
 
    public String getCurrentUserDisplayName() {
        if (currentUser == null) return "Guest";
        return currentUser.getUsername() + " (" + currentUser.getRole().getDisplayName() + ")";
    }
}
