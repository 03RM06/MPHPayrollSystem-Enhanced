package DAO;

import Model.PayrollPeriod;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD operations for the payroll_period table.
 * Uses the Database singleton (Database.getInstance().getConnection()).
 */
public class PayrollPeriodDAO {

    // ── Read all ───────────────────────────────────────────────────────────

    /** Returns all payroll periods, newest first. */
    public List<PayrollPeriod> getAll() throws SQLException {
        String sql = "SELECT period_id, period_name, start_date, end_date, status "
                   + "FROM payroll_period ORDER BY period_id DESC";
        List<PayrollPeriod> list = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    // ── Read one ───────────────────────────────────────────────────────────

    /** Returns the period with the given ID, or null if not found. */
    public PayrollPeriod getById(int periodId) throws SQLException {
        String sql = "SELECT period_id, period_name, start_date, end_date, status "
                   + "FROM payroll_period WHERE period_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, periodId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    /** Returns the first OPEN period, or null if none exists. */
    public PayrollPeriod getOpenPeriod() throws SQLException {
        String sql = "SELECT period_id, period_name, start_date, end_date, status "
                   + "FROM payroll_period WHERE status = 'OPEN' LIMIT 1";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return map(rs);
        }
        return null;
    }

    // ── Create ─────────────────────────────────────────────────────────────

    /**
     * Inserts a new payroll period and returns the generated primary key.
     *
     * @param p PayrollPeriod with periodName, startDate, endDate and status set
     * @return the auto-generated period_id
     */
    public int create(PayrollPeriod p) throws SQLException {
        String sql = "INSERT INTO payroll_period (period_name, start_date, end_date, status) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getPeriodName());
            ps.setDate(2, Date.valueOf(p.getStartDate()));
            ps.setDate(3, Date.valueOf(p.getEndDate()));
            ps.setString(4, p.getStatus() != null ? p.getStatus() : "OPEN");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    p.setPeriodId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Failed to retrieve generated key after insert into payroll_period");
    }

    // ── Update ─────────────────────────────────────────────────────────────

    /** Sets a period's status to CLOSED. */
    public void closePeriod(int periodId) throws SQLException {
        String sql = "UPDATE payroll_period SET status = 'CLOSED' WHERE period_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, periodId);
            ps.executeUpdate();
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private PayrollPeriod map(ResultSet rs) throws SQLException {
        PayrollPeriod p = new PayrollPeriod();
        p.setPeriodId(rs.getInt("period_id"));
        p.setPeriodName(rs.getString("period_name"));
        Date start = rs.getDate("start_date");
        Date end   = rs.getDate("end_date");
        p.setStartDate(start != null ? start.toLocalDate() : null);
        p.setEndDate(end   != null ? end.toLocalDate()   : null);
        p.setStatus(rs.getString("status"));
        return p;
    }
}
