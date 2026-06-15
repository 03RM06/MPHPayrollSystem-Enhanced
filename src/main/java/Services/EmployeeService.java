package Services;
 
import Model.Employee;
import Utility.StringUtils;
import DAO.EmployeeDAO;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
 
/**
 * Service layer for Employee management.
 * Extends BaseService for shared error handling.
 */
public class EmployeeService extends BaseService {
 
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
 
    public void loadEmployeesToTable(JTable table) {
        String[] cols = {
            "ID", "Last Name", "First Name", "Birthday", "Address",
            "Phone Number", "SSS #", "PhilHealth #", "TIN #", "Pag-IBIG #",
            "Status", "Position", "Immediate Supervisor", "Basic Salary",
            "Rice Subsidy", "Phone Allowance", "Clothing Allowance",
            "Gross Semi-monthly Rate", "Hourly Rate"
        };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        try {
            List<Employee> employees = employeeDAO.findAll();
            if (employees == null || employees.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                    "No employee records found.", "Information",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            for (Object e : employees) {
                if (e != null) model.addRow(toRow((Employee) e));
            }
            table.setModel(model);
        } catch (SQLException ex) {
            showError("Database Error", ex.getMessage());
        } catch (Exception ex) {
            showError("Error", ex.getMessage());
        }
    }
 
    private Object[] toRow(Employee e) {
        return new Object[]{
            StringUtils.nullToEmpty(e.getEmployeeId()),
            StringUtils.nullToEmpty(e.getLastName()),
            StringUtils.nullToEmpty(e.getFirstName()),
            StringUtils.nullToEmpty(e.getFormattedBirthday()),
            StringUtils.nullToEmpty(e.getAddress()),
            StringUtils.nullToEmpty(e.getPhoneNumber()),
            StringUtils.nullToEmpty(e.getSssNumber()),
            StringUtils.nullToEmpty(e.getPhilhealthNumber()),
            StringUtils.nullToEmpty(e.getTinNumber()),
            StringUtils.nullToEmpty(e.getPagIbigNumber()),
            StringUtils.nullToEmpty(e.getStatus() != null ? e.getStatus().name() : ""),
            StringUtils.nullToEmpty(e.getPosition()),
            StringUtils.nullToEmpty(e.getImmediateSupervisor()),
            e.getBasicSalary()          != null ? e.getBasicSalary().toPlainString()          : "0.00",
            e.getRiceSubsidy()          != null ? e.getRiceSubsidy().toPlainString()          : "0.00",
            e.getPhoneAllowance()       != null ? e.getPhoneAllowance().toPlainString()       : "0.00",
            e.getClothingAllowance()    != null ? e.getClothingAllowance().toPlainString()    : "0.00",
            e.getGrossSemiMonthlyRate() != null ? e.getGrossSemiMonthlyRate().toPlainString() : "0.00",
            e.getHourlyRate()           != null ? e.getHourlyRate().toPlainString()           : "0.00",
        };
    }
}
