package ui;
 
import DAO.UserAccountDAO;
import Model.UserAccount;
import Services.SessionManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
 
/**
 * Login form for MotorPH Payroll System.
 *
 * Authenticates via UserAccountDAO.login() (SHA-256 vs MySQL SHA2 hashes).
 * All roles are redirected to ViewEmployee after a successful login.
 */
public class Login extends javax.swing.JFrame {
 
    private static final Logger logger =
            Logger.getLogger(Login.class.getName());
 
    /** Populated after a successful validateLogin() call. */
    private UserAccount userAccount;
 
    private final UserAccountDAO userAccountDAO = new UserAccountDAO();
 
    // ─────────────────────────────────────────────────────────────────────
    public Login() {
        initComponents();
        setLocationRelativeTo(null);
        setTitle("MotorPH Payroll System — Login");
    }
 
    // ─────────────────────────────────────────────────────────────────────
    //  Core logic
    // ─────────────────────────────────────────────────────────────────────
 
    /**
     * Validates credentials against the database.
     * Populates {@link #userAccount} on success and returns true.
     * Shows a dialog and returns false on any failure.
     */
    private boolean validateLogin(String username, String password) {
 
        if (username == null || username.isBlank()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your username.",
                    "Login Error", JOptionPane.WARNING_MESSAGE);
            usernameTextfield.requestFocus();
            return false;
        }
        if (password == null || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter your password.",
                    "Login Error", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return false;
        }
 
        try {
            UserAccount account = userAccountDAO.login(username, password);
 
            if (account == null) {
                JOptionPane.showMessageDialog(this,
                        "Incorrect username or password. Please try again.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
                passwordField.requestFocus();
                return false;
            }
 
            this.userAccount = account;
            return true;
 
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Database error during login", ex);
            JOptionPane.showMessageDialog(this,
                    "Database error:\n" + ex.getMessage()
                    + "\n\nPlease contact your system administrator.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    /**
     * All roles go to ViewEmployee after login.
     * ViewEmployee handles role-based visibility internally.
     */
    
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        usernameLabel = new javax.swing.JLabel();
        passwordLabel = new javax.swing.JLabel();
        usernameTextfield = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        loginButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(240, 240, 240));

        jPanel1.setBackground(new java.awt.Color(240, 240, 240));

        jPanel4.setBackground(new java.awt.Color(0, 255, 204));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Verdana", 1, 14)); // NOI18N
        jLabel4.setText("Payroll System");
        jPanel4.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 70, -1, -1));

        jLabel3.setFont(new java.awt.Font("Verdana", 3, 36)); // NOI18N
        jLabel3.setText("MotorPH");
        jPanel4.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 30, -1, -1));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setMinimumSize(new java.awt.Dimension(410, 445));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        usernameLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        usernameLabel.setText("Username:");
        jPanel2.add(usernameLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 80, -1, -1));

        passwordLabel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        passwordLabel.setText("Password:");
        jPanel2.add(passwordLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, -1, -1));

        usernameTextfield.setBackground(new java.awt.Color(0, 255, 204));
        usernameTextfield.addActionListener(this::usernameTextfieldActionPerformed);
        jPanel2.add(usernameTextfield, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 80, 181, -1));

        passwordField.setBackground(new java.awt.Color(0, 255, 204));
        jPanel2.add(passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, 181, -1));

        loginButton.setBackground(new java.awt.Color(0, 255, 204));
        loginButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        loginButton.setText("Login");
        loginButton.addActionListener(this::loginButtonActionPerformed);
        jPanel2.add(loginButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 210, -1, -1));

        backButton.setBackground(new java.awt.Color(0, 255, 204));
        backButton.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        backButton.setText("Exit");
        backButton.addActionListener(this::backButtonActionPerformed);
        jPanel2.add(backButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 210, 80, -1));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(147, 147, 147)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 660, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(154, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        // store inputted username & password of users into variables
        String u = usernameTextfield.getText();
        String p = new String(passwordField.getPassword());

        // validate login credentials
        if (validateLogin(u, p)) {
            SessionManager.getInstance().setCurrentUser(this.userAccount);
            navigateToRoleBasedPage(this.userAccount);
            this.dispose();
        }
        }

        /**
        * Navigate to the appropriate page based on user role
        * @param user the logged-in user
        */
       private void navigateToRoleBasedPage(UserAccount user) {
        new MainShell().setVisible(true);

    }//GEN-LAST:event_loginButtonActionPerformed

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_backButtonActionPerformed

    private void usernameTextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usernameTextfieldActionPerformed
        // TODO add your handling code here:
        passwordField.requestFocus();
    }//GEN-LAST:event_usernameTextfieldActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       try {
            DAO.Database.getInstance().getConnection();
            System.out.println("Database connected successfully.");
        } catch (Exception e) {
            System.err.println("Database connection error: " + e.getMessage());
            JOptionPane.showMessageDialog(null,
                    "Cannot connect to the database:\n" + e.getMessage()
                    + "\n\nCheck your MySQL service and try again.",
                    "Startup Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
 
        // Apply Nimbus look and feel
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
            logger.log(Level.SEVERE, "Look and feel error", ex);
        }
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JButton loginButton;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JLabel usernameLabel;
    private javax.swing.JTextField usernameTextfield;
    // End of variables declaration//GEN-END:variables
}
