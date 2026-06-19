package Model.Report;

import java.math.BigDecimal;

public class PayrollSummaryRow {
    private final String employeeNo, fullName, position, department,
            sssNumber, philhealthNumber, pagibigNumber, tinNumber;
    private final BigDecimal grossIncome, sssContribution, philhealthContribution,
            pagibigContribution, withholdingTax, netPay;

    public PayrollSummaryRow(String employeeNo, String fullName, String position, String department,
            BigDecimal grossIncome, String sssNumber, BigDecimal sssContribution,
            String philhealthNumber, BigDecimal philhealthContribution,
            String pagibigNumber, BigDecimal pagibigContribution,
            String tinNumber, BigDecimal withholdingTax, BigDecimal netPay) {
        this.employeeNo = employeeNo; this.fullName = fullName;
        this.position = position; this.department = department;
        this.grossIncome = grossIncome; this.sssNumber = sssNumber;
        this.sssContribution = sssContribution; this.philhealthNumber = philhealthNumber;
        this.philhealthContribution = philhealthContribution; this.pagibigNumber = pagibigNumber;
        this.pagibigContribution = pagibigContribution; this.tinNumber = tinNumber;
        this.withholdingTax = withholdingTax; this.netPay = netPay;
    }

    public String getEmployeeNo() { return employeeNo; }
    public String getFullName() { return fullName; }
    public String getPosition() { return position; }
    public String getDepartment() { return department; }
    public BigDecimal getGrossIncome() { return grossIncome; }
    public String getSssNumber() { return sssNumber; }
    public BigDecimal getSssContribution() { return sssContribution; }
    public String getPhilhealthNumber() { return philhealthNumber; }
    public BigDecimal getPhilhealthContribution() { return philhealthContribution; }
    public String getPagibigNumber() { return pagibigNumber; }
    public BigDecimal getPagibigContribution() { return pagibigContribution; }
    public String getTinNumber() { return tinNumber; }
    public BigDecimal getWithholdingTax() { return withholdingTax; }
    public BigDecimal getNetPay() { return netPay; }
}