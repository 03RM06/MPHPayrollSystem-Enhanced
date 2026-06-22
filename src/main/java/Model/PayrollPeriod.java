package Model;

import java.time.LocalDate;

/**
 * Represents a payroll processing period (e.g. "June 2024 – 1st Half").
 * A period is either OPEN (editable, payroll can be run) or CLOSED (read-only).
 */
public class PayrollPeriod {

    private int       periodId;
    private String    periodName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String    status;   // "OPEN" or "CLOSED"

    public PayrollPeriod() {}

    public PayrollPeriod(int periodId, String periodName,
                         LocalDate startDate, LocalDate endDate, String status) {
        this.periodId   = periodId;
        this.periodName = periodName;
        this.startDate  = startDate;
        this.endDate    = endDate;
        this.status     = status;
    }

    // ── Getters ────────────────────────────────────────────────────────────
    public int       getPeriodId()   { return periodId;   }
    public String    getPeriodName() { return periodName; }
    public LocalDate getStartDate()  { return startDate;  }
    public LocalDate getEndDate()    { return endDate;    }
    public String    getStatus()     { return status;     }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setPeriodId(int v)       { this.periodId   = v; }
    public void setPeriodName(String v)  { this.periodName = v; }
    public void setStartDate(LocalDate v){ this.startDate  = v; }
    public void setEndDate(LocalDate v)  { this.endDate    = v; }
    public void setStatus(String v)      { this.status     = v; }

    public boolean isOpen() {
        return "OPEN".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return periodName + " [" + status + "]";
    }
}
