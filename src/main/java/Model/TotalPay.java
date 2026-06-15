package Model;
 
/**
 * Encapsulates the result of a payroll calculation for a single employee.
 * Demonstrates Encapsulation (private fields) and Abstraction (method hides formula).
 */
public class TotalPay {
 
    private double gross;
    private double net;
    private double totalDeductions;
    private double hourlyRate;
    private double semiMonthlyRate;
 
    /**
     * Calculates gross, semi-monthly, hourly, and net pay for an employee.
     *
     * @param employee        the employee to calculate for
     * @param totalDeductions combined statutory + withholding deductions
     */
    public void calculatePayroll(Employee employee, double totalDeductions) {
        this.totalDeductions = totalDeductions;
 
        double monthly   = safeDouble(employee.getBasicSalary());
        double rice      = safeDouble(employee.getRiceSubsidy());
        double phone     = safeDouble(employee.getPhoneAllowance());
        double clothing  = safeDouble(employee.getClothingAllowance());
 
        this.semiMonthlyRate = monthly / 2.0;
        this.hourlyRate      = (monthly * 12.0) / 52.0 / 40.0;
        this.gross           = monthly + rice + phone + clothing;
        this.net             = this.gross - this.totalDeductions;
    }
 
    public double getSemiMonthlyRate() { return semiMonthlyRate; }
    public double getHourlyRate()      { return hourlyRate; }
    public double getGross()           { return gross; }
    public double getNet()             { return net; }
    public double getTotalDeductions() { return totalDeductions; }
 
    private double safeDouble(java.math.BigDecimal v) {
        return v != null ? v.doubleValue() : 0.0;
    }
}
