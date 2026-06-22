package Services;

import DAO.EmployeeDAO;
import DAO.PayrollRecordDAO;
import Model.Employee;
import Model.PayrollRecord;
import Model.TotalPay;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates payroll computation for a single employee or an entire period.
 * Uses SalaryDeduction + WithholdingTax (Strategy pattern via DeductionCalculator).
 */
public class PayrollService extends BaseService {
 
    /** Immutable result record returned to the UI layer. */
    public static class PayrollResult {
        public double gross;
        public double deductions;
        public double net;
        public String details;
        public double semiMonthly;
        public double hourly;
 
        @Override
        public String toString() {
            return String.format("Gross: %.2f | Deductions: %.2f | Net: %.2f | %s",
                    gross, deductions, net, details);
        }
    }
 
    /**
     * Computes the payroll for a given employee.
     *
     * @param employee a fully-loaded Employee object (with salary data)
     * @return PayrollResult with gross, deductions, and net pay
     */
    public PayrollResult computeForEmployee(Employee employee) {
        PayrollResult result = new PayrollResult();
        if (!isValid(employee)) {
            result.details = "Invalid employee data: missing basic salary";
            return result;
        }
        try {
            // 1. Statutory deductions (SSS, PhilHealth, Pag-IBIG)
            SalaryDeduction statutory = new SalaryDeduction();
            statutory.calculate(employee);
 
            // 2. Withholding tax (applied on net-of-statutory income)
            WithholdingTax tax = new WithholdingTax();
            tax.setTotalDeduction(statutory.getAmount());
            tax.calculate(employee);
 
            // 3. Total deductions & net pay
            double totalDeductions = statutory.getAmount() + tax.getAmount();
 
            TotalPay tp = new TotalPay();
            tp.calculatePayroll(employee, totalDeductions);
 
            result.gross       = tp.getGross();
            result.semiMonthly = tp.getSemiMonthlyRate();
            result.hourly      = tp.getHourlyRate();
            result.deductions  = totalDeductions;
            result.net         = tp.getNet();
            result.details     = String.format("Statutory: %.2f | Tax: %s",
                                     statutory.getAmount(), tax.getDescription());
        } catch (Exception ex) {
            showError("Payroll Calculation Error", ex.getMessage());
            result.details = "Error: " + ex.getMessage();
        }
        return result;
    }
 
    /**
     * Computes payroll for every REGULAR employee and persists each result
     * as a PayrollRecord for the specified period.
     *
     * <p>Employees that have no basic salary or whose salary is zero are
     * skipped silently.  All other employees whose status is REGULAR are
     * processed; any employee whose computation fails is logged and skipped
     * so the rest of the batch still completes.</p>
     *
     * @param periodId the ID of an existing payroll_period row
     * @return list of successfully computed and saved PayrollRecord objects
     * @throws SQLException if a database error occurs that cannot be recovered
     */
    public List<PayrollRecord> processPayrollForPeriod(int periodId) throws SQLException {
        EmployeeDAO      employeeDAO = new EmployeeDAO();
        PayrollRecordDAO recordDAO   = new PayrollRecordDAO();

        List<Employee>     allEmployees = employeeDAO.findAll();
        List<PayrollRecord> results     = new ArrayList<>();

        for (Employee emp : allEmployees) {
            // Only process REGULAR employees with a positive basic salary
            if (emp.getStatus() != Employee.EmploymentStatus.REGULAR) continue;
            if (!isValid(emp)) continue;

            try {
                // 1. Statutory deductions
                SalaryDeduction statutory = new SalaryDeduction();
                statutory.calculate(emp);

                // 2. Withholding tax (on taxable = basic - statutory)
                WithholdingTax tax = new WithholdingTax();
                tax.setTotalDeduction(statutory.getAmount());
                tax.calculate(emp);

                // 3. Gross and net
                double totalDeductions = statutory.getAmount() + tax.getAmount();
                TotalPay tp = new TotalPay();
                tp.calculatePayroll(emp, totalDeductions);

                // 4. Build record
                PayrollRecord record = new PayrollRecord();
                record.setPeriodId(periodId);
                record.setEmployeeId(emp.getEmployeeId());
                record.setEmployeeName(emp.getLastName() + ", " + emp.getFirstName());
                record.setBasicSalary(emp.getBasicSalary().doubleValue());
                record.setGrossPay(tp.getGross());
                record.setSssDeduction(statutory.getSSS());
                record.setPhilhealthDeduction(statutory.getPhilDeduct());
                record.setPagibigDeduction(statutory.getPagibigDeduct());
                record.setWithholdingTax(tax.getAmount());
                record.setTotalDeductions(totalDeductions);
                record.setNetPay(tp.getNet());

                // 5. Persist (INSERT or ON DUPLICATE KEY UPDATE)
                recordDAO.save(record);
                results.add(record);

            } catch (SQLException ex) {
                // Rethrow DB errors so the caller can handle them
                throw ex;
            } catch (Exception ex) {
                // Calculation errors: log and skip this employee
                System.err.println("[PayrollService] Skipped employee "
                        + emp.getEmployeeId() + ": " + ex.getMessage());
            }
        }
        return results;
    }

    private boolean isValid(Employee e) {
        return e != null
            && e.getBasicSalary() != null
            && e.getBasicSalary().compareTo(BigDecimal.ZERO) >= 0;
    }
}
