package DAO;
 
import Model.Employee;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
 
public class EmployeeDAO implements DataAccessObject<Employee> {
 
    // ── common SELECT projection (re-used in findAll and findById) ────────
    private static final String BASE_SELECT =
        "SELECT e.employee_id, e.last_name, e.first_name, e.phone_number, "
      + "       b.birth_date, a.full_address, es.status_name, "
      + "       p.position_name, e.supervisor_employee_id, "
      + "       c.basic_salary, c.gross_semi_monthly_rate, c.hourly_rate, "
      + "       g.sss_number, g.philhealth_number, g.tin_number, g.pagibig_number, "
      + "       COALESCE(SUM(CASE WHEN al.allowance_name = 'Rice Subsidy'       THEN ea.amount ELSE 0 END),0) AS rice_subsidy, "
      + "       COALESCE(SUM(CASE WHEN al.allowance_name = 'Phone Allowance'    THEN ea.amount ELSE 0 END),0) AS phone_allowance, "
      + "       COALESCE(SUM(CASE WHEN al.allowance_name = 'Clothing Allowance' THEN ea.amount ELSE 0 END),0) AS clothing_allowance "
      + "FROM employee e "
      + "LEFT JOIN birthday             b  ON e.birthday_id  = b.birthday_id "
      + "LEFT JOIN address              a  ON e.address_id   = a.address_id "
      + "LEFT JOIN employment_status    es ON e.status_id    = es.status_id "
      + "LEFT JOIN `position`           p  ON e.position_id  = p.position_id "
      + "LEFT JOIN compensation         c  ON e.employee_id  = c.employee_id "
      + "LEFT JOIN employee_government_id g ON e.employee_id = g.employee_id "
      + "LEFT JOIN employee_allowance   ea ON e.employee_id  = ea.employee_id "
      + "LEFT JOIN allowance            al ON ea.allowance_id = al.allowance_id ";
 
    private static final String BASE_GROUP =
        "GROUP BY e.employee_id, e.last_name, e.first_name, e.phone_number, "
      + "b.birth_date, a.full_address, es.status_name, p.position_name, "
      + "e.supervisor_employee_id, c.basic_salary, c.gross_semi_monthly_rate, "
      + "c.hourly_rate, g.sss_number, g.philhealth_number, g.tin_number, g.pagibig_number ";
 
    @Override
    public List<Employee> findAll() throws SQLException {
        String sql = BASE_SELECT + BASE_GROUP + "ORDER BY e.employee_id";
        List<Employee> list = new ArrayList<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
 
    @Override
    public Employee findById(String id) throws SQLException {
        String sql = BASE_SELECT + "WHERE e.employee_id = ? " + BASE_GROUP;
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
    public boolean create(Employee emp) throws SQLException {
        Connection conn = Database.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);
 
            // 1. birthday
            int birthdayId = 0;
            if (emp.getBirthday() != null) {
                String sql = "INSERT INTO birthday (birth_date) VALUES (?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setDate(1, Date.valueOf(emp.getBirthday()));
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) birthdayId = rs.getInt(1); }
                }
            }
 
            // 2. address
            int addressId = 0;
            if (emp.getAddress() != null && !emp.getAddress().isBlank()) {
                String sql = "INSERT INTO address (full_address) VALUES (?)";
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, emp.getAddress());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) addressId = rs.getInt(1); }
                }
            }
 
            // 3. lookup/create status & position
            int statusId   = getOrCreateStatusId(conn, emp.getStatus().name());
            int positionId = getOrCreatePositionId(conn, emp.getPosition());
 
            // 4. employee row
            String sqlE = "INSERT INTO employee "
                        + "(employee_id, last_name, first_name, birthday_id, address_id, "
                        + "phone_number, status_id, position_id, supervisor_employee_id) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlE)) {
                ps.setString(1, emp.getEmployeeId());
                ps.setString(2, emp.getLastName());
                ps.setString(3, emp.getFirstName());
                ps.setInt(4, birthdayId);
                ps.setInt(5, addressId);
                ps.setString(6, emp.getPhoneNumber());
                ps.setInt(7, statusId);
                ps.setInt(8, positionId);
                ps.setString(9, emp.getImmediateSupervisor());
                ps.executeUpdate();
            }
 
            // 5. compensation
            String sqlC = "INSERT INTO compensation "
                        + "(employee_id, basic_salary, gross_semi_monthly_rate, hourly_rate) "
                        + "VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlC)) {
                ps.setString(1, emp.getEmployeeId());
                ps.setBigDecimal(2, orZero(emp.getBasicSalary()));
                ps.setBigDecimal(3, orZero(emp.getGrossSemiMonthlyRate()));
                ps.setBigDecimal(4, orZero(emp.getHourlyRate()));
                ps.executeUpdate();
            }
 
            // 6. government IDs
            String sqlG = "INSERT INTO employee_government_id "
                        + "(employee_id, sss_number, philhealth_number, tin_number, pagibig_number) "
                        + "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlG)) {
                ps.setString(1, emp.getEmployeeId());
                ps.setString(2, emp.getSssNumber());
                ps.setString(3, emp.getPhilhealthNumber());
                ps.setString(4, emp.getTinNumber());
                ps.setString(5, emp.getPagIbigNumber());
                ps.executeUpdate();
            }
 
            // 7. allowances
            upsertAllowance(conn, emp.getEmployeeId(), "Rice Subsidy",       emp.getRiceSubsidy());
            upsertAllowance(conn, emp.getEmployeeId(), "Phone Allowance",    emp.getPhoneAllowance());
            upsertAllowance(conn, emp.getEmployeeId(), "Clothing Allowance", emp.getClothingAllowance());
 
            conn.commit();
            return true;
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }
 
    @Override
    public boolean update(Employee emp) throws SQLException {
        Connection conn = Database.getInstance().getConnection();
        try {
            conn.setAutoCommit(false);
 
            String sqlE = "UPDATE employee "
                        + "SET last_name = ?, first_name = ?, phone_number = ?, "
                        + "status_id = ?, position_id = ?, supervisor_employee_id = ? "
                        + "WHERE employee_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlE)) {
                ps.setString(1, emp.getLastName());
                ps.setString(2, emp.getFirstName());
                ps.setString(3, emp.getPhoneNumber());
                ps.setInt(4, getOrCreateStatusId(conn, emp.getStatus().name()));
                ps.setInt(5, getOrCreatePositionId(conn, emp.getPosition()));
                ps.setString(6, emp.getImmediateSupervisor());
                ps.setString(7, emp.getEmployeeId());
                ps.executeUpdate();
            }
 
            String sqlC = "UPDATE compensation "
                        + "SET basic_salary = ?, gross_semi_monthly_rate = ?, hourly_rate = ? "
                        + "WHERE employee_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlC)) {
                ps.setBigDecimal(1, orZero(emp.getBasicSalary()));
                ps.setBigDecimal(2, orZero(emp.getGrossSemiMonthlyRate()));
                ps.setBigDecimal(3, orZero(emp.getHourlyRate()));
                ps.setString(4, emp.getEmployeeId());
                ps.executeUpdate();
            }
 
            String sqlG = "UPDATE employee_government_id "
                        + "SET sss_number = ?, philhealth_number = ?, "
                        + "tin_number = ?, pagibig_number = ? "
                        + "WHERE employee_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlG)) {
                ps.setString(1, emp.getSssNumber());
                ps.setString(2, emp.getPhilhealthNumber());
                ps.setString(3, emp.getTinNumber());
                ps.setString(4, emp.getPagIbigNumber());
                ps.setString(5, emp.getEmployeeId());
                ps.executeUpdate();
            }
 
            conn.commit();
            return true;
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }
 
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM employee WHERE employee_id = ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(id));
            return ps.executeUpdate() == 1;
        }
    }
 
    // ── private helpers ───────────────────────────────────────────────────
 
    private int getOrCreateStatusId(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT status_id FROM employment_status WHERE status_name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO employment_status (status_name) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
        }
        return 1;
    }
 
    private int getOrCreatePositionId(Connection conn, String name) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT position_id FROM `position` WHERE position_name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO `position` (position_name) VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) return rs.getInt(1); }
        }
        return 1;
    }
 
    private void upsertAllowance(Connection conn, String empId,
                                  String name, BigDecimal amount) throws SQLException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return;
        int allowanceId = 0;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT allowance_id FROM allowance WHERE allowance_name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) allowanceId = rs.getInt(1); }
        }
        if (allowanceId == 0) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO allowance (allowance_name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) allowanceId = rs.getInt(1); }
            }
        }
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO employee_allowance (employee_id, allowance_id, amount) "
              + "VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE amount = ?")) {
            ps.setString(1, empId);
            ps.setInt(2, allowanceId);
            ps.setBigDecimal(3, amount);
            ps.setBigDecimal(4, amount);
            ps.executeUpdate();
        }
    }
 
    private static BigDecimal orZero(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
 
    private Employee map(ResultSet rs) throws SQLException {
        String statusStr = rs.getString("status_name");
        Employee.EmploymentStatus status;
        try {
            status = Employee.EmploymentStatus.valueOf(
                statusStr != null ? statusStr.toUpperCase() : "REGULAR");
        } catch (IllegalArgumentException ex) {
            status = Employee.EmploymentStatus.REGULAR;
        }
 
        LocalDate birthday = LocalDate.now();
        Date sqlDate = rs.getDate("birth_date");
        if (sqlDate != null) birthday = sqlDate.toLocalDate();
 
        return new Employee.Builder(
                rs.getString("employee_id"),
                rs.getString("last_name"),
                rs.getString("first_name"),
                birthday)
            .withAddress(rs.getString("full_address"))
            .withPhoneNumber(rs.getString("phone_number"))
            .withSssNumber(rs.getString("sss_number"))
            .withPhilhealthNumber(rs.getString("philhealth_number"))
            .withTinNumber(rs.getString("tin_number"))
            .withPagIbigNumber(rs.getString("pagibig_number"))
            .withStatus(status)
            .withPosition(rs.getString("position_name"))
            .withImmediateSupervisor(rs.getString("supervisor_employee_id"))
            .withBasicSalary(orZero(rs.getBigDecimal("basic_salary")))
            .withRiceSubsidy(orZero(rs.getBigDecimal("rice_subsidy")))
            .withPhoneAllowance(orZero(rs.getBigDecimal("phone_allowance")))
            .withClothingAllowance(orZero(rs.getBigDecimal("clothing_allowance")))
            .withGrossSemiMonthlyRate(orZero(rs.getBigDecimal("gross_semi_monthly_rate")))
            .withHourlyRate(orZero(rs.getBigDecimal("hourly_rate")))
            .build();
    }
}
