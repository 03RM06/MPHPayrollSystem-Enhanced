package ui;

import DAO.UserAccountDAO;
import Model.Permission;
import Model.UserAccount;
import Services.AuditLogService;
import Services.SessionManager;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserManagementPanel extends JPanel {

    private static final String[] COLUMNS   = {"Account ID", "Username", "Employee ID", "Role", "Status"};
    private static final String[] ROLE_NAMES = {"ADMIN", "HR", "FINANCE", "IT", "EMPLOYEE"};

    private final UserAccountDAO   dao = new UserAccountDAO();
    private final DefaultTableModel tableModel;
    private final JTable            table;
    private final JLabel            statusLabel;
    private final JButton           addBtn;
    private final JButton           editBtn;
    private final JButton           deactivateBtn;

    public UserManagementPanel() {
        super(new BorderLayout(4, 4));

        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(22);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] widths = {80, 130, 110, 100, 80};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);

        SessionManager sm = SessionManager.getInstance();

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        addBtn = new JButton("Add User");
        addBtn.setEnabled(sm.hasPermission(Permission.CREATE_USER));
        addBtn.addActionListener(e -> openAddDialog());

        editBtn = new JButton("Edit User");
        editBtn.setEnabled(sm.hasPermission(Permission.EDIT_USER));
        editBtn.addActionListener(e -> openEditDialog());

        deactivateBtn = new JButton("Deactivate");
        deactivateBtn.setEnabled(sm.hasPermission(Permission.MANAGE_PERMISSIONS)
                || sm.hasPermission(Permission.EDIT_USER));
        deactivateBtn.addActionListener(e -> doToggleActive());

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateDeactivateButtonLabel();
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        toolbar.add(refreshBtn);
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deactivateBtn);

        statusLabel = new JLabel(" ");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 12, 6, 12));

        JPanel north = new JPanel(new BorderLayout());
        north.add(title,   BorderLayout.NORTH);
        north.add(toolbar, BorderLayout.CENTER);

        add(north,       BorderLayout.NORTH);
        add(scroll,      BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        loadData();
    }

    // ── data loading ──────────────────────────────────────────────────────

    private void loadData() {
        new SwingWorker<List<UserAccount>, Void>() {
            @Override
            protected List<UserAccount> doInBackground() throws Exception {
                return dao.findAll();
            }
            @Override
            protected void done() {
                try {
                    List<UserAccount> users = get();
                    tableModel.setRowCount(0);
                    for (UserAccount u : users) {
                        tableModel.addRow(new Object[]{
                            u.getAccountId(),
                            u.getUsername(),
                            u.getEmployeeID(),
                            u.getRole() != null ? u.getRole().name() : "",
                            u.getStatusId() == 1 ? "ACTIVE" : "INACTIVE"
                        });
                    }
                    statusLabel.setText(users.size() + " records loaded");
                } catch (Exception ex) {
                    statusLabel.setText("Error loading users: " + ex.getMessage());
                }
            }
        }.execute();
    }

    private void updateDeactivateButtonLabel() {
        int row = table.getSelectedRow();
        if (row < 0) {
            deactivateBtn.setText("Deactivate");
            return;
        }
        String status = (String) tableModel.getValueAt(row, 4);
        deactivateBtn.setText("ACTIVE".equals(status) ? "Deactivate" : "Reactivate");
    }

    // ── Add User dialog ───────────────────────────────────────────────────

    private void openAddDialog() {
        JDialog dlg = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Add User",
            Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(8, 8));

        JTextField    usernameField = new JTextField(18);
        JPasswordField passwordField = new JPasswordField(18);
        JTextField    empIdField    = new JTextField(18);
        JComboBox<String> roleBox   = new JComboBox<>(ROLE_NAMES);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(4, 8, 4, 8);
        gc.anchor  = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; form.add(new JLabel("Username:"),  gc);
        gc.gridx = 1;               form.add(usernameField,            gc);
        gc.gridx = 0; gc.gridy = 1; form.add(new JLabel("Password:"),  gc);
        gc.gridx = 1;               form.add(passwordField,            gc);
        gc.gridx = 0; gc.gridy = 2; form.add(new JLabel("Employee ID:"), gc);
        gc.gridx = 1;               form.add(empIdField,               gc);
        gc.gridx = 0; gc.gridy = 3; form.add(new JLabel("Role:"),      gc);
        gc.gridx = 1;               form.add(roleBox,                  gc);

        JButton ok     = new JButton("Create");
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());

        ok.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String empId    = empIdField.getText().trim();
            int    roleId   = roleBox.getSelectedIndex() + 1;

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,
                    "Username and password are required.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                UserAccount ua = new UserAccount();
                ua.setUsername(username);
                ua.setPassword(password);
                ua.setEmployeeID(empId.isEmpty() ? null : empId);

                UserAccount created = dao.create(ua, roleId);
                AuditLogService.log("CREATE_USER", "UserAccount",
                    String.valueOf(created.getAccountId()), username);

                dlg.dispose();
                loadData();
                JOptionPane.showMessageDialog(UserManagementPanel.this,
                    "User created successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg,
                    "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(ok);
        btnRow.add(cancel);

        dlg.add(form,   BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ── Edit User dialog ──────────────────────────────────────────────────

    private void openEditDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Select a user to edit.", "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    accountId = (int)    tableModel.getValueAt(row, 0);
        String username  = (String) tableModel.getValueAt(row, 1);
        String empId     = (String) tableModel.getValueAt(row, 2);
        String roleName  = (String) tableModel.getValueAt(row, 3);

        JDialog dlg = new JDialog(
            SwingUtilities.getWindowAncestor(this),
            "Edit User",
            Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setLayout(new BorderLayout(8, 8));

        JTextField    usernameField = new JTextField(username, 18);
        JTextField    empIdField    = new JTextField(empId != null ? empId : "", 18);
        JComboBox<String> roleBox   = new JComboBox<>(ROLE_NAMES);

        for (int i = 0; i < ROLE_NAMES.length; i++) {
            if (ROLE_NAMES[i].equals(roleName)) { roleBox.setSelectedIndex(i); break; }
        }
        int originalRoleId = roleBox.getSelectedIndex() + 1;

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets  = new Insets(4, 8, 4, 8);
        gc.anchor  = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; form.add(new JLabel("Username:"),    gc);
        gc.gridx = 1;               form.add(usernameField,              gc);
        gc.gridx = 0; gc.gridy = 1; form.add(new JLabel("Employee ID:"), gc);
        gc.gridx = 1;               form.add(empIdField,                 gc);
        gc.gridx = 0; gc.gridy = 2; form.add(new JLabel("Role:"),        gc);
        gc.gridx = 1;               form.add(roleBox,                    gc);

        JButton ok     = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dlg.dispose());

        ok.addActionListener(e -> {
            String newUsername = usernameField.getText().trim();
            String newEmpId   = empIdField.getText().trim();
            int    newRoleId  = roleBox.getSelectedIndex() + 1;

            if (newUsername.isEmpty()) {
                JOptionPane.showMessageDialog(dlg,
                    "Username is required.", "Validation",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                UserAccount ua = new UserAccount();
                ua.setAccountId(accountId);
                ua.setUsername(newUsername);
                ua.setEmployeeID(newEmpId.isEmpty() ? null : newEmpId);
                dao.update(ua);

                if (newRoleId != originalRoleId) {
                    dao.setRoleForUser(accountId, newRoleId);
                }

                AuditLogService.log("EDIT_USER", "UserAccount",
                    String.valueOf(accountId), newUsername);

                dlg.dispose();
                loadData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dlg,
                    "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRow.add(ok);
        btnRow.add(cancel);

        dlg.add(form,   BorderLayout.CENTER);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    // ── Deactivate / Reactivate ───────────────────────────────────────────

    private void doToggleActive() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Select a user first.", "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int    accountId = (int)    tableModel.getValueAt(row, 0);
        String username  = (String) tableModel.getValueAt(row, 1);
        String roleName  = (String) tableModel.getValueAt(row, 3);
        String status    = (String) tableModel.getValueAt(row, 4);
        boolean isActive = "ACTIVE".equals(status);

        UserAccount currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getAccountId() == accountId) {
            JOptionPane.showMessageDialog(this,
                "You cannot deactivate your own account.",
                "Not Allowed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isActive && "ADMIN".equals(roleName)) {
            try {
                if (dao.countActiveAdmins() <= 1) {
                    JOptionPane.showMessageDialog(this,
                        "Cannot deactivate the last active administrator.",
                        "Not Allowed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            dao.setActive(accountId, !isActive);
            String action = isActive ? "DEACTIVATE_USER" : "REACTIVATE_USER";
            AuditLogService.log(action, "UserAccount",
                String.valueOf(accountId), username);
            loadData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
