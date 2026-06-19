package Model.Report;

public class TimecardRow {
    private final String date, day, timeIn, timeOut, totalHours, remarks;

    public TimecardRow(String date, String day, String timeIn, String timeOut,
                        String totalHours, String remarks) {
        this.date = date; this.day = day; this.timeIn = timeIn;
        this.timeOut = timeOut; this.totalHours = totalHours; this.remarks = remarks;
    }

    public String getDate() { return date; }
    public String getDay() { return day; }
    public String getTimeIn() { return timeIn; }
    public String getTimeOut() { return timeOut; }
    public String getTotalHours() { return totalHours; }
    public String getRemarks() { return remarks; }
}