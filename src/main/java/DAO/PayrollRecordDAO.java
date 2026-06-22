package DAO;

import Model.PayrollRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence operations for the payroll_record table.
 * Uses the Database singleton (Database.getInstance().getConnection()).
 *
 * employee_name is NOT stored in the table; it is populated at read time
 * via a JOIN with the employee table.
 */
public class PayrollRecordDAO {

    // ── Write ──────────────────────────────────────────────────────────────

    /**
     * Inserts a new payroll record, or updates the existing one if the
     * (period_id, employee_id) unique key already exists.
     */
    public void save(PayrollRecord r) throws SQLException {
        String sql = "INSERT INTO payroll_record "
                   + "  (period_id, employee_id, basic_salary, gross_pay, "
                   + "   sss_deduction, philhealth_deduction, pagibig_deduction, "
                   + "   withholding_tax, total_deductions, net_pay) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE "
                   + "  basic_salary         = VALUES(basic_salary), "
                   + "  gross_pay            = VALUES(gross_pay), "
                   + "  sss_deduction        = VALUES(sss_deduction), "
                   + "  philhealth_deduction = VALUES(philhealth_deduction), "
                   + "  pagibig_deduction    = VALUES(pagibig_deduction), "
                   + "  withholding_tax      = VALUES(withholding_tax), "
                   + "  total_deductions     = VALUES(total_deductions), "
                   + "  net_pay              = VALUES(net_pay)";

        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getPeriodId());
            ps.setString(2, r.getEmployeeId());
            ps.setDouble(3, r.getBasicSalary());
            ps.setDouble(4, r.getGrossPay());
            ps.setDouble(5, r.getSssDeduction());
            ps.setDouble(6, r.getPhilhealthDeduction());
            ps.setDouble(7, r.getPagibigDeduction());
            ps.setDouble(8, r.getWithholdingTax());
            ps.setDouble(9, r.getTotalDeductions());
            ps.setDouble(10, r.getNetPay());
            ps.executeUpdate();

            // Capture the generated key only on INSERT (UPDATE returns 0)
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    if (id > 0) r.setRecordId(id);
                }
            }
        }
    }

    // ── Read ───────────────────────────────────────────────────────────────

    /**
     * Returns all payroll records for the given period, joined with employee
     * to populate employee_name. Ordered by employee_id.
     */
    public List<PayrollRecord> getByPeriod(int periodId) throws SQLException {
        String sql = "SELECT pr.record_id, pr.period_id, pr.employee_id, "
                   + "       CONCAT(e.last_name, ', ', e.first_name) AS employee_name, "
                   + "       pr.basic_salary, pr.gross_pay, "
                   + "       pr.sss_deduction, pr.philhealth_deduction, pr.pagibig_deduction, "
                   + "       pr.withholding_tax, pr.total_deductions, pr.net_pay, pr.computed_at "
                   + "FROM payroll_record pr "
                   + "JOIN employee e ON pr.employee_id = e.employee_id "
                   + "WHERE pr.period_id = ? "
                   + "ORDER BY pr.employee_id";
        List<PayrollRecord> list = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, periodId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    /**
     * Returns the payroll record for a specific (period, employee) pair,
     * or null if no record exists.
     */
    public PayrollRecord getByPeriodAndEmployee(int periodId, String employeeId) throws SQLException {
        String sql = "SELECT pr.record_id, pr.period_id, pr.employee_id, "
                   + "       CONCAT(e.last_name, ', ', e.first_name) AS employee_name, "
                   + "       pr.basic_salary, pr.gross_pay, "
                   + "       pr.sss_deduction, pr.philhealth_deduction, pr.pagibig_deduction, "
                   + "       pr.withholding_tax, pr.total_deductions, pr.net_pay, pr.computed_at "
                   + "FROM payroll_record pr "
                   + "JOIN employee e ON pr.employee_id = e.employee_id "
                   + "WHERE pr.period_id = ? AND pr.employee_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, periodId);
            ps.setString(2, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        }
        return null;
    }

    /**
     * Returns all payroll records for a given employee across all periods,
     * newest period first.
     */
    public List<PayrollRecord> getByEmployee(String employeeId) throws SQLException {
        String sql = "SELECT pr.record_id, pr.period_id, pr.employee_id, "
                   + "       CONCAT(e.last_name, ', ', e.first_name) AS employee_name, "
                   + "       pr.basic_salary, pr.gross_pay, "
                   + "       pr.sss_deduction, pr.philhealth_deduction, pr.pagibig_deduction, "
                   + "       pr.withholding_tax, pr.total_deductions, pr.net_pay, pr.computed_at "
                   + "FROM payroll_record pr "
                   + "JOIN employee e ON pr.employee_id = e.employee_id "
                   + "WHERE pr.employee_id = ? "
                   + "ORDER BY pr.period_id DESC";
        List<PayrollRecord> list = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private PayrollRecord map(ResultSet rs) throws SQLException {
        PayrollRecord r = new PayrollRecord();
        r.setRecordId(rs.getInt("record_id"));
        r.setPeriodId(rs.getInt("period_id"));
        r.setEmployeeId(rs.getString("employee_id"));
        r.setEmployeeName(rs.getString("employee_name"));
        r.setBasicSalary(rs.getDouble("basic_salary"));
        r.setGrossPay(rs.getDouble("gross_pay"));
        r.setSssDeduction(rs.getDouble("sss_deduction"));
        r.setPhilhealthDeduction(rs.getDouble("philhealth_deduction"));
        r.setPagibigDeduction(rs.getDouble("pagibig_deduction"));
        r.setWithholdingTax(rs.getDouble("withholding_tax"));
        r.setTotalDeductions(rs.getDouble("total_deductions"));
        r.setNetPay(rs.getDouble("net_pay"));
        Timestamp ts = rs.getTimestamp("computed_at");
        if (ts != null) r.setComputedAt(ts.toLocalDateTime());
        return r;
    }
}
