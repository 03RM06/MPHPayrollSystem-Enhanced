package ui;
 
import DAO.EmployeeDAO;
import Model.Employee;
import Model.Role;
import Model.UserAccount;
import Services.SessionManager;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
 
public class AdminPage extends javax.swing.JFrame {
 
    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(AdminPage.class.getName());
 
    private final UserAccount        currentUser;
    private final javax.swing.JFrame parentFrame;
 
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
 
    private DefaultTableModel tableModel;
 
    public AdminPage(UserAccount user, javax.swing.JFrame parent) {
        this.currentUser = user;
        this.parentFrame = parent;
        initComponents();
        
        // Fix Delete button visibility (white text needs colored background)
        jButtonDeleteRec.setBackground(new java.awt.Color(200, 50, 50));
        jButtonDeleteRec.setForeground(new java.awt.Color(255, 255, 255));
        jButtonDeleteRec.setOpaque(true);
        jButtonDeleteRec.setBorderPainted(false);
        
        setLocationRelativeTo(null);
        setTitle("MotorPH — Employee Management");
        setupTable();
        loadEmployeeData();       // load data first
        configureButtonsByRole(); // then apply role-based button states LAST
        jTableDataBase.revalidate();
        jTableDataBase.repaint();
    }
 
    /** No-arg constructor — NetBeans Form Editor only. */
    public AdminPage() {
        this.currentUser = null;
        this.parentFrame = null;
        initComponents();
        setupTable();
    }
 
    private void configureButtonsByRole() {
        if (currentUser == null) return;
        Role role = currentUser.getRole();
        boolean isAdminOrHR = (role == Role.ADMIN || role == Role.HR);
        boolean isAdminOnly  = (role == Role.ADMIN);
 
        // Always enabled based on role — no row selection needed
        jButtonCreateRec.setEnabled(isAdminOrHR);
        jButtonRefresh.setEnabled(true);
 
        // Hidden/shown by role
        jButtonDeleteRec.setVisible(isAdminOnly);
 
        // These require a row selection — start disabled
        jButtonDeleteRec.setEnabled(false);
        jButtonEditRec.setEnabled(false);
        jButtonViewEmployeeDetails.setEnabled(false);
        jButtonClear.setEnabled(false);
    }
 
    private void setupTable() {
        tableModel = new DefaultTableModel(
            new String[]{
                "Emp ID", "Last Name", "First Name", "Birthday",
                "Address", "Phone No.", "SSS No.", "PhilHealth No.",
                "TIN No.", "Pag-IBIG No.", "Status", "Position",
                "Supervisor", "Basic Salary", "Semi-Monthly", "Hourly Rate"
            }, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        jTableDataBase.setModel(tableModel);
        jTableDataBase.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTableDataBase.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTableDataBase.getTableHeader().setFont(
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        jTableDataBase.getSelectionModel().addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            int row = jTableDataBase.getSelectedRow();
            if (row < 0) return;

            jTextFieldEmpNum.setText(safeCell(row, 0));
            jTextFieldLastName.setText(safeCell(row, 1));
            jTextFieldFirstName.setText(safeCell(row, 2));
            jTextAreaAddress.setText(safeCell(row, 4));
            jTextFieldPhoneNum.setText(safeCell(row, 5));
            jTextFieldSupervisor.setText(safeCell(row, 12));
            jTextFieldPosition.setText(safeCell(row, 11));
            jTextFieldStatus.setText(safeCell(row, 10));

            jButtonClear.setEnabled(true);
            jButtonViewEmployeeDetails.setEnabled(true);

            boolean canEdit = currentUser != null &&
                    (currentUser.getRole() == Role.ADMIN ||
                     currentUser.getRole() == Role.HR);
            jButtonEditRec.setEnabled(canEdit);

            boolean canDelete = currentUser != null &&
                    currentUser.getRole() == Role.ADMIN;
            jButtonDeleteRec.setEnabled(canDelete);
        }
    });

    }
 
    private void loadEmployeeData() {
        tableModel.setRowCount(0);
        clearDetailFields();
        try {
            List<Employee> employees = employeeDAO.findAll();
            for (Employee e : employees) {
                tableModel.addRow(new Object[]{
                    safe(e.getEmployeeId()),
                    safe(e.getLastName()),
                    safe(e.getFirstName()),
                    safe(e.getFormattedBirthday()),
                    safe(e.getAddress()),
                    safe(e.getPhoneNumber()),
                    safe(e.getSssNumber()),
                    safe(e.getPhilhealthNumber()),
                    safe(e.getTinNumber()),
                    safe(e.getPagIbigNumber()),
                    e.getStatus() != null ? e.getStatus().name() : "",
                    safe(e.getPosition()),
                    safe(e.getImmediateSupervisor()),
                    fmt(e.getBasicSalary()),
                    fmt(e.getGrossSemiMonthlyRate()),
                    fmt(e.getHourlyRate())
                });
            }
            // Re-apply role config after load to restore correct button states
            configureButtonsByRole();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to load employees", ex);
            JOptionPane.showMessageDialog(this,
                    "Error loading employees:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void clearDetailFields() {
        jTextFieldEmpNum.setText("");
        jTextFieldLastName.setText("");
        jTextFieldFirstName.setText("");
        jTextAreaAddress.setText("");
        jTextFieldPhoneNum.setText("");
        jTextFieldSupervisor.setText("");
        jTextFieldPosition.setText("");
        jTextFieldStatus.setText("");
 
        // Only disable row-dependent buttons — do NOT touch Create or Refresh
        jButtonEditRec.setEnabled(false);
        jButtonDeleteRec.setEnabled(false);
        jButtonViewEmployeeDetails.setEnabled(false);
        jButtonClear.setEnabled(false);
        jButtonEditRec.setEnabled(false);
    }
 
    private String safe(String v) { return v != null ? v : ""; }
    private String fmt(BigDecimal v) {
        return v != null ? String.format("%,.2f", v) : "0.00";
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonCreateRec = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jButtonRefresh = new javax.swing.JButton();
        jButtonEditRec = new javax.swing.JButton();
        jButtonDeleteRec = new javax.swing.JButton();
        jTextFieldPosition = new javax.swing.JTextField();
        jTextFieldEmpNum = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButtonViewAllEmp = new javax.swing.JButton();
        jTextFieldLastName = new javax.swing.JTextField();
        jTextFieldFirstName = new javax.swing.JTextField();
        jTextFieldPhoneNum = new javax.swing.JTextField();
        jTextFieldSupervisor = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableDataBase = new javax.swing.JTable();
        jButtonViewEmployeeDetails = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jTextFieldStatus = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaAddress = new javax.swing.JTextArea();
        jButtonLogout = new javax.swing.JButton();
        jButtonLeaveManagement = new javax.swing.JButton();
        jButtonLogout1 = new javax.swing.JButton();
        jButtonLogout2 = new javax.swing.JButton();
        jButtonLogout3 = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setFocusTraversalPolicyProvider(true);
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButtonCreateRec.setText("Create Employee");
        jButtonCreateRec.addActionListener(this::jButtonCreateRecActionPerformed);
        jPanel1.add(jButtonCreateRec, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 520, 130, 30));

        jButtonClear.setText("Clear Employee");
        jButtonClear.addActionListener(this::jButtonClearActionPerformed);
        jPanel1.add(jButtonClear, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 480, 130, 30));

        jButtonRefresh.setText("Refresh");
        jButtonRefresh.addActionListener(this::jButtonRefreshActionPerformed);
        jPanel1.add(jButtonRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 480, 130, 30));

        jButtonEditRec.setText("Edit Employee");
        jButtonEditRec.addActionListener(this::jButtonEditRecActionPerformed);
        jPanel1.add(jButtonEditRec, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 520, 130, 30));

        jButtonDeleteRec.setForeground(new java.awt.Color(255, 255, 255));
        jButtonDeleteRec.setText("Delete Employee");
        jButtonDeleteRec.addActionListener(this::jButtonDeleteRecActionPerformed);
        jPanel1.add(jButtonDeleteRec, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 520, 130, 30));

        jTextFieldPosition.setEditable(false);
        jTextFieldPosition.addActionListener(this::jTextFieldPositionActionPerformed);
        jPanel1.add(jTextFieldPosition, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 380, 250, 30));

        jTextFieldEmpNum.setEditable(false);
        jPanel1.add(jTextFieldEmpNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 130, 70, 30));

        jLabel6.setText("Employee Number: ");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, -1, 20));

        jLabel7.setText("Last Name:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 180, -1, 20));

        jLabel8.setText("First Name:");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 220, -1, 20));

        jLabel9.setText("Phone No.:");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 260, -1, 20));

        jLabel10.setText("Address:");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 310, -1, 20));

        jLabel11.setText("Supervisor:");
        jPanel1.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 350, 70, 20));

        jLabel5.setText("Position:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 390, 50, 20));

        jButtonViewAllEmp.setText("View All Employees");
        jButtonViewAllEmp.addActionListener((evt) -> {
            jButtonViewAllEmpActionPerformed(evt);
            jButtonViewAllEmponViewAllEmployeesClick(evt);
        });
        jPanel1.add(jButtonViewAllEmp, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 80, 220, 40));

        jTextFieldLastName.setEditable(false);
        jTextFieldLastName.addActionListener(this::jTextFieldLastNameActionPerformed);
        jPanel1.add(jTextFieldLastName, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 170, 250, 30));

        jTextFieldFirstName.setEditable(false);
        jTextFieldFirstName.addActionListener(this::jTextFieldFirstNameActionPerformed);
        jPanel1.add(jTextFieldFirstName, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 210, 250, 30));

        jTextFieldPhoneNum.setEditable(false);
        jTextFieldPhoneNum.addActionListener(this::jTextFieldPhoneNumActionPerformed);
        jPanel1.add(jTextFieldPhoneNum, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 250, 250, 30));

        jTextFieldSupervisor.setEditable(false);
        jTextFieldSupervisor.addActionListener(this::jTextFieldSupervisorActionPerformed);
        jPanel1.add(jTextFieldSupervisor, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 340, 250, 30));

        jTableDataBase.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jTableDataBase.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jTableDataBase.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableDataBaseMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableDataBase);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 80, 820, 530));

        jButtonViewEmployeeDetails.setText("View Employee Details");
        jButtonViewEmployeeDetails.addActionListener(this::jButtonViewEmployeeDetailsActionPerformed);
        jPanel1.add(jButtonViewEmployeeDetails, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 480, 130, 30));

        jLabel12.setText("Status:");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 420, 50, 30));

        jTextFieldStatus.setEditable(false);
        jTextFieldStatus.addActionListener(this::jTextFieldStatusActionPerformed);
        jPanel1.add(jTextFieldStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 420, 250, 30));

        jTextAreaAddress.setEditable(false);
        jTextAreaAddress.setColumns(20);
        jTextAreaAddress.setRows(5);
        jScrollPane2.setViewportView(jTextAreaAddress);

        jPanel1.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 290, 250, 40));

        jButtonLogout.setBackground(new java.awt.Color(0, 255, 204));
        jButtonLogout.setText("Logout");
        jButtonLogout.addActionListener(this::jButtonLogoutActionPerformed);
        jPanel1.add(jButtonLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 80, 30));

        jButtonLeaveManagement.setText("Leave Management");
        jButtonLeaveManagement.addActionListener(this::jButtonLeaveManagementActionPerformed);
        jPanel1.add(jButtonLeaveManagement, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, 200, 40));

        jButtonLogout1.setBackground(new java.awt.Color(0, 255, 204));
        jButtonLogout1.setText("Logout");
        jButtonLogout1.addActionListener(this::jButtonLogout1ActionPerformed);
        jPanel1.add(jButtonLogout1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 80, 30));

        jButtonLogout2.setBackground(new java.awt.Color(0, 255, 204));
        jButtonLogout2.setText("Logout");
        jButtonLogout2.addActionListener(this::jButtonLogout2ActionPerformed);
        jPanel1.add(jButtonLogout2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 80, 30));

        jButtonLogout3.setBackground(new java.awt.Color(0, 255, 204));
        jButtonLogout3.setText("Logout");
        jButtonLogout3.addActionListener(this::jButtonLogout3ActionPerformed);
        jPanel1.add(jButtonLogout3, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 80, 30));

        jButtonBack.setText("Back");
        jButtonBack.addActionListener(this::jButtonBackActionPerformed);
        jPanel1.add(jButtonBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 573, 120, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1320, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1320, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 630, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 630, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCreateRecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateRecActionPerformed
        System.out.println("CREATE BUTTON CLICKED, role: " + (currentUser != null ? currentUser.getRole() : "null"));
    
    if (currentUser == null ||
            (currentUser.getRole() != Role.ADMIN &&
             currentUser.getRole() != Role.HR)) {
        JOptionPane.showMessageDialog(this,
                "Access Denied.\nOnly HR and Admin can create employees.",
                "Access Restricted", JOptionPane.WARNING_MESSAGE);
        return;
    }
    this.setVisible(false);
    new CreateEmployee(currentUser, this).setVisible(true);
    }//GEN-LAST:event_jButtonCreateRecActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        clearDetailFields();
        jTableDataBase.clearSelection();
        // Re-apply role buttons after clear so Create/Refresh stay enabled
        configureButtonsByRole();
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        loadEmployeeData();
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void jButtonEditRecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditRecActionPerformed
       if (currentUser == null ||
                (currentUser.getRole() != Role.ADMIN &&
                 currentUser.getRole() != Role.HR)) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied.\nOnly HR and Admin can edit employees.",
                    "Access Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedId = jTextFieldEmpNum.getText().trim();
        if (selectedId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Edit Employee feature is under development.\nEmployee ID: " + selectedId,
                "Coming Soon", JOptionPane.INFORMATION_MESSAGE);

    }//GEN-LAST:event_jButtonEditRecActionPerformed

    private void jButtonDeleteRecActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteRecActionPerformed
         if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied.\nOnly Admin can delete employees.",
                    "Access Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String selectedId = jTextFieldEmpNum.getText().trim();
        if (selectedId.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete Employee #" + selectedId + "?\n"
                + "This action cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
 
        try {
            boolean ok = deleteEmployee(selectedId);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Employee deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                loadEmployeeData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Delete failed. Employee may not exist.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Delete failed", ex);
            JOptionPane.showMessageDialog(this,
                    "Database Error:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButtonDeleteRecActionPerformed
    
    
    private void jTextFieldPositionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPositionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPositionActionPerformed

    private void jButtonViewAllEmpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewAllEmpActionPerformed
        loadEmployeeData();
    }//GEN-LAST:event_jButtonViewAllEmpActionPerformed

    private void jButtonViewAllEmponViewAllEmployeesClick(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewAllEmponViewAllEmployeesClick
        loadEmployeeData();
    }//GEN-LAST:event_jButtonViewAllEmponViewAllEmployeesClick

    private void jTextFieldLastNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldLastNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldLastNameActionPerformed

    private void jTextFieldFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldFirstNameActionPerformed

    private void jTextFieldPhoneNumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPhoneNumActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPhoneNumActionPerformed

    private void jTextFieldSupervisorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldSupervisorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldSupervisorActionPerformed

    private void jTableDataBaseMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableDataBaseMouseClicked
        System.out.println("ROW CLICKED: " + jTableDataBase.getSelectedRow());
        int row = jTableDataBase.getSelectedRow();
        if (row < 0) return;
 
        jTextFieldEmpNum.setText(safeCell(row, 0));
        jTextFieldLastName.setText(safeCell(row, 1));
        jTextFieldFirstName.setText(safeCell(row, 2));
        jTextAreaAddress.setText(safeCell(row, 4));
        jTextFieldPhoneNum.setText(safeCell(row, 5));
        jTextFieldSupervisor.setText(safeCell(row, 12));
        jTextFieldPosition.setText(safeCell(row, 11));
        jTextFieldStatus.setText(safeCell(row, 10));
 
        jButtonClear.setEnabled(true);
        jButtonViewEmployeeDetails.setEnabled(true);
 
        boolean canEdit = currentUser != null &&
                (currentUser.getRole() == Role.ADMIN ||
                 currentUser.getRole() == Role.HR);
        jButtonEditRec.setEnabled(canEdit);
 
        boolean canDelete = currentUser != null &&
                currentUser.getRole() == Role.ADMIN;
        jButtonDeleteRec.setEnabled(canDelete);
    }//GEN-LAST:event_jTableDataBaseMouseClicked

    private void jButtonViewEmployeeDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonViewEmployeeDetailsActionPerformed
        String selectedId = jTextFieldEmpNum.getText().trim();
            if (selectedId.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please select an employee from the table first.",
                        "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                Employee emp = employeeDAO.findById(selectedId);
                if (emp == null) {
                    JOptionPane.showMessageDialog(this,
                            "Employee record not found for ID: " + selectedId,
                            "Not Found", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                this.setVisible(false);
                new ViewEmployee(currentUser, emp, this).setVisible(true);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "Error loading employee", ex);
                JOptionPane.showMessageDialog(this,
                        "Error loading employee:\n" + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
    }//GEN-LAST:event_jButtonViewEmployeeDetailsActionPerformed

    private void jTextFieldStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldStatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldStatusActionPerformed

    private void jButtonLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogoutActionPerformed
        SessionManager.getInstance().logout();
        new Login().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButtonLogoutActionPerformed

    private void jButtonLeaveManagementActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLeaveManagementActionPerformed
         this.setVisible(false);
        new LeaveManagement(currentUser, this).setVisible(true);
    }//GEN-LAST:event_jButtonLeaveManagementActionPerformed

    private void jButtonLogout1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogout1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLogout1ActionPerformed

    private void jButtonLogout2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogout2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLogout2ActionPerformed

    private void jButtonLogout3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonLogout3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonLogout3ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new AdminPage().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonCreateRec;
    private javax.swing.JButton jButtonDeleteRec;
    private javax.swing.JButton jButtonEditRec;
    private javax.swing.JButton jButtonLeaveManagement;
    private javax.swing.JButton jButtonLogout;
    private javax.swing.JButton jButtonLogout1;
    private javax.swing.JButton jButtonLogout2;
    private javax.swing.JButton jButtonLogout3;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonViewAllEmp;
    private javax.swing.JButton jButtonViewEmployeeDetails;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableDataBase;
    private javax.swing.JTextArea jTextAreaAddress;
    private javax.swing.JTextField jTextFieldEmpNum;
    private javax.swing.JTextField jTextFieldFirstName;
    private javax.swing.JTextField jTextFieldLastName;
    private javax.swing.JTextField jTextFieldPhoneNum;
    private javax.swing.JTextField jTextFieldPosition;
    private javax.swing.JTextField jTextFieldStatus;
    private javax.swing.JTextField jTextFieldSupervisor;
    // End of variables declaration//GEN-END:variables

     private boolean deleteEmployee(String empId) throws java.sql.SQLException {
        String sql = "DELETE FROM employee WHERE employee_id = ?";
        try (java.sql.Connection conn = DAO.Database.getInstance().getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, empId);
            return ps.executeUpdate() >= 1;
        }
    }
 
    private String safeCell(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }
}