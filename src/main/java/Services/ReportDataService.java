package Services;

import DAO.Database;
import DAO.EmployeeDAO;
import Model.Employee;
import Model.Report.PayrollSummaryRow;
import Model.Report.TimecardRow;

import java.math.BigDecimal;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ReportDataService {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    public List<PayrollSummaryRow> buildPayrollSummary() throws SQLException {
        Map<String, String> departmentByEmployee = loadDepartmentNames();
        List<Employee> employees = employeeDAO.findAll();
        List<PayrollSummaryRow> rows = new ArrayList<>();

        for (Employee e : employees) {
            BigDecimal basicSalary = e.getBasicSalary() != null ? e.getBasicSalary() : BigDecimal.ZERO;

            Employee tempEmp = new Employee.Builder(
                    e.getEmployeeId(), e.getLastName(), e.getFirstName(), e.getBirthday())
                    .withBasicSalary(basicSalary)
                    .withRiceSubsidy(e.getRiceSubsidy())
                    .withPhoneAllowance(e.getPhoneAllowance())
                    .withClothingAllowance(e.getClothingAllowance())
                    .withHourlyRate(e.getHourlyRate())
                    .build();

            Services.SalaryDeduction statutory = new Services.SalaryDeduction();
            Services.WithholdingTax tax = new Services.WithholdingTax();
            statutory.calculate(tempEmp);
            tax.setTotalDeduction(statutory.getAmount());
            tax.calculate(tempEmp);

            double totalDeductions = statutory.getAmount() + tax.getAmount();
            Model.TotalPay total = new Model.TotalPay();
            total.calculatePayroll(tempEmp, totalDeductions);

            rows.add(new PayrollSummaryRow(
                    e.getEmployeeId(), e.getLastName() + ", " + e.getFirstName(),
                    e.getPosition(), departmentByEmployee.getOrDefault(e.getEmployeeId(), ""),
                    BigDecimal.valueOf(total.getGross()),
                    e.getSssNumber(), BigDecimal.valueOf(statutory.getSSS()),
                    e.getPhilhealthNumber(), BigDecimal.valueOf(statutory.getPhilDeduct()),
                    e.getPagIbigNumber(), BigDecimal.valueOf(statutory.getPagibigDeduct()),
                    e.getTinNumber(), BigDecimal.valueOf(tax.getAmount()),
                    BigDecimal.valueOf(total.getNet())));
        }
        return rows;
    }

    private Map<String, String> loadDepartmentNames() throws SQLException {
        String sql = "SELECT e.employee_id, d.department_name " +
                     "FROM employee e " +
                     "LEFT JOIN position p ON p.position_id = e.position_id " +
                     "LEFT JOIN department d ON d.department_id = p.department_id";
        Map<String, String> result = new HashMap<>();
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.put(rs.getString("employee_id"), rs.getString("department_name"));
            }
        }
        return result;
    }

    public List<TimecardRow> buildTimecard(String employeeId, YearMonth yearMonth) throws SQLException {
        return buildTimecard(employeeId, yearMonth.atDay(1), yearMonth.atEndOfMonth());
    }

    public List<TimecardRow> buildTimecard(String employeeId, LocalDate start, LocalDate end) throws SQLException {
        Map<LocalDate, String[]> attendanceByDate = new HashMap<>();
        
        String sql = "SELECT attendance_date, time_in, time_out FROM time_and_attendance " +
                     "WHERE employee_id = ? AND attendance_date BETWEEN ? AND ?";
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, employeeId);
            ps.setDate(2, java.sql.Date.valueOf(start));
            ps.setDate(3, java.sql.Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("attendance_date").toLocalDate();
                    Time in = rs.getTime("time_in");
                    Time out = rs.getTime("time_out");
                    attendanceByDate.put(date, new String[]{
                            in != null ? in.toString() : null,
                            out != null ? out.toString() : null});
                }
            }
        }

        List<TimecardRow> rows = new ArrayList<>();
        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            boolean isWeekend = d.getDayOfWeek() == DayOfWeek.SATURDAY
                              || d.getDayOfWeek() == DayOfWeek.SUNDAY;
            String[] record = attendanceByDate.get(d);
            String timeIn = (record != null && record[0] != null) ? record[0] : "-";
            String timeOut = (record != null && record[1] != null) ? record[1] : "-";

            String remarks, totalHours;
            if (record != null && record[0] != null && record[1] != null) {
                remarks = "Present"; totalHours = "8 hrs";
            } else if (isWeekend) {
                remarks = "Weekend"; totalHours = "0 hrs";
            } else {
                remarks = "Absent"; totalHours = "0 hrs";
            }

            rows.add(new TimecardRow(
                    d.format(DateTimeFormatter.ofPattern("MMM dd")),
                    d.getDayOfWeek().toString().substring(0, 3),
                    timeIn, timeOut, totalHours, remarks));
        }
        return rows;
    }
}