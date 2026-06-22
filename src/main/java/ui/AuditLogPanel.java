package ui;

import Model.AuditLog;
import Services.AuditLogService;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Read-only panel that displays the audit log.
 * Access is restricted at the navigation level (IT/ADMIN roles).
 */
public class AuditLogPanel extends JPanel {

    private static final DateTimeFormatter TS_FMT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String[] COLUMNS = {
        "Timestamp", "Performed By", "Action",
        "Target Entity", "Target ID", "Details"
    };

    private final DefaultTableModel tableModel;
    private final JTable            table;
    private final JTextField        filterField;

    public AuditLogPanel() {
        super(new BorderLayout(4, 4));

        // ── table ─────────────────────────────────────────────────────────
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.getTableHeader().setFont(
            new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        table.setRowHeight(22);

        // Set preferred column widths
        int[] widths = {150, 110, 150, 110, 90, 300};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);

        // ── toolbar ───────────────────────────────────────────────────────
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));

        toolbar.add(new JLabel("Filter by user:"));
        filterField = new JTextField(16);
        toolbar.add(filterField);

        JButton filterBtn = new JButton("Filter");
        filterBtn.addActionListener(e -> filterByUser());
        toolbar.add(filterBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadAll());
        toolbar.add(refreshBtn);

        add(toolbar, BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);

        // Load on creation
        loadAll();
    }

    // ── data loading ──────────────────────────────────────────────────────

    private void loadAll() {
        List<AuditLog> logs = AuditLogService.getAll();
        populateTable(logs);
    }

    private void filterByUser() {
        String username = filterField.getText().trim();
        if (username.isEmpty()) {
            loadAll();
            return;
        }
        List<AuditLog> logs = AuditLogService.getByUser(username);
        populateTable(logs);
    }

    private void populateTable(List<AuditLog> logs) {
        tableModel.setRowCount(0);
        for (AuditLog a : logs) {
            String timestamp = (a.getPerformedAt() != null)
                ? a.getPerformedAt().format(TS_FMT)
                : "";
            tableModel.addRow(new Object[]{
                timestamp,
                a.getPerformedBy(),
                a.getAction(),
                a.getTargetEntity(),
                a.getTargetId(),
                a.getDetails()
            });
        }
    }
}
