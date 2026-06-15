package Model;
 
/** Maps to time_and_attendance row. */
public class Attendance {
    private Integer id;
    private String  employeeId;
    private String  date;        // "yyyy-MM-dd"
    private String  timeIn;      // "HH:mm" or "HH:mm:ss"
    private String  timeOut;
    private Double  hoursWorked;
    private Double  overtimeHours;
    private String  notes;
 
    public Integer getId()            { return id; }
    public String  getEmployeeId()    { return employeeId; }
    public String  getDate()          { return date; }
    public String  getTimeIn()        { return timeIn; }
    public String  getTimeOut()       { return timeOut; }
    public Double  getHoursWorked()   { return hoursWorked; }
    public Double  getOvertimeHours() { return overtimeHours; }
    public String  getNotes()         { return notes; }
 
    public void setId(Integer id)               { this.id = id; }
    public void setEmployeeId(String employeeId){ this.employeeId = employeeId; }
    public void setDate(String date)            { this.date = date; }
    public void setTimeIn(String timeIn)        { this.timeIn = timeIn; }
    public void setTimeOut(String timeOut)      { this.timeOut = timeOut; }
    public void setHoursWorked(Double v)        { this.hoursWorked = v; }
    public void setOvertimeHours(Double v)      { this.overtimeHours = v; }
    public void setNotes(String notes)          { this.notes = notes; }
}
