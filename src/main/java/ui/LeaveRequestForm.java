package ui;
 
import DAO.LeaveDAO;
import Model.Employee;
import Model.UserAccount;
import com.toedter.calendar.JCalendar;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import javax.swing.JOptionPane;
 
public class LeaveRequestForm extends javax.swing.JFrame {
 
    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(LeaveRequestForm.class.getName());
 
    // ── injected state ────────────────────────────────────────────────────
    private final UserAccount        currentUser;
    private final Employee           currentEmployee;
    private final javax.swing.JFrame parentFrame;
 
    // ── DAO ───────────────────────────────────────────────────────────────
    private final LeaveDAO leaveDAO = new LeaveDAO();
 
    // ─────────────────────────────────────────────────────────────────────
    //  Primary constructor — called from ViewEmployee "File Leave" button
    // ─────────────────────────────────────────────────────────────────────
    public LeaveRequestForm(UserAccount user, Employee employee,
                         javax.swing.JFrame parent) {
    this.currentUser     = user;
    this.currentEmployee = employee;
    this.parentFrame     = parent;
    initComponents();
    setLocationRelativeTo(null);
    setTitle("MotorPH — Leave Request Form");
    
    // ADD THIS LINE - overrides the generated combo box values
    jComboBoxLeaveType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{
        "Emergency Leave", "Maternity Leave", "Paternity Leave", "Sick Leave", "Vacation Leave"
    }));
    
    populateEmployeeFields();
}
 
    /** No-arg constructor — kept for NetBeans Form Editor only. */
    public LeaveRequestForm() {
        this.currentUser     = null;
        this.currentEmployee = null;
        this.parentFrame     = null;
        initComponents();
    }
 
    // ─────────────────────────────────────────────────────────────────────
    //  Auto-fill employee info
    // ─────────────────────────────────────────────────────────────────────
    private void populateEmployeeFields() {
        if (currentEmployee == null) return;
        jTextFieldFirstName.setText(
                currentEmployee.getFirstName() != null
                ? currentEmployee.getFirstName() : "");
        jTextFieldLastName_.setText(
                currentEmployee.getLastName() != null
                ? currentEmployee.getLastName() : "");
        jTextFieldEmpNo.setText(
                currentEmployee.getEmployeeId() != null
                ? currentEmployee.getEmployeeId() : "");
        jTextFieldPosition.setText(
                currentEmployee.getPosition() != null
                ? currentEmployee.getPosition() : "");
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldFirstName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldLastName_ = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldPosition = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButtonSubmitRequest = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jComboBoxLeaveType = new javax.swing.JComboBox<>();
        jTextFieldEmpNo = new javax.swing.JTextField();
        jButtonBack = new javax.swing.JButton();
        jCalendarFrom = new com.toedter.calendar.JCalendar();
        jCalendarTo = new com.toedter.calendar.JCalendar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(0, 255, 204));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Leave Request Form");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 10, -1, -1));

        jPanel2.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 500, 60));

        jLabel2.setBackground(new java.awt.Color(0, 0, 0));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Employee #");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 80, 20));

        jTextFieldFirstName.setEditable(false);
        jTextFieldFirstName.addActionListener(this::jTextFieldFirstNameActionPerformed);
        jPanel2.add(jTextFieldFirstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 80, 270, -1));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Last Name");
        jPanel2.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 80, 20));

        jTextFieldLastName_.setEditable(false);
        jPanel2.add(jTextFieldLastName_, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 110, 270, -1));

        jLabel4.setBackground(new java.awt.Color(0, 0, 0));
        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("First Name");
        jPanel2.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 80, 80, 20));

        jTextFieldPosition.setEditable(false);
        jTextFieldPosition.addActionListener(this::jTextFieldPositionActionPerformed);
        jPanel2.add(jTextFieldPosition, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 170, 270, -1));

        jLabel5.setBackground(new java.awt.Color(0, 0, 0));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Position");
        jPanel2.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 170, 80, 20));

        jLabel6.setBackground(new java.awt.Color(0, 0, 0));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Leave Type");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 200, 80, 20));

        jLabel8.setBackground(new java.awt.Color(0, 0, 0));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Date of Return");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 230, -1, 20));

        jLabel10.setBackground(new java.awt.Color(0, 0, 0));
        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Start of Leave");
        jPanel2.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 230, 80, 20));

        jButtonSubmitRequest.setBackground(new java.awt.Color(0, 255, 204));
        jButtonSubmitRequest.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonSubmitRequest.setText("Submit Request");
        jButtonSubmitRequest.addActionListener(this::jButtonSubmitRequestActionPerformed);
        jPanel2.add(jButtonSubmitRequest, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 410, 120, 30));

        jLabel11.setBackground(new java.awt.Color(0, 0, 0));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Date");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 230, 60, 20));

        jComboBoxLeaveType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Vacation", "Sick" }));
        jPanel2.add(jComboBoxLeaveType, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 270, -1));

        jTextFieldEmpNo.setEditable(false);
        jTextFieldEmpNo.addActionListener(this::jTextFieldEmpNoActionPerformed);
        jPanel2.add(jTextFieldEmpNo, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 140, 270, -1));

        jButtonBack.setBackground(new java.awt.Color(0, 255, 204));
        jButtonBack.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButtonBack.setText("Back");
        jButtonBack.addActionListener(this::jButtonBackActionPerformed);
        jPanel2.add(jButtonBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 460, 70, 30));
        jPanel2.add(jCalendarFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 250, 180, 110));
        jPanel2.add(jCalendarTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 250, 180, 110));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 500, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldFirstNameActionPerformed

    private void jTextFieldPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPositionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPositionActionPerformed

    private void jButtonSubmitRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitRequestActionPerformed
         Date fromDate = jCalendarFrom.getDate();
        Date toDate   = jCalendarTo.getDate();
 
        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select both Start of Leave and Date of Return.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        if (toDate.before(fromDate)) {
            JOptionPane.showMessageDialog(this,
                    "Date of Return cannot be before Start of Leave.",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        // Convert to ISO yyyy-MM-dd for the DB
        SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd");
 
        try {
            String employeeName = currentEmployee.getFirstName() + " " + currentEmployee.getLastName();
leaveDAO.addLeaveRequest(
        currentEmployee.getEmployeeId(),
        employeeName,
        (String) jComboBoxLeaveType.getSelectedItem(),
        iso.format(fromDate),
        iso.format(toDate),
        ""
            );
            JOptionPane.showMessageDialog(this,
                    "Leave request submitted successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            if (parentFrame != null) parentFrame.setVisible(true);
            this.dispose();
 
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving leave request", e);
            JOptionPane.showMessageDialog(this,
                    "Error saving request:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButtonSubmitRequestActionPerformed

    private void jTextFieldEmpNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldEmpNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldEmpNoActionPerformed

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        if (parentFrame != null) {
            parentFrame.setVisible(true); // Show EmployeePage again
        }
        this.dispose();
    }//GEN-LAST:event_jButtonBackActionPerformed

    /**
     * @param args the command line arguments
     */
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonSubmitRequest;
    private com.toedter.calendar.JCalendar jCalendarFrom;
    private com.toedter.calendar.JCalendar jCalendarTo;
    private javax.swing.JComboBox<String> jComboBoxLeaveType;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField jTextFieldEmpNo;
    private javax.swing.JTextField jTextFieldFirstName;
    private javax.swing.JTextField jTextFieldLastName_;
    private javax.swing.JTextField jTextFieldPosition;
    // End of variables declaration//GEN-END:variables
}
