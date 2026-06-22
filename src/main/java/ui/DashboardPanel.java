package ui;

import DAO.AuditLogDAO;
import DAO.Database;
import DAO.PayrollPeriodDAO;
import Model.AuditLog;
import Model.PayrollPeriod;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

/**
 * Landing dashboard panel.
 * Shows 4 KPI cards and a recent-activity table (last 10 audit_log rows).
 * All database calls are executed off the EDT via SwingWorker.
 */
public class DashboardPanel extends JPanel {

    private static final DateTimeFormatter TS_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ── KPI value labels (populated by SwingWorker) ───────────────────────
    private final JLabel lblTotalEmployees = makeKpiValue("...");
    private final JLabel lblOpenPeriod     = makeKpiValue("...");
    private final JLabel lblPendingLeave   = makeKpiValue("...");
    private final JLabel lblTotalPayroll   = makeKpiValue("...");

    // ── Recent-activity table ─────────────────────────────────────────────
    private final DefaultTableModel activityModel;
    private final JTable            activityTable;

    // ─────────────────────────────────────────────────────────────────────
    public DashboardPanel() {
        super(new BorderLayout(0, 0));
        setBackground(new Color(245, 245, 248));

        activityModel = new DefaultTableModel(
                new String[]{"Timestamp", "User", "Action", "Details"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        activityTable = new JTable(activityModel);
        activityTable.setRowHeight(22);
        activityTable.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 12));
        activityTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        activityTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        activityTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        activityTable.getColumnModel().getColumn(3).setPreferredWidth(350);

        buildUi();
        loadData();
    }

    // ── UI construction ───────────────────────────────────────────────────

    private void buildUi() {
        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Color.WHITE);
        titleBar.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleBar.add(titleLabel, BorderLayout.WEST);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());
        titleBar.add(refreshBtn, BorderLayout.EAST);

        add(titleBar, BorderLayout.NORTH);

        // KPI row
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 12, 0));
        kpiRow.setBackground(new Color(245, 245, 248));
        kpiRow.setBorder(new EmptyBorder(20, 20, 12, 20));

        kpiRow.add(makeKpiCard("Total Employees (Regular)",
                lblTotalEmployees, new Color(52, 152, 219)));
        kpiRow.add(makeKpiCard("Open Payroll Period",
                lblOpenPeriod,     new Color(46, 204, 113)));
        kpiRow.add(makeKpiCard("Pending Leave Requests",
                lblPendingLeave,   new Color(230, 126, 34)));
        kpiRow.add(makeKpiCard("Total Payroll Records",
                lblTotalPayroll,   new Color(155, 89, 182)));

        // Recent-activity section
        JPanel activitySection = new JPanel(new BorderLayout());
        activitySection.setBackground(Color.WHITE);
        activitySection.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(0, 20, 20, 20),
                BorderFactory.createLineBorder(new Color(220, 220, 220))));

        JLabel actLabel = new JLabel("Recent Activity  (last 10 entries)");
        actLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        actLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        activitySection.add(actLabel, BorderLayout.NORTH);
        activitySection.add(new JScrollPane(activityTable), BorderLayout.CENTER);

        // Center area
        JPanel center = new JPanel(new BorderLayout(0, 0));
        center.setBackground(new Color(245, 245, 248));
        center.add(kpiRow,           BorderLayout.NORTH);
        center.add(activitySection,  BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    // ── KPI card factory ──────────────────────────────────────────────────

    private static JPanel makeKpiCard(String label, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(12, 14, 12, 14)));

        // Top colour stripe
        JPanel stripe = new JPanel();
        stripe.setBackground(accent);
        stripe.setPreferredSize(new Dimension(0, 4));
        card.add(stripe, BorderLayout.NORTH);

        JLabel lbl = new JLabel("<html>" + label + "</html>");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setForeground(new Color(110, 110, 110));
        card.add(lbl, BorderLayout.CENTER);

        valueLabel.setForeground(accent);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        card.add(valueLabel, BorderLayout.SOUTH);

        return card;
    }

    private static JLabel makeKpiValue(String initial) {
        JLabel lbl = new JLabel(initial);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 26));
        return lbl;
    }

    // ── Data loading (off-EDT) ────────────────────────────────────────────

    void loadData() {
        new SwingWorker<DashboardData, Void>() {
            @Override
            protected DashboardData doInBackground() {
                DashboardData d = new DashboardData();

                d.totalEmployees = queryCount(
                    "SELECT COUNT(*) FROM employee " +
                    "WHERE status_id = (SELECT status_id FROM employment_status " +
                    "                   WHERE status_name = 'REGULAR')");

                try {
                    PayrollPeriod op = new PayrollPeriodDAO().getOpenPeriod();
                    d.openPeriodName = (op != null) ? op.getPeriodName() : "None";
                } catch (Exception ex) {
                    d.openPeriodName = "Error";
                }

                d.pendingLeave = queryCount(
                    "SELECT COUNT(*) FROM leave_request " +
                    "WHERE status_id = (SELECT status_id FROM approval_status " +
                    "                   WHERE status_name = 'Pending')");

                d.totalPayroll = queryCount("SELECT COUNT(*) FROM payroll_record");

                List<AuditLog> all = new AuditLogDAO().getAll();
                d.recentActivity = all.stream()
                        .limit(10)
                        .collect(Collectors.toList());

                return d;
            }

            @Override
            protected void done() {
                try {
                    DashboardData d = get();
                    lblTotalEmployees.setText(String.valueOf(d.totalEmployees));
                    // Period name can be long — shrink font if needed
                    if (d.openPeriodName.length() > 12) {
                        lblOpenPeriod.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else {
                        lblOpenPeriod.setFont(new Font("Segoe UI", Font.BOLD, 26));
                    }
                    lblOpenPeriod.setText(d.openPeriodName);
                    lblPendingLeave.setText(String.valueOf(d.pendingLeave));
                    lblTotalPayroll.setText(String.valueOf(d.totalPayroll));

                    activityModel.setRowCount(0);
                    for (AuditLog a : d.recentActivity) {
                        String ts = (a.getPerformedAt() != null)
                                ? a.getPerformedAt().format(TS_FMT) : "";
                        activityModel.addRow(new Object[]{
                            ts,
                            a.getPerformedBy(),
                            a.getAction(),
                            a.getDetails()
                        });
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    setKpiError();
                } catch (ExecutionException ex) {
                    setKpiError();
                }
            }
        }.execute();
    }

    private void setKpiError() {
        lblTotalEmployees.setText("Err");
        lblOpenPeriod.setText("Err");
        lblPendingLeave.setText("Err");
        lblTotalPayroll.setText("Err");
    }

    // ── DB helper ─────────────────────────────────────────────────────────

    private static int queryCount(String sql) {
        try (Connection conn = Database.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException ex) {
            System.err.println("[DashboardPanel] queryCount failed: " + ex.getMessage());
        }
        return 0;
    }

    // ── Internal data holder ──────────────────────────────────────────────

    private static class DashboardData {
        int totalEmployees = 0;
        String openPeriodName = "None";
        int pendingLeave = 0;
        int totalPayroll = 0;
        List<AuditLog> recentActivity = Collections.emptyList();
    }
}
