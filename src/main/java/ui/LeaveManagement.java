package ui;
 
import DAO.LeaveDAO;
import Model.Role;
import Model.UserAccount;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
 
public class LeaveManagement extends javax.swing.JFrame {
 
    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(LeaveManagement.class.getName());
 
    private final UserAccount        currentUser;
    private final javax.swing.JFrame parentFrame;
    private final LeaveDAO           leaveDAO = new LeaveDAO();
 
    // ── table model ───────────────────────────────────────────────────────
    private DefaultTableModel tableModel;
 
    // ─────────────────────────────────────────────────────────────────────
    //  Primary constructor — called from ViewEmployee
    // ─────────────────────────────────────────────────────────────────────
    public LeaveManagement(UserAccount user, javax.swing.JFrame parent) {
        this.currentUser = user;
        this.parentFrame = parent;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("MotorPH — Leave Management");
        setupTable();
        loadLeaveData();
    }
 
    /** No-arg constructor — NetBeans Form Editor only. */
    public LeaveManagement() {
        this.currentUser = null;
        this.parentFrame = null;
        initComponents();
        setupTable();
    }
 
    // ─────────────────────────────────────────────────────────────────────
    //  Table setup
    // ─────────────────────────────────────────────────────────────────────
 
    private void setupTable() {
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Emp ID", "Employee Name",
                         "Leave Type", "Start Date",
                         "End Date", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        jTable.setModel(tableModel);
        jTable.setRowHeight(24);
        jTable.getTableHeader().setFont(
                new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
 
        // Colour-code rows by status
        jTable.setDefaultRenderer(Object.class,
                new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    Object statusObj = table.getModel().getValueAt(row, 6);
                    String status = statusObj != null ? statusObj.toString() : "";
                    if ("Approved".equalsIgnoreCase(status)) {
                        setBackground(new java.awt.Color(204, 255, 204));
                        setForeground(java.awt.Color.BLACK);
                    } else if ("Denied".equalsIgnoreCase(status)) {
                        setBackground(new java.awt.Color(255, 204, 204));
                        setForeground(java.awt.Color.BLACK);
                    } else {
                        setBackground(java.awt.Color.WHITE);
                        setForeground(java.awt.Color.BLACK);
                    }
                }
                return this;
            }
        });
    }
 
    // ─────────────────────────────────────────────────────────────────────
    //  Data loading
    // ─────────────────────────────────────────────────────────────────────
 
    private void loadLeaveData() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        try {
            List<String[]> leaves = leaveDAO.getAllLeaves();
            for (String[] row : leaves) {
                tableModel.addRow(new Object[]{
                    row[0],  // request_id
                    row[1],  // employee_id
                    row[2],  // employee_name
                    row[3],  // leave_type
                    row[4],  // start_date
                    row[5],  // end_date
                    row[6]   // approval_status
                });
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to load leave requests", ex);
            JOptionPane.showMessageDialog(this,
                    "Error loading leave requests:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    // ─────────────────────────────────────────────────────────────────────
    //  Approve / Reject logic
    // ─────────────────────────────────────────────────────────────────────
 
    private void updateLeaveStatus(String newStatus) {
        int selectedRow = jTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a request from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
 
        String requestId    = tableModel.getValueAt(selectedRow, 0).toString();
        String employeeName = tableModel.getValueAt(selectedRow, 2).toString();
        String status       = tableModel.getValueAt(selectedRow, 6).toString();
 
        if (!status.equalsIgnoreCase("Pending")) {
            JOptionPane.showMessageDialog(this,
                    "This request has already been " + status + ".",
                    "Already Processed", JOptionPane.WARNING_MESSAGE);
            jButtonApprove.setEnabled(false);
            jButtonReject1.setEnabled(false);
            return;
        }
 
        int confirm = JOptionPane.showConfirmDialog(this,
                newStatus + " leave request for " + employeeName + "?",
                "Confirm " + newStatus, JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
 
        try {
            leaveDAO.updateLeaveStatus(
                    requestId,
                    newStatus,
                    currentUser != null ? currentUser.getEmployeeID() : ""
            );
            JOptionPane.showMessageDialog(this,
                    "Leave request " + newStatus.toLowerCase() + "d successfully.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            loadLeaveData();
            jButtonApprove.setEnabled(false);
            jButtonReject1.setEnabled(false);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to update leave status", ex);
            JOptionPane.showMessageDialog(this,
                    "Error updating request:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();
        jButtonRefresh = new javax.swing.JButton();
        jButtonApprove = new javax.swing.JButton();
        jButtonReject1 = new javax.swing.JButton();
        jButtonBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 255, 204));
        jPanel1.setForeground(new java.awt.Color(51, 51, 51));
        jPanel1.setMinimumSize(new java.awt.Dimension(1312, 624));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(0, 255, 204));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setText("Leave Management");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, -1, -1));

        jPanel1.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1310, 70));

        jTable.setBackground(new java.awt.Color(0, 0, 0));
        jTable.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jTable.setForeground(new java.awt.Color(255, 255, 255));
        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 1140, 550));

        jButtonRefresh.setText("Refresh");
        jButtonRefresh.addActionListener(this::jButtonRefreshActionPerformed);
        jPanel1.add(jButtonRefresh, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 530, 170, 40));

        jButtonApprove.setBackground(new java.awt.Color(0, 255, 204));
        jButtonApprove.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonApprove.setForeground(new java.awt.Color(255, 255, 255));
        jButtonApprove.setText("Approve");
        jButtonApprove.setEnabled(false);
        jButtonApprove.addActionListener(this::jButtonApproveActionPerformed);
        jPanel1.add(jButtonApprove, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 70, 150, 50));

        jButtonReject1.setBackground(new java.awt.Color(204, 0, 0));
        jButtonReject1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButtonReject1.setForeground(new java.awt.Color(255, 255, 255));
        jButtonReject1.setText("Reject");
        jButtonReject1.setEnabled(false);
        jButtonReject1.addActionListener(this::jButtonReject1ActionPerformed);
        jPanel1.add(jButtonReject1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1150, 130, 150, 50));

        jButtonBack.setText("Back");
        jButtonBack.addActionListener(this::jButtonBackActionPerformed);
        jPanel1.add(jButtonBack, new org.netbeans.lib.awtextra.AbsoluteConstraints(1140, 580, 170, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1310, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 620, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMouseClicked
        // TODO add your handling code here:
        jButtonApprove.setEnabled(true);
        jButtonReject1.setEnabled(true);

    }//GEN-LAST:event_jTableMouseClicked

    private void jButtonRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRefreshActionPerformed
        loadLeaveData();
        jButtonApprove.setEnabled(false);
        jButtonReject1.setEnabled(false);
    }//GEN-LAST:event_jButtonRefreshActionPerformed

    private void jButtonApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonApproveActionPerformed
         updateLeaveStatus("Approved");
    }//GEN-LAST:event_jButtonApproveActionPerformed

    private void jButtonReject1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonReject1ActionPerformed
        updateLeaveStatus("Denied");

    }//GEN-LAST:event_jButtonReject1ActionPerformed

    private void jButtonBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackActionPerformed
        AdminPage AdminPage = new AdminPage();
        this.setVisible(false);
        AdminPage.setVisible(true);
    }//GEN-LAST:event_jButtonBackActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LeaveManagement().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonApprove;
    private javax.swing.JButton jButtonBack;
    private javax.swing.JButton jButtonRefresh;
    private javax.swing.JButton jButtonReject1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    // End of variables declaration//GEN-END:variables

}
