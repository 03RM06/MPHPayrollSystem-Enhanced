package Model;

import java.time.LocalDateTime;

/** Represents a single entry in the audit_log table. */
public class AuditLog {

    private long          auditId;
    private String        performedBy;
    private String        action;
    private String        targetEntity;
    private String        targetId;
    private String        details;
    private LocalDateTime performedAt;

    public AuditLog() {}

    public AuditLog(long auditId, String performedBy, String action,
                    String targetEntity, String targetId,
                    String details, LocalDateTime performedAt) {
        this.auditId      = auditId;
        this.performedBy  = performedBy;
        this.action       = action;
        this.targetEntity = targetEntity;
        this.targetId     = targetId;
        this.details      = details;
        this.performedAt  = performedAt;
    }

    // ── getters ───────────────────────────────────────────────────────────

    public long          getAuditId()      { return auditId; }
    public String        getPerformedBy()  { return performedBy; }
    public String        getAction()       { return action; }
    public String        getTargetEntity() { return targetEntity; }
    public String        getTargetId()     { return targetId; }
    public String        getDetails()      { return details; }
    public LocalDateTime getPerformedAt()  { return performedAt; }

    // ── setters ───────────────────────────────────────────────────────────

    public void setAuditId(long v)           { this.auditId      = v; }
    public void setPerformedBy(String v)     { this.performedBy  = v; }
    public void setAction(String v)          { this.action       = v; }
    public void setTargetEntity(String v)    { this.targetEntity = v; }
    public void setTargetId(String v)        { this.targetId     = v; }
    public void setDetails(String v)         { this.details      = v; }
    public void setPerformedAt(LocalDateTime v) { this.performedAt = v; }
}
