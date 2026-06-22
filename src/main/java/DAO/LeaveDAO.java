package DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    public List<String[]> getAllLeaves() throws SQLException {
        String sql =
            "SELECT lr.leave_id, "
          + "       lr.employee_id, "
          + "       lr.employee_name, "
          + "       lt.type_name, "
          + "       DATE_FORMAT(lr.start_date, '%m/%d/%Y') AS start_date, "
          + "       DATE_FORMAT(lr.end_date,   '%m/%d/%Y') AS end_date, "
          + "       aps.status_name AS approval_status "
          + "FROM leave_request lr "
          + "INNER JOIN leave_type      lt  ON lt.leave_type_id = lr.leave_type_id "
          + "INNER JOIN approval_status aps ON aps.status_id    = lr.status_id "
          + "ORDER BY lr.leave_id ASC";

        List<String[]> result = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new String[]{
                    rs.getString("leave_id"),
                    rs.getString("employee_id"),
                    rs.getString("employee_name"),
                    rs.getString("type_name"),
                    rs.getString("start_date"),
                    rs.getString("end_date"),
                    rs.getString("approval_status")
                });
            }
        }
        return result;
    }

    public List<String[]> getLeavesByEmployee(String employeeId) throws SQLException {
        String sql =
            "SELECT lr.leave_id, "
          + "       lr.employee_id, "
          + "       lr.employee_name, "
          + "       lt.type_name, "
          + "       DATE_FORMAT(lr.start_date, '%m/%d/%Y') AS start_date, "
          + "       DATE_FORMAT(lr.end_date,   '%m/%d/%Y') AS end_date, "
          + "       aps.status_name AS approval_status "
          + "FROM leave_request lr "
          + "INNER JOIN leave_type      lt  ON lt.leave_type_id = lr.leave_type_id "
          + "INNER JOIN approval_status aps ON aps.status_id    = lr.status_id "
          + "WHERE lr.employee_id = ? "
          + "ORDER BY lr.leave_id DESC";

        List<String[]> result = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new String[]{
                        rs.getString("leave_id"),
                        rs.getString("employee_id"),
                        rs.getString("employee_name"),
                        rs.getString("type_name"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("approval_status")
                    });
                }
            }
        }
        return result;
    }

    public boolean addLeaveRequest(String employeeId, String employeeName,
                                String leaveTypeName, String startDate,
                                String endDate, String reason) throws SQLException {
    int leaveTypeId = resolveLeaveTypeId(leaveTypeName);
    int pendingStatusId = resolveApprovalStatusId("Pending");

    String sql = "INSERT INTO leave_request "
               + "(leave_id, employee_id, employee_name, leave_type_id, start_date, end_date, "
               + " reason, status_id, requested_at) "
               + "VALUES (UUID(), ?, ?, ?, ?, ?, ?, ?, NOW())";
    try (Connection conn = Database.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, employeeId);
        ps.setString(2, employeeName);
        ps.setInt(3, leaveTypeId);
        ps.setDate(4, Date.valueOf(startDate));
        ps.setDate(5, Date.valueOf(endDate));
        ps.setString(6, reason);
        ps.setInt(7, pendingStatusId);
        return ps.executeUpdate() == 1;
    }
}

    public void updateLeaveStatus(String leaveId, String newStatus,
                                   String reviewedBy) throws SQLException {
        int statusId = resolveApprovalStatusId(newStatus);
        String sql = "UPDATE leave_request "
                   + "SET status_id = ?, reviewed_by = ?, reviewed_at = NOW() "
                   + "WHERE leave_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            ps.setString(2, reviewedBy);
            ps.setString(3, leaveId);
            int rows = ps.executeUpdate();
            if (rows == 0)
                throw new SQLException("Leave ID " + leaveId + " not found.");
        }
        if ("Approved".equalsIgnoreCase(newStatus)) {
            Services.AuditLogService.log("APPROVE_LEAVE", "leave_request", leaveId, "Leave approved");
        } else if ("Denied".equalsIgnoreCase(newStatus)) {
            Services.AuditLogService.log("REJECT_LEAVE", "leave_request", leaveId, "Leave rejected");
        }
    }

    public boolean deleteLeaveRequest(String leaveId) throws SQLException {
        String sql = "DELETE FROM leave_request WHERE leave_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, leaveId);
            return ps.executeUpdate() == 1;
        }
    }

    private int resolveLeaveTypeId(String name) throws SQLException {
        String sql = "SELECT leave_type_id FROM leave_type WHERE type_name = ?";
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
        String sql = "SELECT status_id FROM approval_status WHERE status_name = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        throw new SQLException("Approval status not found: " + name);
    }
}