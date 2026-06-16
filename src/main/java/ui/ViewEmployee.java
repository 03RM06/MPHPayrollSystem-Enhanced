package ui;
 
import DAO.EmployeeDAO;
import Model.Employee;
import Model.Role;
import Model.UserAccount;
import Services.SalaryDeduction;
import Services.SessionManager;
import Services.WithholdingTax;
import Model.TotalPay;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.logging.Level;
import javax.swing.JOptionPane;
 
public class ViewEmployee extends javax.swing.JFrame {
 
    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(ViewEmployee.class.getName());
 
    private final UserAccount  currentUser;
    private       Employee     currentEmployee;
    private javax.swing.JFrame parentFrame = null;
 
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
 
    // ── Roles that can access Leave Management ────────────────────────────
    private static final java.util.Set<Role> LEAVE_MGMT_ROLES =
            java.util.EnumSet.of(Role.ADMIN, Role.HR);
 
    // ─────────────────────────────────────────────────────────────────────
    //  Constructors
    // ─────────────────────────────────────────────────────────────────────
 
    public ViewEmployee(UserAccount user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("MotorPH Payroll System");
        configureRoleVisibility();
        loadCurrentEmployee();
    }
 
    public ViewEmployee(UserAccount user, Employee employee,
                        javax.swing.JFrame parent) {
        this.currentUser     = user;
        this.currentEmployee = employee;
        this.parentFrame     = parent;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("MotorPH Payroll System — " +
                 employee.getFirstName() + " " + employee.getLastName());
        configureRoleVisibility();
        populateFields(employee);
    }
 
    public ViewEmployee() {
        this.currentUser = null;
        initComponents();
    }
 
    // ─────────────────────────────────────────────────────────────────────
    //  Role-based visibility
    // ─────────────────────────────────────────────────────────────────────
 
   private void configureRoleVisibility() {
    // All buttons visible to all roles
    // Access control is enforced on click inside each button's action handler
}
 
    // ─────────────────────────────────────────────────────────────────────
    //  Data loading
    // ─────────────────────────────────────────────────────────────────────
 
    private void loadCurrentEmployee() {
        if (currentUser == null) return;
        String empId = currentUser.getEmployeeID();
        if (empId == null || empId.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Your account is not linked to an employee record.",
                    "Account Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            currentEmployee = employeeDAO.findById(empId);
            if (currentEmployee == null) {
                JOptionPane.showMessageDialog(this,
                        "No employee record found for ID: " + empId,
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            populateFields(currentEmployee);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to load employee", ex);
            JOptionPane.showMessageDialog(this,
                    "Database error:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void populateFields(Employee e) {
        jTextFieldEmpNum1.setText(safe(e.getEmployeeId()));
        jTextFieldLastName1.setText(safe(e.getLastName()));
        jTextFieldFirstName1.setText(safe(e.getFirstName()));
        jTextAreaAddress1.setText(safe(e.getAddress()));
        jTextFieldPhoneNum1.setText(safe(e.getPhoneNumber()));
        jTextFieldBirthDay1.setText(safe(e.getFormattedBirthday()));
        jTextFieldStatus1.setText(e.getStatus() != null ? e.getStatus().name() : "");
        jTextFieldPosition1.setText(safe(e.getPosition()));
        jTextFieldSupervisor1.setText(safe(e.getImmediateSupervisor()));
        jTextFieldSSSNum1.setText(safe(e.getSssNumber()));
        jTextFieldPhilHealthNum1.setText(safe(e.getPhilhealthNumber()));
        jTextFieldTINNum1.setText(safe(e.getTinNumber()));
        jTextFieldPagIBIGNum1.setText(safe(e.getPagIbigNumber()));
        jTextFieldBasicSalary1.setText(fmt(e.getBasicSalary()));
        jTextFieldSemiMonthlyRate1.setText(fmt(e.getGrossSemiMonthlyRate()));
        jTextFieldHourlyRate1.setText(fmt(e.getHourlyRate()));
        jTextFieldRiceSubsidy1.setText(fmt(e.getRiceSubsidy()));
        jTextFieldPhoneAllowance1.setText(fmt(e.getPhoneAllowance()));
        jTextFieldClothingAllowance1.setText(fmt(e.getClothingAllowance()));
        jTextFieldSSSContribution1.setText("");
        jTextFieldPhilHealthContribution1.setText("");
        jTextFieldPagIBIGContribution1.setText("");
        jTextFieldGrossPay1.setText("");
        jTextFieldNetPay1.setText("");
    }
 
    private String safe(String v) { return v != null ? v : ""; }
    private String fmt(BigDecimal v) {
        return v != null ? String.format("%,.2f", v) : "0.00";
    }
 
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jTextFieldEmpNum1 = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jTextFieldLastName1 = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jTextFieldFirstName1 = new javax.swing.JTextField();
        jLabel37 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jTextFieldStatus1 = new javax.swing.JTextField();
        jTextFieldPosition1 = new javax.swing.JTextField();
        jTextFieldSupervisor1 = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jTextFieldSSSNum1 = new javax.swing.JTextField();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jTextFieldPagIBIGNum1 = new javax.swing.JTextField();
        jTextFieldTINNum1 = new javax.swing.JTextField();
        jTextFieldPhilHealthNum1 = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jTextFieldBirthDay1 = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jTextFieldPhoneNum1 = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaAddress1 = new javax.swing.JTextArea();
        jComboBoxMonth1 = new javax.swing.JComboBox<>();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jTextFieldBasicSalary1 = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jTextFieldRiceSubsidy1 = new javax.swing.JTextField();
        jTextFieldSemiMonthlyRate1 = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        jTextFieldHourlyRate1 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jTextFieldPhoneAllowance1 = new javax.swing.JTextField();
        jTextFieldClothingAllowance1 = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jTextFieldSSSContribution1 = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jTextFieldPhilHealthContribution1 = new javax.swing.JTextField();
        jTextFieldPagIBIGContribution1 = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jTextFieldGrossPay1 = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        jTextFieldNetPay1 = new javax.swing.JTextField();
        jButtonCompute1 = new javax.swing.JButton();
        jLabel62 = new javax.swing.JLabel();
        jButtonBack1 = new javax.swing.JButton();
        jButtonFileLeave = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        button1 = new java.awt.Button();
        button2 = new java.awt.Button();
        button3 = new java.awt.Button();
        button4 = new java.awt.Button();
        jPanel2 = new javax.swing.JPanel();
        button5 = new java.awt.Button();
        button6 = new java.awt.Button();
        button7 = new java.awt.Button();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel4.setBackground(new java.awt.Color(0, 255, 204));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel4.setText("MotorPH");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 10, 170, 30));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("Payroll System");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 40, -1, 20));

        jPanel3.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 0, 1260, 70));

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel34.setText("Employee No. : ");
        jPanel3.add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, 90, 20));

        jTextFieldEmpNum1.setEditable(false);
        jTextFieldEmpNum1.addActionListener(this::jTextFieldEmpNum1ActionPerformed);
        jPanel3.add(jTextFieldEmpNum1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 100, 50, -1));

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel35.setText("Last Name : ");
        jPanel3.add(jLabel35, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 130, -1, 20));

        jTextFieldLastName1.setEditable(false);
        jPanel3.add(jTextFieldLastName1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 130, 140, -1));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel36.setText("First Name :");
        jPanel3.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 160, -1, 20));

        jTextFieldFirstName1.setEditable(false);
        jPanel3.add(jTextFieldFirstName1, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 160, 140, -1));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel37.setText("Address :");
        jPanel3.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 190, 60, 20));

        jPanel20.setBackground(new java.awt.Color(255, 255, 255));
        jPanel20.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel20.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel38.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel38.setText("Status :");
        jPanel20.add(jLabel38, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 90, 60, 20));

        jLabel39.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel39.setText("Position :");
        jPanel20.add(jLabel39, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 120, 90, 20));

        jTextFieldStatus1.setEditable(false);
        jPanel20.add(jTextFieldStatus1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 90, 140, -1));

        jTextFieldPosition1.setEditable(false);
        jPanel20.add(jTextFieldPosition1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 120, 140, -1));

        jTextFieldSupervisor1.setEditable(false);
        jPanel20.add(jTextFieldSupervisor1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 20, 140, -1));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel40.setText("SSS No. :");
        jPanel20.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 50, 60, 20));

        jTextFieldSSSNum1.setEditable(false);
        jPanel20.add(jTextFieldSSSNum1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 50, 140, -1));

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel41.setText("PhilHealth No. :");
        jPanel20.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 80, 90, 20));

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel42.setText("TIN :");
        jPanel20.add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 110, 60, 20));

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel43.setText("Pag-IBIG No. :");
        jPanel20.add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 140, 90, 20));

        jTextFieldPagIBIGNum1.setEditable(false);
        jPanel20.add(jTextFieldPagIBIGNum1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 140, 140, -1));

        jTextFieldTINNum1.setEditable(false);
        jPanel20.add(jTextFieldTINNum1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 110, 140, -1));

        jTextFieldPhilHealthNum1.setEditable(false);
        jPanel20.add(jTextFieldPhilHealthNum1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1030, 80, 140, -1));

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel44.setText("Immediate Supervisor :");
        jPanel20.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 20, 130, 20));

        jTextFieldBirthDay1.setEditable(false);
        jPanel20.add(jTextFieldBirthDay1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 60, 140, -1));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel45.setText("Birthday:");
        jPanel20.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 60, -1, -1));

        jTextFieldPhoneNum1.setEditable(false);
        jPanel20.add(jTextFieldPhoneNum1, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 30, 140, -1));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel46.setText("Phone Number :");
        jPanel20.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 30, 100, 20));

        jTextAreaAddress1.setEditable(false);
        jTextAreaAddress1.setColumns(20);
        jTextAreaAddress1.setRows(5);
        jScrollPane2.setViewportView(jTextAreaAddress1);

        jPanel20.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 110, 240, 40));

        jPanel3.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 1260, 180));

        jComboBoxMonth1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "January", "February", "March", "April", "May", "June", "July", "August", "September", "November", "December" }));
        jComboBoxMonth1.addActionListener(this::jComboBoxMonth1ActionPerformed);
        jPanel3.add(jComboBoxMonth1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 280, 100, 40));

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel47.setText("Select Month");
        jPanel3.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 280, 100, 20));

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel48.setText("Hourly Rate :");
        jPanel3.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 410, -1, 20));

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel49.setText("Gross Semi-Monthly Rate :");
        jPanel3.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 380, 160, 20));

        jTextFieldBasicSalary1.setEditable(false);
        jPanel3.add(jTextFieldBasicSalary1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 350, 270, -1));

        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel50.setText("Basic Salary :");
        jPanel3.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 350, 100, 20));

        jLabel51.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel51.setText("Rice Subsidy :");
        jPanel3.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 480, 90, 20));

        jTextFieldRiceSubsidy1.setEditable(false);
        jPanel3.add(jTextFieldRiceSubsidy1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 480, 270, -1));

        jTextFieldSemiMonthlyRate1.setEditable(false);
        jPanel3.add(jTextFieldSemiMonthlyRate1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 380, 270, -1));

        jLabel52.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel52.setText("Total");
        jPanel3.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 460, 100, 20));

        jTextFieldHourlyRate1.setEditable(false);
        jTextFieldHourlyRate1.addActionListener(this::jTextFieldHourlyRate1ActionPerformed);
        jPanel3.add(jTextFieldHourlyRate1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 410, 270, -1));

        jLabel53.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel53.setText("Earnings");
        jPanel3.add(jLabel53, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 320, 70, 20));

        jLabel54.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel54.setText("Phone Allowance :");
        jPanel3.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 510, 110, 20));

        jTextFieldPhoneAllowance1.setEditable(false);
        jPanel3.add(jTextFieldPhoneAllowance1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 510, 270, -1));

        jTextFieldClothingAllowance1.setEditable(false);
        jPanel3.add(jTextFieldClothingAllowance1, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 540, 270, -1));

        jLabel55.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel55.setText("Clothing Allowance :");
        jPanel3.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 540, 140, 20));

        jTextFieldSSSContribution1.setEditable(false);
        jTextFieldSSSContribution1.addActionListener(this::jTextFieldSSSContribution1ActionPerformed);
        jPanel3.add(jTextFieldSSSContribution1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 350, 270, -1));

        jLabel56.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel56.setText("SSS Contribution :");
        jPanel3.add(jLabel56, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 350, 110, 20));

        jLabel57.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel57.setText("PhilHealth Contribution :");
        jPanel3.add(jLabel57, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 380, 140, 20));

        jTextFieldPhilHealthContribution1.setEditable(false);
        jTextFieldPhilHealthContribution1.addActionListener(this::jTextFieldPhilHealthContribution1ActionPerformed);
        jPanel3.add(jTextFieldPhilHealthContribution1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 380, 270, -1));

        jTextFieldPagIBIGContribution1.setEditable(false);
        jTextFieldPagIBIGContribution1.addActionListener(this::jTextFieldPagIBIGContribution1ActionPerformed);
        jPanel3.add(jTextFieldPagIBIGContribution1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 410, 270, -1));

        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel58.setText("Pag-IBIG Contribution :");
        jPanel3.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 410, 150, 20));

        jLabel59.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel59.setText("Allowances");
        jPanel3.add(jLabel59, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 450, 100, 20));

        jLabel60.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel60.setText("Gross Pay :");
        jPanel3.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 490, 100, 20));

        jTextFieldGrossPay1.setEditable(false);
        jPanel3.add(jTextFieldGrossPay1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 490, 270, -1));

        jLabel61.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel61.setText("Net Pay :");
        jPanel3.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 520, 100, 20));

        jTextFieldNetPay1.setEditable(false);
        jPanel3.add(jTextFieldNetPay1, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 520, 270, -1));

        jButtonCompute1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonCompute1.setText("Compute");
        jButtonCompute1.addActionListener(this::jButtonCompute1ActionPerformed);
        jPanel3.add(jButtonCompute1, new org.netbeans.lib.awtextra.AbsoluteConstraints(920, 560, 170, 50));

        jLabel62.setFont(new java.awt.Font("Segoe UI", 3, 14)); // NOI18N
        jLabel62.setText("Deductions");
        jPanel3.add(jLabel62, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 320, 90, 20));

        jButtonBack1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonBack1.setText("Back");
        jButtonBack1.addActionListener(this::jButtonBack1ActionPerformed);
        jPanel3.add(jButtonBack1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 620, 100, 30));

        jButtonFileLeave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonFileLeave.setText("File Leave");
        jButtonFileLeave.addActionListener(this::jButtonFileLeaveActionPerformed);
        jPanel3.add(jButtonFileLeave, new org.netbeans.lib.awtextra.AbsoluteConstraints(1210, 620, -1, 30));

        button1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        button1.setLabel("Leave Request");
        button1.addActionListener(this::button1ActionPerformed);

        button2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        button2.setLabel("Manage Employee");
        button2.addActionListener(this::button2ActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(button2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(button1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(button2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel3.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 140, 140));

        button3.setBackground(new java.awt.Color(0, 204, 102));
        button3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        button3.setLabel("TimeIn");
        jPanel3.add(button3, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 280, 90, 30));

        button4.setBackground(new java.awt.Color(255, 102, 102));
        button4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        button4.setLabel("TimeOut");
        jPanel3.add(button4, new org.netbeans.lib.awtextra.AbsoluteConstraints(910, 280, 90, 30));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 140, -1));

        button5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        button5.setLabel("Manage Leave");
        jPanel3.add(button5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 140, 60));

        button6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        button6.setLabel("Pay Information");
        jPanel3.add(button6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 140, 60));

        button7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        button7.setLabel("Pay Information");
        jPanel3.add(button7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 140, 140, 60));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 12, Short.MAX_VALUE)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldEmpNum1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmpNum1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldEmpNum1ActionPerformed

    private void jComboBoxMonth1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxMonth1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxMonth1ActionPerformed

    private void jTextFieldHourlyRate1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldHourlyRate1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldHourlyRate1ActionPerformed

    private void jTextFieldSSSContribution1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSSSContribution1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSSSContribution1ActionPerformed

    private void jTextFieldPhilHealthContribution1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPhilHealthContribution1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPhilHealthContribution1ActionPerformed

    private void jTextFieldPagIBIGContribution1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPagIBIGContribution1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPagIBIGContribution1ActionPerformed
 
    /** Manage Leave button — ADMIN and HR only. */
    private void button5ActionPerformed(java.awt.event.ActionEvent evt) {
        if (currentUser == null ||
                !LEAVE_MGMT_ROLES.contains(currentUser.getRole())) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied.\n\n"
                    + "You do not have permission to access Leave Management.\n"
                    + "This feature is available to Admin and HR roles only.",
                    "Access Restricted",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        this.setVisible(false);
        new LeaveManagement(currentUser, this).setVisible(true);
    }
    
    private void jButtonCompute1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCompute1ActionPerformed
       if (currentEmployee == null) {
            JOptionPane.showMessageDialog(this,
                    "No employee record loaded.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String selectedMonth = (String) jComboBoxMonth1.getSelectedItem();
            int workingDays = getWorkingDaysForMonth(selectedMonth);
 
            if (workingDays <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Please select a valid month.", "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
 
            BigDecimal hourlyRate = currentEmployee.getHourlyRate();
            if (hourlyRate == null || hourlyRate.compareTo(BigDecimal.ZERO) == 0) {
                JOptionPane.showMessageDialog(this,
                        "Employee has no hourly rate on record.", "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
 
            BigDecimal calculatedBasicSalary = hourlyRate
                    .multiply(new BigDecimal(8))
                    .multiply(new BigDecimal(workingDays));
 
            Employee tempEmp = new Employee.Builder(
                    currentEmployee.getEmployeeId(),
                    currentEmployee.getLastName(),
                    currentEmployee.getFirstName(),
                    currentEmployee.getBirthday())
                    .withBasicSalary(calculatedBasicSalary)
                    .withRiceSubsidy(currentEmployee.getRiceSubsidy())
                    .withPhoneAllowance(currentEmployee.getPhoneAllowance())
                    .withClothingAllowance(currentEmployee.getClothingAllowance())
                    .withHourlyRate(hourlyRate)
                    .build();
 
            SalaryDeduction statutory = new SalaryDeduction();
            WithholdingTax  tax       = new WithholdingTax();
            statutory.calculate(tempEmp);
            tax.setTotalDeduction(statutory.getAmount());
            tax.calculate(tempEmp);
 
            double totalDeductions = statutory.getAmount() + tax.getAmount();
            TotalPay total = new TotalPay();
            total.calculatePayroll(tempEmp, totalDeductions);
 
            jTextFieldBasicSalary1.setText(String.format("%,.2f", calculatedBasicSalary));
            jTextFieldSSSContribution1.setText(String.format("%,.2f", statutory.getSSS()));
            jTextFieldPhilHealthContribution1.setText(String.format("%,.2f", statutory.getPhilDeduct()));
            jTextFieldPagIBIGContribution1.setText(String.format("%,.2f", statutory.getPagibigDeduct()));
            jTextFieldGrossPay1.setText(String.format("%,.2f", total.getGross()));
            jTextFieldNetPay1.setText(String.format("%,.2f", total.getNet()));
 
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error in calculation: " + e.getMessage(),
                    "Calculation Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonCompute1ActionPerformed

    private void jButtonBack1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBack1ActionPerformed
         if (parentFrame != null) {
            // Opened by an Admin from a parent dashboard — go back there
            parentFrame.setVisible(true);
            this.dispose();
        } else {
            // Opened directly from Login — log out and return to Login
            SessionManager.getInstance().logout();
            new ui.Login().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_jButtonBack1ActionPerformed

    private void jButtonFileLeaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFileLeaveActionPerformed
        if (currentEmployee == null) {
            JOptionPane.showMessageDialog(this,
                    "Employee record not loaded. Cannot file leave.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.setVisible(false);
        new LeaveRequestForm(currentUser, currentEmployee, this).setVisible(true);
    }
    
    private int getWorkingDaysForMonth(String month) {
        return switch (month) {
            case "February"                              -> 20;
            case "January", "June", "December"          -> 21;
            case "April", "May", "July",
                 "September", "November"                -> 22;
            case "March", "August", "October"           -> 23;
            default                                     -> 0;
        };
    }//GEN-LAST:event_jButtonFileLeaveActionPerformed

    private void button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button1ActionPerformed
        this.setVisible(false);
    new LeaveRequestForm(currentUser, currentEmployee, this).setVisible(true);
    }//GEN-LAST:event_button1ActionPerformed

    private void button2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button2ActionPerformed
        this.setVisible(false);
    new AdminPage(currentUser, this).setVisible(true);
    }//GEN-LAST:event_button2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
      try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException
                | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new ViewEmployee().setVisible(true));
    }
  

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button button1;
    private java.awt.Button button2;
    private java.awt.Button button3;
    private java.awt.Button button4;
    private java.awt.Button button5;
    private java.awt.Button button6;
    private java.awt.Button button7;
    private javax.swing.JButton jButtonBack1;
    private javax.swing.JButton jButtonCompute1;
    private javax.swing.JButton jButtonFileLeave;
    private javax.swing.JComboBox<String> jComboBoxMonth1;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextAreaAddress1;
    private javax.swing.JTextField jTextFieldBasicSalary1;
    private javax.swing.JTextField jTextFieldBirthDay1;
    private javax.swing.JTextField jTextFieldClothingAllowance1;
    private javax.swing.JTextField jTextFieldEmpNum1;
    private javax.swing.JTextField jTextFieldFirstName1;
    private javax.swing.JTextField jTextFieldGrossPay1;
    private javax.swing.JTextField jTextFieldHourlyRate1;
    private javax.swing.JTextField jTextFieldLastName1;
    private javax.swing.JTextField jTextFieldNetPay1;
    private javax.swing.JTextField jTextFieldPagIBIGContribution1;
    private javax.swing.JTextField jTextFieldPagIBIGNum1;
    private javax.swing.JTextField jTextFieldPhilHealthContribution1;
    private javax.swing.JTextField jTextFieldPhilHealthNum1;
    private javax.swing.JTextField jTextFieldPhoneAllowance1;
    private javax.swing.JTextField jTextFieldPhoneNum1;
    private javax.swing.JTextField jTextFieldPosition1;
    private javax.swing.JTextField jTextFieldRiceSubsidy1;
    private javax.swing.JTextField jTextFieldSSSContribution1;
    private javax.swing.JTextField jTextFieldSSSNum1;
    private javax.swing.JTextField jTextFieldSemiMonthlyRate1;
    private javax.swing.JTextField jTextFieldStatus1;
    private javax.swing.JTextField jTextFieldSupervisor1;
    private javax.swing.JTextField jTextFieldTINNum1;
    // End of variables declaration//GEN-END:variables

}
