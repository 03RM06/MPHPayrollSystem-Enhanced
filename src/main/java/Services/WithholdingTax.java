package Services;

import Model.Employee;

/**
 * Computes BIR withholding tax (monthly bracket basis).
 * Polymorphism: implements DeductionCalculator.
 * Encapsulation: bracket logic is delegated to StatutoryRates.
 */
public class WithholdingTax implements DeductionCalculator {

    private double withholdingTaxAmount;
    private double totalStatutoryDeductions;
    private String info;

    @Override
    public void calculate(Employee employee) {
        if (employee == null || employee.getBasicSalary() == null) {
            withholdingTaxAmount = 0;
            info = "No employee data";
            return;
        }
        double taxable = employee.getBasicSalary().doubleValue() - totalStatutoryDeductions;
        withholdingTaxAmount = StatutoryRates.computeWithholdingTax(taxable);
        info = String.format("Withholding tax on taxable income %.2f", taxable);
    }

    @Override public double getAmount()      { return withholdingTaxAmount; }
    @Override public String getDescription() { return info; }

    public void   setTotalDeduction(double v) { this.totalStatutoryDeductions = v; }
    public double getTotalStatutoryDeductions(){ return totalStatutoryDeductions; }
}
