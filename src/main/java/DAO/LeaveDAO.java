package DAO;
 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
/**
 * DAO for leave_request table in payrollsystem_db.
 * Schema: leave_request(request_id, employee_id, leave_type_id, start_date,
 *         end_date, reason, approval_status_id, reviewed_by, reviewed_at)
 */
public class LeaveDAO {
 
    /** Returns all leave requests with expanded employee/type/status names. */
    public List<String[]> getAllLeaves() throws SQLException {
        String sql =
            "SELECT lr.request_id, "
          + "       e.employee_id, "
          + "       CONCAT(e.first_name, ' ', e.last_name) AS employee_name, "
          + "       lt.leave_type_name, "
          + "       DATE_FORMAT(lr.start_date, '%m/%d/%Y') AS start_date, "
          + "       DATE_FORMAT(lr.end_date,   '%m/%d/%Y') AS end_date, "
          + "       aps.status_name AS approval_status "
          + "FROM leave_request lr "
          + "INNER JOIN employee       e   ON e.employee_id      = lr.employee_id "
          + "INNER JOIN leave_type     lt  ON lt.leave_type_id   = lr.leave_type_id "
          + "INNER JOIN approval_status aps ON aps.approval_status_id = lr.approval_status_id "
          + "ORDER BY lr.request_id ASC";
 
        List<String[]> result = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new String[]{
                    rs.getString("request_id"),
                    rs.getString("employee_id"),
                    rs.getString("employee_name"),
                    rs.getString("leave_type_name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("approval_status")
                });
            }
        }
        return result;
    }
 
    /** Filters leave requests for a single employee. */
    public List<String[]> getLeavesByEmployee(String employeeId) throws SQLException {
        String sql =
            "SELECT lr.request_id, "
          + "       e.employee_id, "
          + "       CONCAT(e.first_name, ' ', e.last_name) AS employee_name, "
          + "       lt.leave_type_name, "
          + "       DATE_FORMAT(lr.start_date, '%m/%d/%Y') AS start_date, "
          + "       DATE_FORMAT(lr.end_date,   '%m/%d/%Y') AS end_date, "
          + "       aps.status_name AS approval_status "
          + "FROM leave_request lr "
          + "INNER JOIN employee        e   ON e.employee_id         = lr.employee_id "
          + "INNER JOIN leave_type      lt  ON lt.leave_type_id      = lr.leave_type_id "
          + "INNER JOIN approval_status aps ON aps.approval_status_id = lr.approval_status_id "
          + "WHERE lr.employee_id = ? "
          + "ORDER BY lr.request_id DESC";
 
        List<String[]> result = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new String[]{
                        rs.getString("request_id"),
                        rs.getString("employee_id"),
                        rs.getString("employee_name"),
                        rs.getString("leave_type_name"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("approval_status")
                    });
                }
            }
        }
        return result;
    }
 
    /**
     * Submits a new leave request.
     *
     * @param employeeId    the requestor's employee_id
     * @param leaveTypeName e.g. "Sick Leave", "Vacation Leave"
     * @param startDate     ISO format "yyyy-MM-dd"
     * @param endDate       ISO format "yyyy-MM-dd"
     * @param reason        optional free-text reason
     */
    public boolean addLeaveRequest(String employeeId, String leaveTypeName,
                                    String startDate, String endDate,
                                    String reason) throws SQLException {
        // Resolve leave_type_id
        int leaveTypeId = resolveLeaveTypeId(leaveTypeName);
        // Resolve the default "Pending" approval_status_id
        int pendingStatusId = resolveApprovalStatusId("Pending");
 
        String sql = "INSERT INTO leave_request "
                   + "(employee_id, leave_type_id, start_date, end_date, "
                   + " reason, approval_status_id) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.setInt(2, leaveTypeId);
            ps.setDate(3, Date.valueOf(startDate));
            ps.setDate(4, Date.valueOf(endDate));
            ps.setString(5, reason);
            ps.setInt(6, pendingStatusId);
            return ps.executeUpdate() == 1;
        }
    }
 
    /**
     * Updates the approval status of a leave request.
     *
     * @param requestId   the request_id as a String
     * @param newStatus   e.g. "Approved", "Denied"
     * @param reviewedBy  employee_id of the approver
     */
    public void updateLeaveStatus(String requestId, String newStatus,
                                   String reviewedBy) throws SQLException {
        int statusId = resolveApprovalStatusId(newStatus);
        String sql = "UPDATE leave_request "
                   + "SET approval_status_id = ?, reviewed_by = ?, reviewed_at = NOW() "
                   + "WHERE request_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            ps.setString(2, reviewedBy);
            ps.setInt(3, Integer.parseInt(requestId.trim()));
            int rows = ps.executeUpdate();
            if (rows == 0)
                throw new SQLException("Request ID " + requestId + " not found.");
        }
    }
 
    public boolean deleteLeaveRequest(int requestId) throws SQLException {
        String sql = "DELETE FROM leave_request WHERE request_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            return ps.executeUpdate() == 1;
        }
    }
 
    // ── helpers ───────────────────────────────────────────────────────────
 
    private int resolveLeaveTypeId(String name) throws SQLException {
        String sql = "SELECT leave_type_id FROM leave_type WHERE leave_type_name = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Leave type not found: " + name);
    }
 
    private int resolveApprovalStatusId(String name) throws SQLException {
        String sql = "SELECT approval_status_id FROM approval_status WHERE status_name = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Approval status not found: " + name);
    }

    public void updateLeaveStatus(String reqId, String approved) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
