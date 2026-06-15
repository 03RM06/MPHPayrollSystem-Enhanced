package Model;
 
import java.util.Set;
 
/** Base user account. Role determines permissions via the Role enum. */
public class UserAccount {
    protected String username;
    protected String password;   // stored as SHA-256 hex
    protected String employeeID;
    protected Role   role;
 
    public UserAccount() {}
    protected UserAccount(Role role) { this.role = role; }
 
    public String getUsername()   { return username; }
    public String getPassword()   { return password; }
    public String getEmployeeID() { return employeeID; }
    public Role   getRole()       { return role; }
 
    public void setUsername(String v)  { this.username = v; }
    public void setPassword(String v)  { this.password = v; }
    public void setEmployeeID(String v){ this.employeeID = v; }
    public void setRole(Role v)        { this.role = v; }
 
    public Set<Permission> getPermissions()            { return role.getPermissions(); }
    public boolean hasPermission(Permission p)         { return role.hasPermission(p); }
 
    public boolean isAdmin()    { return Role.ADMIN   == role; }
    public boolean isHR()       { return Role.HR      == role; }
    public boolean isFinance()  { return Role.FINANCE == role; }
    public boolean isIT()       { return Role.IT      == role; }
    public boolean isEmployee() { return Role.EMPLOYEE == role; }
}
 
// ────────────────────────────────────────────────────────────────────────────
 
/** Extends UserAccount with admin-only capabilities (e.g. password resets). */
class AdminAccount extends UserAccount {
    public AdminAccount() { super(Role.ADMIN); }
 
    public AdminAccount(String username, String password, String employeeID) {
        super(Role.ADMIN);
        this.username   = username;
        this.password   = password;
        this.employeeID = employeeID;
    }
 
    public void resetAnyUserPassword(UserAccount target, String newPassword) {
        target.setPassword(newPassword);
        System.out.println("Password for " + target.getUsername() + " has been reset.");
    }
}
