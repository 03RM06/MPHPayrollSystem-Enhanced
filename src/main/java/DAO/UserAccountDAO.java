package DAO;

import Model.Role;
import Model.UserAccount;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAccountDAO implements DataAccessObject<UserAccount> {

    // ── password hashing ──────────────────────────────────────────────────

    /** Returns SHA-256 hex string to match MySQL SHA2(plainText, 256). */
    public static String hashPassword(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plainText.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static boolean checkPassword(String plainText, String storedHash) {
        return hashPassword(plainText).equals(storedHash);
    }

    // ── CRUD ──────────────────────────────────────────────────────────────

    @Override
    public boolean create(UserAccount ua) throws SQLException {
        return create(ua, 5) != null;
    }

    /**
     * Creates a user account and assigns the given role.
     * Returns the UserAccount with accountId populated, or null on failure.
     */
    public UserAccount create(UserAccount ua, int roleId) throws SQLException {
        String sqlUser = "INSERT INTO user_account (username, password_hash, employee_id, status_id) "
                       + "VALUES (?, ?, ?, 1)";
        String sqlRole = "INSERT INTO user_role (account_id, role_id) VALUES (?, ?)";

        Connection conn = Database.getInstance().getConnection();
        boolean prev = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            int generatedId;
            try (PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, ua.getUsername());
                ps.setString(2, hashPassword(ua.getPassword()));
                ps.setString(3, ua.getEmployeeID());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new SQLException("No generated key returned");
                    generatedId = keys.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlRole)) {
                ps.setInt(1, generatedId);
                ps.setInt(2, roleId);
                ps.executeUpdate();
            }
            conn.commit();
            ua.setAccountId(generatedId);
            return ua;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(prev);
        }
    }

    @Override
    public List<UserAccount> findAll() throws SQLException {
        String sql = BASE_SELECT + "GROUP BY ua.account_id, ua.username, "
                   + "ua.password_hash, ua.employee_id, ua.status_id";
        List<UserAccount> list = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    @Override
    public UserAccount findById(String id) throws SQLException {
        return findByUsername(id);
    }

    public UserAccount findByUsername(String username) throws SQLException {
        String sql = BASE_SELECT
                   + "WHERE ua.username = ? "
                   + "GROUP BY ua.account_id, ua.username, "
                   + "ua.password_hash, ua.employee_id, ua.status_id";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    /** Authenticates and returns the UserAccount, or null if credentials fail. */
    public UserAccount login(String username, String plainPassword) throws SQLException {
        UserAccount ua = findByUsername(username);
        if (ua == null) return null;
        if (checkPassword(plainPassword, ua.getPassword())) {
            Services.AuditLogService.log("LOGIN", "user_account", ua.getUsername(), "User logged in");
            return ua;
        }
        return null;
    }

    @Override
    public boolean update(UserAccount ua) throws SQLException {
        String sql = "UPDATE user_account SET username = ?, employee_id = ? "
                   + "WHERE account_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ua.getUsername());
            ps.setString(2, ua.getEmployeeID());
            ps.setInt(3, ua.getAccountId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(String username, String newPlainPassword) throws SQLException {
        String sql = "UPDATE user_account SET password_hash = ? WHERE username = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hashPassword(newPlainPassword));
            ps.setString(2, username);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean setActive(int accountId, boolean active) throws SQLException {
        String sql = "UPDATE user_account SET status_id = ? WHERE account_id = ?";
        int statusId = active ? 1 : 2;
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            ps.setInt(2, accountId);
            return ps.executeUpdate() == 1;
        }
    }

    public int getRoleForUser(int accountId) throws SQLException {
        String sql = "SELECT r.role_id FROM roles r "
                   + "JOIN user_role ur ON ur.role_id = r.role_id "
                   + "WHERE ur.account_id = ? LIMIT 1";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("role_id") : 0;
            }
        }
    }

    public boolean setRoleForUser(int accountId, int roleId) throws SQLException {
        String sqlDel = "DELETE FROM user_role WHERE account_id = ?";
        String sqlIns = "INSERT INTO user_role (account_id, role_id) VALUES (?, ?)";

        Connection conn = Database.getInstance().getConnection();
        boolean prev = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sqlDel)) {
                ps.setInt(1, accountId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlIns)) {
                ps.setInt(1, accountId);
                ps.setInt(2, roleId);
                ps.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(prev);
        }
    }

    public int countActiveAdmins() throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_account ua "
                   + "JOIN user_role ur ON ur.account_id = ua.account_id "
                   + "WHERE ua.status_id = 1 AND ur.role_id = 1";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM user_account WHERE account_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    // ── private helpers ───────────────────────────────────────────────────

    private static final String BASE_SELECT =
        "SELECT ua.account_id, ua.username, ua.password_hash, "
      + "       ua.employee_id, ua.status_id, "
      + "       GROUP_CONCAT(r.role_name ORDER BY r.role_name SEPARATOR ',') AS role_names "
      + "FROM user_account ua "
      + "LEFT JOIN user_role ur ON ur.account_id = ua.account_id "
      + "LEFT JOIN roles     r  ON r.role_id     = ur.role_id ";

    private UserAccount map(ResultSet rs) throws SQLException {
        UserAccount ua = new UserAccount();
        ua.setAccountId(rs.getInt("account_id"));
        ua.setUsername(rs.getString("username"));
        ua.setPassword(rs.getString("password_hash"));
        ua.setEmployeeID(rs.getString("employee_id"));
        ua.setStatusId(rs.getInt("status_id"));
        ua.setRole(resolveHighestRole(rs.getString("role_names")));
        return ua;
    }

    private Role resolveHighestRole(String csv) {
        if (csv == null || csv.isBlank()) return Role.EMPLOYEE;
        Role best = Role.EMPLOYEE;
        for (String raw : csv.split(",")) {
            Role candidate = dbNameToRole(raw.trim());
            if (rolePriority(candidate) > rolePriority(best)) best = candidate;
        }
        return best;
    }

    private Role dbNameToRole(String name) {
        if (name == null) return Role.EMPLOYEE;
        return switch (name.toUpperCase()) {
            case "ADMIN"    -> Role.ADMIN;
            case "HR"       -> Role.HR;
            case "FINANCE"  -> Role.FINANCE;
            case "IT"       -> Role.IT;
            case "EMPLOYEE" -> Role.EMPLOYEE;
            default         -> Role.EMPLOYEE;
        };
    }

    private int rolePriority(Role r) {
        return switch (r) {
            case ADMIN -> 5; case HR -> 4; case FINANCE -> 3;
            case IT -> 2;    default  -> 1;
        };
    }
}
