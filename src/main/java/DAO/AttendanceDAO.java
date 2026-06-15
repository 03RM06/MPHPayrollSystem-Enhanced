package DAO;
 
import Model.Attendance;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
 
public class AttendanceDAO implements DataAccessObject<Attendance> {
 
    @Override
    public boolean create(Attendance a) throws SQLException {
        String sql = "INSERT INTO time_and_attendance "
                   + "(employee_id, attendance_date, time_in, time_out) "
                   + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = Database.getInstance()
                                            .getConnection()
                                            .prepareStatement(sql)) {
            bind(ps, a);
            return ps.executeUpdate() == 1;
        }
    }
 
    @Override
    public List<Attendance> findAll() throws SQLException {
        String sql = "SELECT attendance_id, employee_id, "
                   + "attendance_date, time_in, time_out "
                   + "FROM time_and_attendance "
                   + "ORDER BY attendance_date DESC, attendance_id DESC";
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = Database.getInstance()
                                            .getConnection()
                                            .prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
 
    @Override
    public Attendance findById(String id) throws SQLException {
        String sql = "SELECT attendance_id, employee_id, "
                   + "attendance_date, time_in, time_out "
                   + "FROM time_and_attendance WHERE attendance_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }
 
    @Override
    public List<Attendance> findByEmployee(String employeeId) throws SQLException {
        String sql = "SELECT attendance_id, employee_id, "
                   + "attendance_date, time_in, time_out "
                   + "FROM time_and_attendance "
                   + "WHERE employee_id = ? ORDER BY attendance_date DESC";
        List<Attendance> list = new ArrayList<>();
        try (PreparedStatement ps = Database.getInstance()
                                            .getConnection()
                                            .prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }
 
    @Override
    public boolean update(Attendance a) throws SQLException {
        if (a.getId() == null)
            throw new IllegalArgumentException("Attendance.id is required for update");
        String sql = "UPDATE time_and_attendance "
                   + "SET employee_id = ?, attendance_date = ?, "
                   + "time_in = ?, time_out = ? "
                   + "WHERE attendance_id = ?";
        try (PreparedStatement ps = Database.getInstance()
                                            .getConnection()
                                            .prepareStatement(sql)) {
            bind(ps, a);
            ps.setInt(5, a.getId());
            return ps.executeUpdate() == 1;
        }
    }
 
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM time_and_attendance WHERE attendance_id = ?";
        try (PreparedStatement ps = Database.getInstance()
                                            .getConnection()
                                            .prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }
 
    // ── private helpers ──────────────────────────────────────────────────
 
    private static void bind(PreparedStatement ps, Attendance a) throws SQLException {
        ps.setString(1, a.getEmployeeId());
        ps.setString(2, a.getDate());      // "yyyy-MM-dd" → MySQL DATE
        ps.setString(3, a.getTimeIn());    // "HH:mm" or "HH:mm:ss" → MySQL TIME
        ps.setString(4, a.getTimeOut());
    }
 
    public static Attendance map(ResultSet rs) throws SQLException {
        Attendance a = new Attendance();
        a.setId(rs.getInt("attendance_id"));
        a.setEmployeeId(rs.getString("employee_id"));
        a.setDate(rs.getString("attendance_date"));
        a.setTimeIn(rs.getString("time_in"));
        a.setTimeOut(rs.getString("time_out"));
        return a;
    }
}
