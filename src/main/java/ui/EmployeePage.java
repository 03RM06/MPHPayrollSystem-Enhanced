package ui;

import DAO.Database;
import DAO.EmployeeDAO;
import Model.Employee;
import Model.Role;
import Model.UserAccount;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Employee management panel (JPanel).
 * Embedded inside {@link MainShell} when the user navigates to "EMPLOYEES".
 *
 * Features:
 *  - Full employee table with 16 columns
 *  - Live search/filter by Employee ID, Last Name, or First Name (case-insensitive)
 *  - Role-gated Create / Edit / Delete buttons (ADMIN and HR only for write ops)
 *  - Detail fields updated on row selection
 *  - "View Details" opens the existing ViewEmployee JFrame for the selected employee
 */
public class EmployeePage extends JPanel {

    private static final Logger logger =
            Logger.getLogger(EmployeePage.class.getName());

    private final UserAccount currentUser;
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    // ── Table ──────────────────────────────────────────────────────────────
    private DefaultTableModel              tableModel;
    private JTable                         table;
    private TableRowSorter<DefaultTableModel> rowSorter;

    // ── Search ─────────────────────────────────────────────────────────────
    private JTextField searchField;

    // ── Detail fields ──────────────────────────────────────────────────────
    private JTextField tfEmpNum, tfLastName, tfFirstName, tfPhone,
                       tfSupervisor, tfPosition, tfStatus;
    private JTextArea  taAddress;

    // ── Action buttons ─────────────────────────────────────────────────────
    private JButton btnCreate, btnEdit, btnDelete,
                    btnRefresh, btnViewDetails, btnClear;

    // ─────────────────────────────────────────────────────────────────────
    public EmployeePage(UserAccount user) {
        super(new BorderLayout(0, 0));
        this.currentUser = user;
        buildUi();
        configureByRole();
        loadData();
    }

    // ── UI construction ───────────────────────────────────────────────────

    private void buildUi() {
        // Title bar + search bar
        add(buildTopBar(), BorderLayout.NORTH);

        // Table (top) + detail panel (bottom) in a split pane
        JScrollPane tableScroll = new JScrollPane(buildTable());
        tableScroll.setPreferredSize(new Dimension(900, 400));

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT, tableScroll, buildDetailPanel());
        split.setResizeWeight(0.65);
        split.setDividerLocation(350);
        add(split, BorderLayout.CENTER);
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("Employee Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bar.add(title, BorderLayout.WEST);

        // Search controls
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setBackground(Color.WHITE);

        searchRow.add(new JLabel("Search (name or ID):"));
        searchField = new JTextField(22);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setToolTipText("Filter by Employee ID, Last Name, or First Name");
        searchRow.add(searchField);

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        searchRow.add(searchButton);

        JButton clearSearchBtn = new JButton("Clear");
        clearSearchBtn.addActionListener(e -> clearSearch());
        searchRow.add(clearSearchBtn);

        // Search on Enter key in the text field
        searchField.addActionListener(e -> performSearch());

        bar.add(searchRow, BorderLayout.EAST);
        return bar;
    }

    private JTable buildTable() {
        String[] cols = {
            "Emp ID", "Last Name", "First Name", "Birthday",
            "Address", "Phone No.", "SSS No.", "PhilHealth No.",
            "TIN No.", "Pag-IBIG No.", "Status", "Position",
            "Supervisor", "Basic Salary", "Semi-Monthly", "Hourly Rate"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setRowHeight(22);

        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onRowSelected();
        });

        return table;
    }

    private JPanel buildDetailPanel() {
        JPanel detail = new JPanel(new BorderLayout());
        detail.setBorder(BorderFactory.createTitledBorder("Employee Details"));

        // Grid of label + field pairs — 4 columns per row (label, field, label, field)
        JPanel fields = new JPanel(new GridLayout(0, 4, 8, 6));
        fields.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        tfEmpNum     = addField(fields, "Employee No.");
        tfLastName   = addField(fields, "Last Name");
        tfFirstName  = addField(fields, "First Name");
        tfPhone      = addField(fields, "Phone No.");
        tfPosition   = addField(fields, "Position");
        tfStatus     = addField(fields, "Status");
        tfSupervisor = addField(fields, "Supervisor");

        // Address spans multiple cells
        fields.add(new JLabel("Address:"));
        taAddress = new JTextArea(2, 15);
        taAddress.setEditable(false);
        taAddress.setLineWrap(true);
        taAddress.setWrapStyleWord(true);
        fields.add(new JScrollPane(taAddress));
        // Pad the remaining two columns in this row
        fields.add(new JLabel());
        fields.add(new JLabel());

        detail.add(fields, BorderLayout.CENTER);
        detail.add(buildButtonRow(), BorderLayout.SOUTH);
        return detail;
    }

    /** Adds a JLabel + non-editable JTextField pair to a GridLayout panel. */
    private JTextField addField(JPanel panel, String labelText) {
        panel.add(new JLabel(labelText + ":"));
        JTextField tf = new JTextField();
        tf.setEditable(false);
        panel.add(tf);
        return tf;
    }

    private JPanel buildButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

        btnRefresh     = new JButton("Refresh");
        btnCreate      = new JButton("Create Employee");
        btnEdit        = new JButton("Edit Employee");
        btnDelete      = new JButton("Delete Employee");
        btnViewDetails = new JButton("View Details");
        btnClear       = new JButton("Clear Selection");

        // Visual distinction for the destructive button
        btnDelete.setBackground(new Color(200, 50, 50));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setOpaque(true);
        btnDelete.setBorderPainted(false);

        btnRefresh    .addActionListener(e -> loadData());
        btnCreate     .addActionListener(e -> onCreateEmployee());
        btnEdit       .addActionListener(e -> onEditEmployee());
        btnDelete     .addActionListener(e -> onDeleteEmployee());
        btnViewDetails.addActionListener(e -> onViewDetails());
        btnClear      .addActionListener(e -> clearSelection());

        row.add(btnRefresh);
        row.add(btnCreate);
        row.add(btnEdit);
        row.add(btnDelete);
        row.add(btnViewDetails);
        row.add(btnClear);
        return row;
    }

    // ── Role-based button visibility ──────────────────────────────────────

    private void configureByRole() {
        if (currentUser == null) {
            btnCreate.setEnabled(false);
            btnEdit.setEnabled(false);
            btnDelete.setVisible(false);
            return;
        }
        Role role = currentUser.getRole();
        boolean isAdminOrHR = (role == Role.ADMIN || role == Role.HR);
        boolean isAdminOnly = (role == Role.ADMIN);

        btnCreate.setEnabled(isAdminOrHR);
        btnDelete.setVisible(isAdminOnly);

        // Row-dependent buttons start disabled until a row is selected
        btnEdit       .setEnabled(false);
        btnDelete     .setEnabled(false);
        btnViewDetails.setEnabled(false);
        btnClear      .setEnabled(false);
    }

    // ── Data loading ──────────────────────────────────────────────────────

    private void loadData() {
        tableModel.setRowCount(0);
        clearDetailFields();
        try {
            List<Employee> employees = employeeDAO.findAll();
            for (Employee e : employees) {
                tableModel.addRow(toRow(e));
            }
            configureByRole();   // restore after load
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to load employees", ex);
            JOptionPane.showMessageDialog(this,
                    "Error loading employees:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Object[] toRow(Employee e) {
        return new Object[]{
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
        };
    }

    // ── Search / filter ───────────────────────────────────────────────────

    /** Filters the table to rows whose ID, last name, or first name match. */
    private void performSearch() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            rowSorter.setRowFilter(null);
            return;
        }
        try {
            // Columns 0 = Emp ID, 1 = Last Name, 2 = First Name
            String escaped = Pattern.quote(text);
            rowSorter.setRowFilter(
                    RowFilter.regexFilter("(?i)" + escaped, 0, 1, 2));
        } catch (java.util.regex.PatternSyntaxException ex) {
            rowSorter.setRowFilter(null);
        }
    }

    /** Clears the search field and removes the filter. */
    private void clearSearch() {
        searchField.setText("");
        rowSorter.setRowFilter(null);
    }

    // ── Row selection handler ─────────────────────────────────────────────

    private void onRowSelected() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return;
        // Convert view index to model index (necessary when a filter is active)
        int modelRow = table.convertRowIndexToModel(viewRow);

        tfEmpNum    .setText(cellAt(modelRow,  0));
        tfLastName  .setText(cellAt(modelRow,  1));
        tfFirstName .setText(cellAt(modelRow,  2));
        taAddress   .setText(cellAt(modelRow,  4));
        tfPhone     .setText(cellAt(modelRow,  5));
        tfSupervisor.setText(cellAt(modelRow, 12));
        tfPosition  .setText(cellAt(modelRow, 11));
        tfStatus    .setText(cellAt(modelRow, 10));

        btnClear      .setEnabled(true);
        btnViewDetails.setEnabled(true);

        if (currentUser != null) {
            boolean canEdit   = currentUser.getRole() == Role.ADMIN
                             || currentUser.getRole() == Role.HR;
            boolean canDelete = currentUser.getRole() == Role.ADMIN;
            btnEdit  .setEnabled(canEdit);
            btnDelete.setEnabled(canDelete);
        }
    }

    private void clearDetailFields() {
        if (tfEmpNum     != null) tfEmpNum    .setText("");
        if (tfLastName   != null) tfLastName  .setText("");
        if (tfFirstName  != null) tfFirstName .setText("");
        if (taAddress    != null) taAddress   .setText("");
        if (tfPhone      != null) tfPhone     .setText("");
        if (tfSupervisor != null) tfSupervisor.setText("");
        if (tfPosition   != null) tfPosition  .setText("");
        if (tfStatus     != null) tfStatus    .setText("");

        if (btnEdit       != null) btnEdit       .setEnabled(false);
        if (btnDelete     != null) btnDelete     .setEnabled(false);
        if (btnViewDetails!= null) btnViewDetails.setEnabled(false);
        if (btnClear      != null) btnClear      .setEnabled(false);
    }

    private void clearSelection() {
        table.clearSelection();
        clearDetailFields();
    }

    // ── Button action handlers ────────────────────────────────────────────

    private void onCreateEmployee() {
        if (currentUser == null
                || (currentUser.getRole() != Role.ADMIN
                    && currentUser.getRole() != Role.HR)) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied. Only HR and Admin can create employees.",
                    "Access Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFrame topFrame = getTopFrame();
        new CreateEmployee(currentUser, topFrame).setVisible(true);
    }

    private void onEditEmployee() {
        if (currentUser == null
                || (currentUser.getRole() != Role.ADMIN
                    && currentUser.getRole() != Role.HR)) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied. Only HR and Admin can edit employees.",
                    "Access Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = tfEmpNum.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Edit Employee feature is under development.\nEmployee ID: " + id,
                "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onDeleteEmployee() {
        if (currentUser == null || currentUser.getRole() != Role.ADMIN) {
            JOptionPane.showMessageDialog(this,
                    "Access Denied. Only Admin can delete employees.",
                    "Access Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = tfEmpNum.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete Employee #" + id + "?  This cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            String sql = "DELETE FROM employee WHERE employee_id = ?";
            try (Connection conn = Database.getInstance().getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, id);
                if (ps.executeUpdate() >= 1) {
                    JOptionPane.showMessageDialog(this,
                            "Employee deleted successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Delete failed — employee may not exist.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Delete failed", ex);
            JOptionPane.showMessageDialog(this,
                    "Database Error:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onViewDetails() {
        String id = tfEmpNum.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please select an employee from the table first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Employee emp = employeeDAO.findById(id);
            if (emp == null) {
                JOptionPane.showMessageDialog(this,
                        "Employee not found for ID: " + id,
                        "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JFrame topFrame = getTopFrame();
            new ViewEmployee(currentUser, emp, topFrame).setVisible(true);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error loading employee", ex);
            JOptionPane.showMessageDialog(this,
                    "Error loading employee:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Utilities ─────────────────────────────────────────────────────────

    /** Returns the top-level JFrame ancestor, or null. */
    private JFrame getTopFrame() {
        Window w = SwingUtilities.getWindowAncestor(this);
        return (w instanceof JFrame) ? (JFrame) w : null;
    }

    private String cellAt(int modelRow, int col) {
        Object v = tableModel.getValueAt(modelRow, col);
        return v != null ? v.toString() : "";
    }

    private static String safe(String v) { return v != null ? v : ""; }

    private static String fmt(BigDecimal v) {
        return v != null ? String.format("%,.2f", v) : "0.00";
    }
}
