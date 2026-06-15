package Services;
 
import Model.Employee;

/**
 * Computes statutory deductions: SSS, PhilHealth, and Pag-IBIG.
 * Implements Polymorphism via DeductionCalculator interface.
 * Applies Abstraction: SSS contribution table is hidden from callers.
 */
public class SalaryDeduction implements DeductionCalculator {
 
    private double sssContribution;
    private double pagibigContribution;
    private double philHealthContribution;
    private double totalStatutoryAmount;
 
    @Override
    public void calculate(Employee employee) {
        if (employee == null || employee.getBasicSalary() == null) {
            reset(); return;
        }
        double salary = employee.getBasicSalary().doubleValue();
 
        this.pagibigContribution    = 100.00;
        this.philHealthContribution = salary * 0.03;
        this.sssContribution        = calculateSSS(salary);
        this.totalStatutoryAmount   = sssContribution + pagibigContribution + philHealthContribution;
    }
 
    @Override
    public double getAmount()       { return totalStatutoryAmount; }
 
    @Override
    public String getDescription()  {
        return String.format("Statutory: SSS(%.2f), PhilHealth(%.2f), Pag-IBIG(%.2f)",
                sssContribution, philHealthContribution, pagibigContribution);
    }
 
    public double getSSS()             { return sssContribution; }
    public double getPagibigDeduct()   { return pagibigContribution; }
    public double getPhilDeduct()      { return philHealthContribution; }
 
    // ── SSS contribution table ────────────────────────────────────────────
    private double calculateSSS(double salary) {
        if (salary <  3250) return 135.00;
        if (salary <  3750) return 157.00;
        if (salary <  4250) return 180.00;
        if (salary <  4750) return 202.50;
        if (salary <  5250) return 225.00;
        if (salary <  5750) return 247.50;
        if (salary <  6250) return 270.00;
        if (salary <  6750) return 292.50;
        if (salary <  7250) return 315.00;
        if (salary <  7750) return 337.50;
        if (salary <  8250) return 360.00;
        if (salary <  8750) return 382.50;
        if (salary <  9250) return 405.00;
        if (salary <  9750) return 427.50;
        if (salary < 10250) return 450.00;
        if (salary < 10750) return 472.50;
        if (salary < 11250) return 495.00;
        if (salary < 11750) return 517.50;
        if (salary < 12250) return 540.00;
        if (salary < 12750) return 562.50;
        if (salary < 13250) return 585.00;
        if (salary < 13750) return 607.50;
        if (salary < 14250) return 630.00;
        if (salary < 14750) return 652.50;
        if (salary < 15250) return 675.00;
        if (salary < 15750) return 697.50;
        if (salary < 16250) return 720.00;
        if (salary < 16750) return 742.50;
        if (salary < 17250) return 765.00;
        if (salary < 17750) return 787.50;
        if (salary < 18250) return 810.00;
        if (salary < 18750) return 832.50;
        if (salary < 19250) return 855.00;
        if (salary < 19750) return 877.50;
        if (salary < 20250) return 900.00;
        if (salary < 20750) return 922.50;
        if (salary < 21750) return 967.50;
        if (salary < 22250) return 990.00;
        if (salary < 22750) return 1012.50;
        if (salary < 23250) return 1035.00;
        if (salary < 23750) return 1057.50;
        if (salary < 24250) return 1080.00;
        if (salary < 24750) return 1102.50;
        return 1125.50;
    }
 
    private void reset() {
        sssContribution = pagibigContribution = philHealthContribution = totalStatutoryAmount = 0;
    }
}
