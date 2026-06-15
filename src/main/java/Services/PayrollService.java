package Services;
 
import Model.Employee;
import Model.TotalPay;
import java.math.BigDecimal;
 
/**
 * Orchestrates payroll computation for a single employee.
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
 
    private boolean isValid(Employee e) {
        return e != null
            && e.getBasicSalary() != null
            && e.getBasicSalary().compareTo(BigDecimal.ZERO) >= 0;
    }
}
