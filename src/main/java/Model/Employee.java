package Model;
 
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Stream;
 
/**
 * Immutable-by-default employee entity.
 * Uses the Builder pattern for controlled construction.
 * Implements Payable to expose a gross compensation figure.
 */
public class Employee implements Payable {
 
    private final String employeeId;
    private String lastName;
    private String firstName;
    private LocalDate birthday;
    private String address;
    private String phoneNumber;
    private String sssNumber;
    private String philhealthNumber;
    private String tinNumber;
    private String pagIbigNumber;
    private EmploymentStatus status;
    private String position;
    private String immediateSupervisor;
    private BigDecimal basicSalary;
    private BigDecimal riceSubsidy;
    private BigDecimal phoneAllowance;
    private BigDecimal clothingAllowance;
    private BigDecimal grossSemiMonthlyRate;
    private BigDecimal hourlyRate;
 
    /** Returns the sum of basic salary and all allowances. */
    @Override
    public BigDecimal getAmountToPay() {
        return Stream.of(basicSalary, riceSubsidy, phoneAllowance, clothingAllowance)
                     .filter(Objects::nonNull)
                     .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
 
    public String getFormattedBirthday() {
        return birthday == null ? ""
             : birthday.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    }
 
    private Employee(Builder b) {
        this.employeeId           = b.employeeId;
        this.lastName             = b.lastName;
        this.firstName            = b.firstName;
        this.birthday             = b.birthday;
        this.address              = b.address;
        this.phoneNumber          = b.phoneNumber;
        this.sssNumber            = b.sssNumber;
        this.philhealthNumber     = b.philhealthNumber;
        this.tinNumber            = b.tinNumber;
        this.pagIbigNumber        = b.pagIbigNumber;
        this.status               = b.status;
        this.position             = b.position;
        this.immediateSupervisor  = b.immediateSupervisor;
        this.basicSalary          = b.basicSalary;
        this.riceSubsidy          = b.riceSubsidy;
        this.phoneAllowance       = b.phoneAllowance;
        this.clothingAllowance    = b.clothingAllowance;
        this.grossSemiMonthlyRate = b.grossSemiMonthlyRate;
        this.hourlyRate           = b.hourlyRate;
    }
 
    // ── getters ──────────────────────────────────────────────────────────
    public String           getEmployeeId()           { return employeeId; }
    public String           getLastName()             { return lastName; }
    public String           getFirstName()            { return firstName; }
    public LocalDate        getBirthday()             { return birthday; }
    public String           getAddress()              { return address; }
    public String           getPhoneNumber()          { return phoneNumber; }
    public String           getSssNumber()            { return sssNumber; }
    public String           getPhilhealthNumber()     { return philhealthNumber; }
    public String           getTinNumber()            { return tinNumber; }
    public String           getPagIbigNumber()        { return pagIbigNumber; }
    public EmploymentStatus getStatus()               { return status; }
    public String           getPosition()             { return position; }
    public String           getImmediateSupervisor()  { return immediateSupervisor; }
    public BigDecimal       getBasicSalary()          { return basicSalary; }
    public BigDecimal       getRiceSubsidy()          { return riceSubsidy; }
    public BigDecimal       getPhoneAllowance()       { return phoneAllowance; }
    public BigDecimal       getClothingAllowance()    { return clothingAllowance; }
    public BigDecimal       getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
    public BigDecimal       getHourlyRate()           { return hourlyRate; }
 
    // ── setters (limited) ─────────────────────────────────────────────────
    public void setLastName(String v)           { this.lastName = requireNonBlank(v, "Last name"); }
    public void setSssNumber(String v)          { this.sssNumber = clean(v); }
    public void setPhilhealthNumber(String v)   { this.philhealthNumber = clean(v); }
    public void setStatus(EmploymentStatus s)   { this.status = Objects.requireNonNull(s, "Status"); }
 
    private String requireNonBlank(String v, String field) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException(field + " cannot be blank");
        return v.trim();
    }
    private String clean(String v) { return v == null ? "" : v.trim(); }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        return Objects.equals(employeeId, ((Employee) o).employeeId);
    }
    @Override public int    hashCode() { return Objects.hash(employeeId); }
    @Override public String toString() {
        return "Employee{id=" + employeeId + ", name=" + lastName + ", " + firstName + "}";
    }
 
    // ── Employment status enum ────────────────────────────────────────────
    public enum EmploymentStatus {
        PROBATIONARY, REGULAR, RESIGNED, TERMINATED, RETIRED
    }
 
    // ── Builder ───────────────────────────────────────────────────────────
    public static class Builder {
        private final String employeeId;
        private final String lastName;
        private final String firstName;
        private final LocalDate birthday;
 
        private String address = "", phoneNumber = "", sssNumber = "";
        private String philhealthNumber = "", tinNumber = "", pagIbigNumber = "";
        private EmploymentStatus status = EmploymentStatus.PROBATIONARY;
        private String position = "", immediateSupervisor = "";
        private BigDecimal basicSalary = BigDecimal.ZERO, riceSubsidy = BigDecimal.ZERO;
        private BigDecimal phoneAllowance = BigDecimal.ZERO, clothingAllowance = BigDecimal.ZERO;
        private BigDecimal grossSemiMonthlyRate = BigDecimal.ZERO, hourlyRate = BigDecimal.ZERO;
 
        public Builder(String employeeId, String lastName,
                       String firstName, LocalDate birthday) {
            this.employeeId = employeeId;
            this.lastName   = lastName;
            this.firstName  = firstName;
            this.birthday   = birthday;
        }
 
        public Builder withAddress(String v)              { address = or(v, ""); return this; }
        public Builder withPhoneNumber(String v)          { phoneNumber = or(v, ""); return this; }
        public Builder withSssNumber(String v)            { sssNumber = or(v, ""); return this; }
        public Builder withPhilhealthNumber(String v)     { philhealthNumber = or(v, ""); return this; }
        public Builder withTinNumber(String v)            { tinNumber = or(v, ""); return this; }
        public Builder withPagIbigNumber(String v)        { pagIbigNumber = or(v, ""); return this; }
        public Builder withStatus(EmploymentStatus v)     { status = v != null ? v : EmploymentStatus.PROBATIONARY; return this; }
        public Builder withPosition(String v)             { position = or(v, ""); return this; }
        public Builder withImmediateSupervisor(String v)  { immediateSupervisor = or(v, ""); return this; }
        public Builder withBasicSalary(BigDecimal v)      { if (v != null) basicSalary = v; return this; }
        public Builder withRiceSubsidy(BigDecimal v)      { riceSubsidy = or(v, BigDecimal.ZERO); return this; }
        public Builder withPhoneAllowance(BigDecimal v)   { phoneAllowance = or(v, BigDecimal.ZERO); return this; }
        public Builder withClothingAllowance(BigDecimal v){ clothingAllowance = or(v, BigDecimal.ZERO); return this; }
        public Builder withGrossSemiMonthlyRate(BigDecimal v){ grossSemiMonthlyRate = or(v, BigDecimal.ZERO); return this; }
        public Builder withHourlyRate(BigDecimal v)       { hourlyRate = or(v, BigDecimal.ZERO); return this; }
 
        public Employee build() { return new Employee(this); }
 
        private static <T> T or(T v, T def) { return v != null ? v : def; }
    }
}
