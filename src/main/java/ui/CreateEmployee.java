package ui;

import DAO.EmployeeDAO;
import Model.Employee;
import Model.Role;
import Model.UserAccount;
import Utility.Validator;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class CreateEmployee extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(CreateEmployee.class.getName());

    private final UserAccount        currentUser;
    private final javax.swing.JFrame parentFrame;
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    // ─────────────────────────────────────────────────────────────────────
    //  Constructors
    // ─────────────────────────────────────────────────────────────────────

    public CreateEmployee(UserAccount user, javax.swing.JFrame parent) {
        this.currentUser = user;
        this.parentFrame = parent;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("MotorPH — Create Employee");
        jComboBoxLeaveType_placeholder();
        autoGenerateEmployeeId();
        applyInputFilters();
    }

    public CreateEmployee(UserAccount user, javax.swing.JFrame parent, Employee emp) {
        this.currentUser = user;
        this.parentFrame = parent;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("MotorPH — Edit Employee");
        applyInputFilters();
        populateFields(emp);
    }

    public CreateEmployee() {
        this.currentUser = null;
        this.parentFrame = null;
        initComponents();
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Auto-generate Employee ID
    // ─────────────────────────────────────────────────────────────────────

    private void autoGenerateEmployeeId() {
        try {
        String sql = "SELECT MAX(CAST(employee_id AS UNSIGNED)) FROM employee";
        try (java.sql.Connection conn = DAO.Database.getInstance().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int lastId = rs.getInt(1);
                jTextFieldEmpNum1.setText(String.valueOf(lastId + 1));
            } else {
                jTextFieldEmpNum1.setText("10034");
            }
        }
    } catch (SQLException ex) {
        logger.log(Level.WARNING, "Could not fetch last employee ID", ex);
        jTextFieldEmpNum1.setText("10034"); // fallback
    }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Populate fields for Edit mode
    // ─────────────────────────────────────────────────────────────────────

    private void populateFields(Employee emp) {
        jTextFieldEmpNum1.setText(emp.getEmployeeId());
        jTextFieldEmpNum1.setEditable(false); // Don't allow ID change in edit mode
        jTextFieldFirstName.setText(emp.getFirstName());
        jTextFieldLastName.setText(emp.getLastName());
        jTextFieldAddress.setText(emp.getAddress());
        jTextFieldPhoneNum.setText(emp.getPhoneNumber() != null
                ? emp.getPhoneNumber().replaceAll("[^0-9]", "") : "");
        jTextFieldSSSNum.setText(emp.getSssNumber() != null
                ? emp.getSssNumber().replaceAll("[^0-9]", "") : "");
        jTextFieldPhilHealthNum.setText(emp.getPhilhealthNumber() != null
                ? emp.getPhilhealthNumber().replaceAll("[^0-9]", "") : "");
        jTextFieldTINNum.setText(emp.getTinNumber() != null
                ? emp.getTinNumber().replaceAll("[^0-9]", "") : "");
        jTextFieldPagibigNum.setText(emp.getPagIbigNumber() != null
                ? emp.getPagIbigNumber().replaceAll("[^0-9]", "") : "");
        jTextFieldBasicSalary.setText(emp.getBasicSalary() != null
                ? emp.getBasicSalary().toPlainString() : "");
        jTextFieldRiceSubsidy.setText(emp.getRiceSubsidy() != null
                ? emp.getRiceSubsidy().toPlainString() : "");
        jTextFieldPhoneAllowance.setText(emp.getPhoneAllowance() != null
                ? emp.getPhoneAllowance().toPlainString() : "");
        jTextFieldClothingAllowance.setText(emp.getClothingAllowance() != null
                ? emp.getClothingAllowance().toPlainString() : "");
        jTextFieldGrossSemiMonthly.setText(emp.getGrossSemiMonthlyRate() != null
                ? emp.getGrossSemiMonthlyRate().toPlainString() : "");
        jTextFieldHourlyRate.setText(emp.getHourlyRate() != null
                ? emp.getHourlyRate().toPlainString() : "");

        if (emp.getStatus() != null) {
            String statusStr = emp.getStatus().name().substring(0, 1).toUpperCase()
                    + emp.getStatus().name().substring(1).toLowerCase();
            jComboBoxStatus.setSelectedItem(statusStr);
        }
        if (emp.getPosition() != null) {
            jComboBoxPosition.setSelectedItem(emp.getPosition());
        }
        if (emp.getImmediateSupervisor() != null && !emp.getImmediateSupervisor().isEmpty()) {
            jComboBoxSupervisor.setSelectedItem(emp.getImmediateSupervisor());
        }
        if (emp.getBirthday() != null) {
            java.util.Date date = java.util.Date.from(
                emp.getBirthday().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            jCalendarBirthday.setDate(date);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Input Filters
    // ─────────────────────────────────────────────────────────────────────

    private void applyInputFilters() {
        addDigitOnlyFilter(jTextFieldPhoneNum, 11);
        addDigitOnlyFilter(jTextFieldSSSNum, 10);
        addDigitOnlyFilter(jTextFieldPhilHealthNum, 12);
        addDigitOnlyFilter(jTextFieldTINNum, 12);
        addDigitOnlyFilter(jTextFieldPagibigNum, 12);
        addDecimalOnlyFilter(jTextFieldBasicSalary);
        addDecimalOnlyFilter(jTextFieldRiceSubsidy);
        addDecimalOnlyFilter(jTextFieldPhoneAllowance);
        addDecimalOnlyFilter(jTextFieldClothingAllowance);
        addDecimalOnlyFilter(jTextFieldGrossSemiMonthly);
        addDecimalOnlyFilter(jTextFieldHourlyRate);
    }

    private void addDigitOnlyFilter(javax.swing.JTextField field, int maxLength) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string,
                    AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtered = string.replaceAll("[^0-9]", "");
                if (filtered.isEmpty() && !string.isEmpty()) {
                    showWarn("Only numbers are allowed in this field.");
                    return;
                }
                if (fb.getDocument().getLength() + filtered.length() > maxLength) {
                    showWarn("Maximum " + maxLength + " digits allowed.");
                    return;
                }
                super.insertString(fb, offset, filtered, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String string,
                    AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtered = string.replaceAll("[^0-9]", "");
                if (filtered.isEmpty() && !string.isEmpty()) {
                    showWarn("Only numbers are allowed in this field.");
                    return;
                }
                if (fb.getDocument().getLength() - length + filtered.length() > maxLength) {
                    showWarn("Maximum " + maxLength + " digits allowed.");
                    return;
                }
                super.replace(fb, offset, length, filtered, attr);
            }
        });
    }

    private void addDecimalOnlyFilter(javax.swing.JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string,
                    AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtered = string.replaceAll("[^0-9.]", "");
                if (filtered.isEmpty() && !string.isEmpty()) {
                    showWarn("Only numbers and decimal point are allowed for peso amounts.");
                    return;
                }
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                if (filtered.contains(".") && current.contains(".")) {
                    showWarn("Only one decimal point is allowed.");
                    return;
                }
                super.insertString(fb, offset, filtered, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String string,
                    AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtered = string.replaceAll("[^0-9.]", "");
                if (filtered.isEmpty() && !string.isEmpty()) {
                    showWarn("Only numbers and decimal point are allowed for peso amounts.");
                    return;
                }
                String current = fb.getDocument().getText(0, fb.getDocument().getLength());
                if (filtered.contains(".") && current.contains(".")) {
                    showWarn("Only one decimal point is allowed.");
                    return;
                }
                super.replace(fb, offset, length, filtered, attr);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Placeholder — not used but avoids compile errors
    // ─────────────────────────────────────────────────────────────────────

    private void jComboBoxLeaveType_placeholder() {
        // intentionally empty
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Validation
    // ─────────────────────────────────────────────────────────────────────

    private boolean validateAllFields() {

        String empId = jTextFieldEmpNum1.getText().trim();
        if (empId.isEmpty()) {
            showWarn("Employee Number is required.");
            jTextFieldEmpNum1.requestFocus();
            return false;
        }

        String firstName = jTextFieldFirstName.getText().trim();
        if (firstName.isEmpty()) {
            showWarn("First Name is required.");
            jTextFieldFirstName.requestFocus();
            return false;
        }
        if (!firstName.matches("^[a-zA-Z\\s.'-]+$")) {
            showWarn("First Name must contain letters only.");
            jTextFieldFirstName.requestFocus();
            return false;
        }

        String lastName = jTextFieldLastName.getText().trim();
        if (lastName.isEmpty()) {
            showWarn("Last Name is required.");
            jTextFieldLastName.requestFocus();
            return false;
        }
        if (!lastName.matches("^[a-zA-Z\\s.'-]+$")) {
            showWarn("Last Name must contain letters only.");
            jTextFieldLastName.requestFocus();
            return false;
        }

        Date birthday = jCalendarBirthday.getDate();
        if (birthday == null) {
            showWarn("Birthday is required. Please select a date from the calendar.");
            return false;
        }
        LocalDate bday = birthday.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (bday.isAfter(LocalDate.now().minusYears(18))) {
            showWarn("Employee must be at least 18 years old.");
            return false;
        }

        String address = jTextFieldAddress.getText().trim();
        if (address.isEmpty()) {
            showWarn("Address is required.");
            jTextFieldAddress.requestFocus();
            return false;
        }

        String phone = jTextFieldPhoneNum.getText().trim();
        if (phone.length() != 11 || !phone.startsWith("09")) {
            showWarn("Invalid Phone Number.\nMust be 11 digits starting with 09 (e.g. 09171234567).");
            jTextFieldPhoneNum.requestFocus();
            return false;
        }

        String sss = jTextFieldSSSNum.getText().trim();
        if (sss.length() != 10) {
            showWarn("SSS Number must be exactly 10 digits.");
            jTextFieldSSSNum.requestFocus();
            return false;
        }

        String philhealth = jTextFieldPhilHealthNum.getText().trim();
        if (philhealth.length() != 12) {
            showWarn("PhilHealth Number must be exactly 12 digits.");
            jTextFieldPhilHealthNum.requestFocus();
            return false;
        }

        String tin = jTextFieldTINNum.getText().trim();
        if (tin.length() != 12) {
            showWarn("TIN Number must be exactly 12 digits.");
            jTextFieldTINNum.requestFocus();
            return false;
        }

        String pagibig = jTextFieldPagibigNum.getText().trim();
        if (pagibig.length() != 12) {
            showWarn("Pag-IBIG Number must be exactly 12 digits.");
            jTextFieldPagibigNum.requestFocus();
            return false;
        }

        if (!isPositiveDecimal(jTextFieldBasicSalary.getText())) {
            showWarn("Basic Salary must be a positive number (e.g. 25000.00).");
            jTextFieldBasicSalary.requestFocus();
            return false;
        }
        if (!isPositiveDecimal(jTextFieldRiceSubsidy.getText())) {
            showWarn("Rice Subsidy must be a positive number.");
            jTextFieldRiceSubsidy.requestFocus();
            return false;
        }
        if (!isPositiveDecimal(jTextFieldPhoneAllowance.getText())) {
            showWarn("Phone Allowance must be a positive number.");
            jTextFieldPhoneAllowance.requestFocus();
            return false;
        }
        if (!isPositiveDecimal(jTextFieldClothingAllowance.getText())) {
            showWarn("Clothing Allowance must be a positive number.");
            jTextFieldClothingAllowance.requestFocus();
            return false;
        }
        if (!isPositiveDecimal(jTextFieldGrossSemiMonthly.getText())) {
            showWarn("Gross Semi-Monthly Rate must be a positive number.");
            jTextFieldGrossSemiMonthly.requestFocus();
            return false;
        }
        if (!isPositiveDecimal(jTextFieldHourlyRate.getText())) {
            showWarn("Hourly Rate must be a positive number.");
            jTextFieldHourlyRate.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isPositiveDecimal(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try {
            return new BigDecimal(value.trim()).compareTo(BigDecimal.ZERO) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDuplicateEmployee(String empId) {
        try {
            return employeeDAO.findById(empId) != null;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Could not check duplicate", ex);
            return false;
        }
    }

    private void showWarn(String msg) {
        JOptionPane.showMessageDialog(this, msg,
                "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextFieldEmpNum1 = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jComboBoxPosition = new javax.swing.JComboBox<>();
        jComboBoxStatus = new javax.swing.JComboBox<>();
        jComboBoxSupervisor = new javax.swing.JComboBox<>();
        jPanel13 = new javax.swing.JPanel();
        jTextFieldPhilHealthNum = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jTextFieldSSSNum = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        jTextFieldTINNum = new javax.swing.JTextField();
        jTextFieldPagibigNum = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldPhoneNum = new javax.swing.JTextField();
        jTextFieldLastName = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jTextFieldAddress = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jTextFieldFirstName = new javax.swing.JTextField();
        jCalendarBirthday = new com.toedter.calendar.JCalendar();
        jButtonBack = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldPhoneAllowance = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jTextFieldClothingAllowance = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextFieldRiceSubsidy = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jTextFieldHourlyRate = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextFieldBasicSalary = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTextFieldGrossSemiMonthly = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jButtonSave = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(0, 255, 204));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 3, 36)); // NOI18N
        jLabel1.setText("MotorPH");
        jPanel3.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, -10, 160, 80));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Payroll System");
        jPanel3.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 50, -1, -1));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Employee Number");
        jPanel3.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 110, 20));

        jTextFieldEmpNum1.setEditable(false);
        jTextFieldEmpNum1.addActionListener(this::jTextFieldEmpNum1ActionPerformed);
        jPanel3.add(jTextFieldEmpNum1, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 90, 60, -1));

        jPanel9.setBackground(new java.awt.Color(0, 255, 204));
        jPanel9.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel32.setText("Employment Details");
        jPanel9.add(jLabel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel36.setText("Position");
        jPanel9.add(jLabel36, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 70, 20));

        jLabel37.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel37.setText("Status");
        jPanel9.add(jLabel37, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, 70, 20));

        jLabel44.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel44.setText("Supervisor");
        jPanel9.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 150, 100, 20));

        jComboBoxPosition.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Chief Executive Officer", "Chief Operating Officer", "Chief Finance Officer", "Chief Marketing Officer", "Account Manager", "IT Operations and Systems", "HR Manager", "Accounting Head", "Sales & Marketing", "Supply Chain and Logistics", "Customer Service and Relations", "Payroll Manager", "HR Team Leader", "Account Team Leader", "Payroll Team Leader", "Account Rank and File", "Payroll Rank and File", "HR Rank and File" }));
        jComboBoxPosition.addActionListener(this::jComboBoxPositionActionPerformed);
        jPanel9.add(jComboBoxPosition, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 200, -1));

        jComboBoxStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Regular", "Probationary", " " }));
        jPanel9.add(jComboBoxStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 110, 200, -1));

        jComboBoxSupervisor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N/A", "Garcia Manuel III", "Lim Antonio", "Aquino Bianca Sofia", "Reyes Isabella", "Alvaro Roderick", "Villanueva Andrea Mae", "Romualdez Fredrick", "Salcedo Anthony", "De Leon Selena", "Mata Christian", "San Jose Brad" }));
        jPanel9.add(jComboBoxSupervisor, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 150, 200, -1));

        jPanel13.setBackground(new java.awt.Color(0, 255, 204));
        jPanel13.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel13.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextFieldPhilHealthNum.addActionListener(this::jTextFieldPhilHealthNumActionPerformed);
        jPanel13.add(jTextFieldPhilHealthNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 90, 160, -1));

        jLabel45.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel45.setText("Government Identifiers");
        jPanel13.add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel46.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel46.setText("PhilHealth");
        jPanel13.add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 100, 20));

        jLabel49.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel49.setText("SSS Number");
        jPanel13.add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 90, 20));
        jPanel13.add(jTextFieldSSSNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 50, 160, -1));

        jLabel50.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel50.setText("TIN Number");
        jPanel13.add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, 70, 20));
        jPanel13.add(jTextFieldTINNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 130, 160, -1));
        jPanel13.add(jTextFieldPagibigNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 170, 160, -1));

        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel58.setText("Pag-IBIG Number");
        jPanel13.add(jLabel58, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, 100, 20));

        jPanel2.setBackground(new java.awt.Color(0, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Address");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 220, 50, 20));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Phone Number");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 250, 90, 20));
        jPanel2.add(jTextFieldPhoneNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 250, 210, -1));
        jPanel2.add(jTextFieldLastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 210, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel10.setText("Employee Information");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 200, 20));

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel17.setText("Last Name");
        jPanel2.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 70, 60, 20));
        jPanel2.add(jTextFieldAddress, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 220, 210, 20));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel13.setText("Birth Day");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, 80, 20));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel23.setText("First Name");
        jPanel2.add(jLabel23, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 70, 20));

        jTextFieldFirstName.addActionListener(this::jTextFieldFirstNameActionPerformed);
        jPanel2.add(jTextFieldFirstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, 210, -1));

        jCalendarBirthday.setBackground(new java.awt.Color(0, 255, 204));
        jCalendarBirthday.setForeground(new java.awt.Color(0, 255, 204));
        jCalendarBirthday.setSundayForeground(new java.awt.Color(255, 51, 51));
        jPanel2.add(jCalendarBirthday, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 210, 110));

        jButtonBack.setText("Back");
        jButtonBack.addActionListener(this::jButtonBackActionPerformed);
        jPanel2.add(jButtonBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 300, 120, 40));

        jPanel4.setBackground(new java.awt.Color(0, 255, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel9.setText("Salary Details");
        jPanel4.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 200, 20));
        jPanel4.add(jTextFieldPhoneAllowance, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 90, 110, -1));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel18.setText("Phone Allowance");
        jPanel4.add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, -1, 20));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel19.setText("Clothing Allowance");
        jPanel4.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 150, -1, 20));
        jPanel4.add(jTextFieldClothingAllowance, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 150, 110, -1));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel20.setText("Rice Subsidy");
        jPanel4.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 120, -1, 20));
        jPanel4.add(jTextFieldRiceSubsidy, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 120, 110, -1));

        jLabel21.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel21.setText("Hourly Rate");
        jPanel4.add(jLabel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 210, -1, 20));
        jPanel4.add(jTextFieldHourlyRate, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 210, 110, -1));

        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel22.setText("Gross Semi-Monthly");
        jPanel4.add(jLabel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 120, 20));
        jPanel4.add(jTextFieldBasicSalary, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 60, 110, -1));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel24.setText("Basic Salary");
        jPanel4.add(jLabel24, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 60, 70, 20));
        jPanel4.add(jTextFieldGrossSemiMonthly, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 180, 110, -1));

        jPanel6.setBackground(new java.awt.Color(0, 255, 204));

        jButtonSave.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonSave.setText("Save");
        jButtonSave.addActionListener(this::jButtonSaveActionPerformed);

        jButtonCancel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonCancel.setText("Cancel");
        jButtonCancel.addActionListener(this::jButtonCancelActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jButtonSave, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 181, Short.MAX_VALUE)
                .addComponent(jButtonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonSave)
                    .addComponent(jButtonCancel))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 1020, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 0, 0)
                            .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 540, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldEmpNum1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmpNum1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldEmpNum1ActionPerformed

    private void jComboBoxPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPositionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxPositionActionPerformed

    private void jTextFieldFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldFirstNameActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        
        // ── Step 1: Validate all fields ───────────────────────────────
         if (!validateAllFields()) return;

        String empId = jTextFieldEmpNum1.getText().trim();

        if (isDuplicateEmployee(empId)) {
            JOptionPane.showMessageDialog(this,
                    "Employee ID \"" + empId + "\" already exists in the database.\n"
                    + "Please use a different Employee Number.",
                    "Duplicate Employee", JOptionPane.ERROR_MESSAGE);
            jTextFieldEmpNum1.requestFocus();
            return;
        }

        try {
            String status     = (String) jComboBoxStatus.getSelectedItem();
            String position   = (String) jComboBoxPosition.getSelectedItem();
            String supervisor = (String) jComboBoxSupervisor.getSelectedItem();

            Employee.EmploymentStatus statusEnum;
            try {
                statusEnum = Employee.EmploymentStatus.valueOf(
                        status.toUpperCase().trim());
            } catch (IllegalArgumentException ignore) {
                statusEnum = Employee.EmploymentStatus.PROBATIONARY;
            }

            LocalDate birthdayDate = jCalendarBirthday.getDate()
                    .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // Format government IDs with dashes before saving
            String phone      = jTextFieldPhoneNum.getText().trim();
            String sss        = jTextFieldSSSNum.getText().trim();
            String philhealth = jTextFieldPhilHealthNum.getText().trim();
            String tin        = jTextFieldTINNum.getText().trim();
            String pagibig    = jTextFieldPagibigNum.getText().trim();

            // SSS: XX-XXXXXXX-X
            String sssFormatted = sss.substring(0, 2) + "-"
                    + sss.substring(2, 9) + "-" + sss.substring(9);
            // TIN: XXX-XXX-XXX-XXX
            String tinFormatted = tin.substring(0, 3) + "-" + tin.substring(3, 6)
                    + "-" + tin.substring(6, 9) + "-" + tin.substring(9);
            // Phone: 09XX-XXX-XXXX
            String phoneFormatted = phone.substring(0, 4) + "-"
                    + phone.substring(4, 7) + "-" + phone.substring(7);

            Employee emp = new Employee.Builder(
                    empId,
                    jTextFieldLastName.getText().trim(),
                    jTextFieldFirstName.getText().trim(),
                    birthdayDate)
                    .withAddress(jTextFieldAddress.getText().trim())
                    .withPhoneNumber(phoneFormatted)
                    .withSssNumber(sssFormatted)
                    .withPhilhealthNumber(philhealth)
                    .withTinNumber(tinFormatted)
                    .withPagIbigNumber(pagibig)
                    .withStatus(statusEnum)
                    .withPosition(position)
                    .withImmediateSupervisor("N/A".equals(supervisor) ? "" : supervisor)
                    .withBasicSalary(new BigDecimal(jTextFieldBasicSalary.getText().trim()))
                    .withRiceSubsidy(new BigDecimal(jTextFieldRiceSubsidy.getText().trim()))
                    .withPhoneAllowance(new BigDecimal(jTextFieldPhoneAllowance.getText().trim()))
                    .withClothingAllowance(new BigDecimal(jTextFieldClothingAllowance.getText().trim()))
                    .withGrossSemiMonthlyRate(new BigDecimal(jTextFieldGrossSemiMonthly.getText().trim()))
                    .withHourlyRate(new BigDecimal(jTextFieldHourlyRate.getText().trim()))
                    .build();

            boolean saved = employeeDAO.create(emp);
            if (saved) {
                JOptionPane.showMessageDialog(this,
                        "Employee \"" + emp.getFirstName() + " "
                        + emp.getLastName() + "\" created successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                if (parentFrame != null) parentFrame.setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Employee was not saved. Please try again.",
                        "Save Failed", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Database error creating employee", ex);
            JOptionPane.showMessageDialog(this,
                    "Database error:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Unexpected error", ex);
            JOptionPane.showMessageDialog(this,
                    "Unexpected error:\n" + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        // TODO add your handling code here:
        if (parentFrame != null) parentFrame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jTextFieldPhilHealthNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPhilHealthNumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPhilHealthNumActionPerformed

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        // TODO add your handling code here:
        if (parentFrame != null) parentFrame.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonBackActionPerformed

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
            logger.log(Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> new CreateEmployee().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonSave;
    private com.toedter.calendar.JCalendar jCalendarBirthday;
    private javax.swing.JComboBox<String> jComboBoxPosition;
    private javax.swing.JComboBox<String> jComboBoxStatus;
    private javax.swing.JComboBox<String> jComboBoxSupervisor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JTextField jTextFieldAddress;
    private javax.swing.JTextField jTextFieldBasicSalary;
    private javax.swing.JTextField jTextFieldClothingAllowance;
    private javax.swing.JTextField jTextFieldEmpNum1;
    private javax.swing.JTextField jTextFieldFirstName;
    private javax.swing.JTextField jTextFieldGrossSemiMonthly;
    private javax.swing.JTextField jTextFieldHourlyRate;
    private javax.swing.JTextField jTextFieldLastName;
    private javax.swing.JTextField jTextFieldPagibigNum;
    private javax.swing.JTextField jTextFieldPhilHealthNum;
    private javax.swing.JTextField jTextFieldPhoneAllowance;
    private javax.swing.JTextField jTextFieldPhoneNum;
    private javax.swing.JTextField jTextFieldRiceSubsidy;
    private javax.swing.JTextField jTextFieldSSSNum;
    private javax.swing.JTextField jTextFieldTINNum;
    // End of variables declaration//GEN-END:variables
}
