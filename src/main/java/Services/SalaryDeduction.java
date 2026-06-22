package Services;

import Model.Employee;

/**
 * Computes statutory deductions: SSS, PhilHealth, and Pag-IBIG.
 * Implements Polymorphism via DeductionCalculator interface.
 * Applies Abstraction: computation logic is delegated to StatutoryRates.
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

        this.sssContribution        = StatutoryRates.computeSSS(salary);
        this.philHealthContribution = StatutoryRates.computePhilHealth(salary);
        this.pagibigContribution    = StatutoryRates.computePagIbig(salary);
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

    private void reset() {
        sssContribution = pagibigContribution = philHealthContribution = totalStatutoryAmount = 0;
    }
}
