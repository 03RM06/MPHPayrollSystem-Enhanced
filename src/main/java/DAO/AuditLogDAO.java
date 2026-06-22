package DAO;

import Model.AuditLog;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data-access object for the audit_log table. */
public class AuditLogDAO {

    /**
     * Inserts one audit entry.
     * Fire-and-forget: exceptions are caught and printed; never propagated.
     */
    public void log(String performedBy, String action,
                    String targetEntity, String targetId, String details) {
        try {
            String sql =
                "INSERT INTO audit_log "
              + "(performed_by, action, target_entity, target_id, details) "
              + "VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = Database.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, performedBy);
                ps.setString(2, action);
                ps.setString(3, targetEntity);
                ps.setString(4, targetId);
                ps.setString(5, details);
                ps.executeUpdate();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Returns up to 500 audit entries, newest first. */
    public List<AuditLog> getAll() {
        String sql =
            "SELECT audit_id, performed_by, action, target_entity, "
          + "       target_id, details, performed_at "
          + "FROM audit_log "
          + "ORDER BY performed_at DESC "
          + "LIMIT 500";
        return query(sql);
    }

    /** Returns up to 200 entries for a specific user, newest first. */
    public List<AuditLog> getByUser(String username) {
        String sql =
            "SELECT audit_id, performed_by, action, target_entity, "
          + "       target_id, details, performed_at "
          + "FROM audit_log "
          + "WHERE performed_by = ? "
          + "ORDER BY performed_at DESC "
          + "LIMIT 200";
        return query(sql, username);
    }

    /** Returns up to 200 entries for a specific action, newest first. */
    public List<AuditLog> getByAction(String action) {
        String sql =
            "SELECT audit_id, performed_by, action, target_entity, "
          + "       target_id, details, performed_at "
          + "FROM audit_log "
          + "WHERE action = ? "
          + "ORDER BY performed_at DESC "
          + "LIMIT 200";
        return query(sql, action);
    }

    // ── private helpers ───────────────────────────────────────────────────

    /** Executes a parameterised query and maps all rows to AuditLog objects. */
    private List<AuditLog> query(String sql, String... params) {
        List<AuditLog> list = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setString(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private AuditLog map(ResultSet rs) throws SQLException {
        AuditLog a = new AuditLog();
        a.setAuditId(rs.getLong("audit_id"));
        a.setPerformedBy(rs.getString("performed_by"));
        a.setAction(rs.getString("action"));
        a.setTargetEntity(rs.getString("target_entity"));
        a.setTargetId(rs.getString("target_id"));
        a.setDetails(rs.getString("details"));
        Timestamp ts = rs.getTimestamp("performed_at");
        if (ts != null) a.setPerformedAt(ts.toLocalDateTime());
        return a;
    }
}
