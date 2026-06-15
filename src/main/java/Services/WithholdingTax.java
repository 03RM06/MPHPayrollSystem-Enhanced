package Services;
 
import Model.Employee;

/**
 * Computes BIR withholding tax (monthly bracket basis).
 * Polymorphism: implements DeductionCalculator.
 * Encapsulation: bracket logic is private.
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
        applyBrackets(taxable);
    }
 
    private void applyBrackets(double taxable) {
        if (taxable < 20832) {
            withholdingTaxAmount = 0;
            info = "No withholding tax";
        } else if (taxable <= 33333) {
            withholdingTaxAmount = taxable * 0.20;
            info = String.format("20%% of %.2f", taxable - 20833);
        } else if (taxable <= 66667) {
            withholdingTaxAmount = taxable * 0.25 - 2500;
            info = String.format("2,500 + 25%% of %.2f", taxable - 33333);
        } else if (taxable <= 166667) {
            withholdingTaxAmount = taxable * 0.30 - 10833;
            info = String.format("10,833 + 30%% of %.2f", taxable - 66667);
        } else if (taxable <= 666667) {
            withholdingTaxAmount = taxable * 0.32 - 40833.33;
            info = String.format("40,833.33 + 32%% of %.2f", taxable - 166667);
        } else {
            withholdingTaxAmount = taxable * 0.35 - 200833.33;
            info = String.format("200,833.33 + 35%% of %.2f", taxable - 666667);
        }
    }
 
    @Override public double getAmount()      { return withholdingTaxAmount; }
    @Override public String getDescription() { return info; }
 
    public void   setTotalDeduction(double v) { this.totalStatutoryDeductions = v; }
    public double getTotalStatutoryDeductions(){ return totalStatutoryDeductions; }
}
