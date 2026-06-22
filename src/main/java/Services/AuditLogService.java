package Services;

import DAO.AuditLogDAO;
import Model.AuditLog;
import java.util.List;

/**
 * Thin service wrapper around AuditLogDAO.
 * Resolves the current username from SessionManager automatically so callers
 * only need to supply the action/entity details.
 */
public class AuditLogService {

    private static final AuditLogDAO dao = new AuditLogDAO();

    private AuditLogService() {}

    /**
     * Records an audit event attributed to the currently logged-in user.
     * Falls back to "SYSTEM" when no session is active.
     */
    public static void log(String action, String targetEntity,
                           String targetId, String details) {
        String user = (SessionManager.getInstance().getCurrentUser() != null)
            ? SessionManager.getInstance().getCurrentUser().getUsername()
            : "SYSTEM";
        dao.log(user, action, targetEntity, targetId, details);
    }

    /** Returns up to 500 log entries, newest first. */
    public static List<AuditLog> getAll() {
        return dao.getAll();
    }

    /** Returns up to 200 log entries for the given username, newest first. */
    public static List<AuditLog> getByUser(String username) {
        return dao.getByUser(username);
    }
}
