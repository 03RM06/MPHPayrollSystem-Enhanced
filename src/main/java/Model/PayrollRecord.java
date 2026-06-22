package Model;

import java.time.LocalDateTime;

/**
 * Represents one employee's computed payroll for a given period.
 * employee_name is denormalized for display; it is NOT persisted to the DB —
 * it is populated via JOIN when reading from payroll_record.
 */
public class PayrollRecord {

    private int           recordId;
    private int           periodId;
    private String        employeeId;
    private String        employeeName;        // display-only, from JOIN
    private double        basicSalary;
    private double        grossPay;
    private double        sssDeduction;
    private double        philhealthDeduction;
    private double        pagibigDeduction;
    private double        withholdingTax;
    private double        totalDeductions;
    private double        netPay;
    private LocalDateTime computedAt;

    public PayrollRecord() {}

    // ── Getters ────────────────────────────────────────────────────────────
    public int           getRecordId()            { return recordId;            }
    public int           getPeriodId()             { return periodId;            }
    public String        getEmployeeId()           { return employeeId;          }
    public String        getEmployeeName()         { return employeeName;        }
    public double        getBasicSalary()          { return basicSalary;         }
    public double        getGrossPay()             { return grossPay;            }
    public double        getSssDeduction()         { return sssDeduction;        }
    public double        getPhilhealthDeduction()  { return philhealthDeduction; }
    public double        getPagibigDeduction()     { return pagibigDeduction;    }
    public double        getWithholdingTax()       { return withholdingTax;      }
    public double        getTotalDeductions()      { return totalDeductions;     }
    public double        getNetPay()               { return netPay;              }
    public LocalDateTime getComputedAt()           { return computedAt;          }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setRecordId(int v)               { this.recordId            = v; }
    public void setPeriodId(int v)               { this.periodId            = v; }
    public void setEmployeeId(String v)          { this.employeeId          = v; }
    public void setEmployeeName(String v)        { this.employeeName        = v; }
    public void setBasicSalary(double v)         { this.basicSalary         = v; }
    public void setGrossPay(double v)            { this.grossPay            = v; }
    public void setSssDeduction(double v)        { this.sssDeduction        = v; }
    public void setPhilhealthDeduction(double v) { this.philhealthDeduction = v; }
    public void setPagibigDeduction(double v)    { this.pagibigDeduction    = v; }
    public void setWithholdingTax(double v)      { this.withholdingTax      = v; }
    public void setTotalDeductions(double v)     { this.totalDeductions     = v; }
    public void setNetPay(double v)              { this.netPay              = v; }
    public void setComputedAt(LocalDateTime v)   { this.computedAt          = v; }

    @Override
    public String toString() {
        return String.format("PayrollRecord{employee=%s, gross=%.2f, net=%.2f}",
                employeeId, grossPay, netPay);
    }
}
