package ui;

import DAO.PayrollPeriodDAO;
import DAO.PayrollRecordDAO;
import Model.PayrollPeriod;
import Model.PayrollRecord;
import Services.PayrollService;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

/**
 * JPanel that displays payroll periods and their records.
 * Embed this panel inside the main application window or any JFrame/JTabbedPane.
 *
 * Layout:
 *   NORTH  — title label + action toolbar
 *   CENTER — JSplitPane
 *               top:    payroll periods table
 *               bottom: payroll records table (populated on "View Records")
 */
public class PayrollHistoryPanel extends JPanel {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");

    // ── Services / DAOs ────────────────────────────────────────────────────
    private final PayrollPeriodDAO periodDAO  = new PayrollPeriodDAO();
    private final PayrollRecordDAO recordDAO  = new PayrollRecordDAO();
    private final PayrollService   payService = new PayrollService();

    // ── Period table ───────────────────────────────────────────────────────
    private final DefaultTableModel periodTableModel;
    private final JTable            periodTable;

    // ── Record table + its titled border ──────────────────────────────────
    private final DefaultTableModel recordTableModel;
    private final JTable            recordTable;
    private       TitledBorder      recordsBorder;
    private       JPanel            recordsPanel;

    // ── Toolbar buttons ────────────────────────────────────────────────────
    private final JButton btnNewPeriod   = new JButton("New Period");
    private final JButton btnRunPayroll  = new JButton("Run Payroll");
    private final JButton btnClosePeriod = new JButton("Close Period");
    private final JButton btnViewRecords = new JButton("View Records");
    private final JButton btnRefresh     = new JButton("Refresh");

    // ── Currently loaded periods ───────────────────────────────────────────
    private List<PayrollPeriod> periods = new java.util.ArrayList<>();

    // ── Constructor ────────────────────────────────────────────────────────

    public PayrollHistoryPanel() {
        super(new BorderLayout(0, 4));

        // ---- Period table model ----
        periodTableModel = new DefaultTableModel(
                new Object[]{"#", "Period Name", "Start Date", "End Date", "Status"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        periodTable = new JTable(periodTableModel);
        periodTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        periodTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        periodTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        periodTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        periodTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        periodTable.getColumnModel().getColumn(4).setPreferredWidth(80);

        // ---- Record table model ----
        recordTableModel = new DefaultTableModel(
                new Object[]{"Employee ID", "Name", "Basic Salary", "Gross Pay",
                             "SSS", "PhilHealth", "Pag-IBIG", "W/H Tax",
                             "Total Deductions", "Net Pay", "Computed At"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        recordTable = new JTable(recordTableModel);
        recordTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        recordTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        recordTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        for (int i = 2; i <= 9; i++) recordTable.getColumnModel().getColumn(i).setPreferredWidth(100);
        recordTable.getColumnModel().getColumn(10).setPreferredWidth(140);

        buildUi();
        wireButtons();
        loadPeriods();
    }

    // ── UI construction ────────────────────────────────────────────────────

    private void buildUi() {
        // ---- NORTH: title + toolbar ----
        JLabel title = new JLabel("Payroll History & Period Management");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 15f));
        title.setBorder(BorderFactory.createEmptyBorder(8, 10, 4, 0));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        toolbar.add(btnNewPeriod);
        toolbar.add(btnRunPayroll);
        toolbar.add(btnClosePeriod);
        toolbar.add(btnViewRecords);
        toolbar.add(btnRefresh);

        JPanel north = new JPanel(new BorderLayout());
        north.add(title,   BorderLayout.NORTH);
        north.add(toolbar, BorderLayout.SOUTH);
        add(north, BorderLayout.NORTH);

        // ---- CENTER: split pane ----
        JPanel periodsPanel = new JPanel(new BorderLayout());
        periodsPanel.setBorder(BorderFactory.createTitledBorder("Payroll Periods"));
        periodsPanel.add(new JScrollPane(periodTable), BorderLayout.CENTER);

        recordsBorder = BorderFactory.createTitledBorder(
                "Payroll Records  (select a period and click \"View Records\")");
        recordsPanel = new JPanel(new BorderLayout());
        recordsPanel.setBorder(recordsBorder);
        recordsPanel.add(new JScrollPane(recordTable), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, periodsPanel, recordsPanel);
        split.setResizeWeight(0.40);
        split.setDividerLocation(200);
        add(split, BorderLayout.CENTER);

        // Initial button states — nothing selected yet
        btnRunPayroll.setEnabled(false);
        btnClosePeriod.setEnabled(false);
        btnViewRecords.setEnabled(false);
    }

    // ── Button wiring ──────────────────────────────────────────────────────

    private void wireButtons() {
        // Selection listener: enable/disable buttons based on selected row
        periodTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = periodTable.getSelectedRow();
            if (row < 0 || row >= periods.size()) {
                btnRunPayroll.setEnabled(false);
                btnClosePeriod.setEnabled(false);
                btnViewRecords.setEnabled(false);
                return;
            }
            PayrollPeriod p = periods.get(row);
            btnRunPayroll.setEnabled(p.isOpen());
            btnClosePeriod.setEnabled(p.isOpen());
            btnViewRecords.setEnabled(true);
        });

        btnNewPeriod.addActionListener(e   -> onNewPeriod());
        btnRunPayroll.addActionListener(e  -> onRunPayroll());
        btnClosePeriod.addActionListener(e -> onClosePeriod());
        btnViewRecords.addActionListener(e -> onViewRecords());
        btnRefresh.addActionListener(e     -> loadPeriods());
    }

    // ── Action handlers ────────────────────────────────────────────────────

    private void onNewPeriod() {
        PayrollPeriod p = PayrollPeriodDialog.showDialog(this);
        if (p == null) return;
        try {
            periodDAO.create(p);
            loadPeriods();
        } catch (SQLException ex) {
            showError("Failed to create payroll period: " + ex.getMessage());
        }
    }

    private void onRunPayroll() {
        int row = periodTable.getSelectedRow();
        if (row < 0 || row >= periods.size()) return;
        PayrollPeriod p = periods.get(row);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Run payroll for \"" + p.getPeriodName() + "\"?\n"
                + "This will compute and save records for all REGULAR employees.",
                "Confirm Run Payroll", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        btnRunPayroll.setEnabled(false);
        btnClosePeriod.setEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<List<PayrollRecord>, Void> worker = new SwingWorker<List<PayrollRecord>, Void>() {
            @Override
            protected List<PayrollRecord> doInBackground() throws Exception {
                return payService.processPayrollForPeriod(p.getPeriodId());
            }
            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    List<PayrollRecord> records = get();
                    JOptionPane.showMessageDialog(PayrollHistoryPanel.this,
                            "Payroll run complete. " + records.size() + " employee(s) processed.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    populateRecordTable(records, p.getPeriodName());
                } catch (ExecutionException ex) {
                    Throwable cause = ex.getCause();
                    showError("Payroll run failed: "
                            + (cause != null ? cause.getMessage() : ex.getMessage()));
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    showError("Payroll run was interrupted.");
                } finally {
                    // Restore button state from the (possibly refreshed) period list
                    int sel = periodTable.getSelectedRow();
                    if (sel >= 0 && sel < periods.size()) {
                        PayrollPeriod sel_p = periods.get(sel);
                        btnRunPayroll.setEnabled(sel_p.isOpen());
                        btnClosePeriod.setEnabled(sel_p.isOpen());
                    }
                }
            }
        };
        worker.execute();
    }

    private void onClosePeriod() {
        int row = periodTable.getSelectedRow();
        if (row < 0 || row >= periods.size()) return;
        PayrollPeriod p = periods.get(row);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Close period \"" + p.getPeriodName() + "\"?\n"
                + "A closed period cannot be re-opened.",
                "Confirm Close", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            periodDAO.closePeriod(p.getPeriodId());
            loadPeriods();
        } catch (SQLException ex) {
            showError("Failed to close period: " + ex.getMessage());
        }
    }

    private void onViewRecords() {
        int row = periodTable.getSelectedRow();
        if (row < 0 || row >= periods.size()) return;
        PayrollPeriod p = periods.get(row);
        try {
            List<PayrollRecord> records = recordDAO.getByPeriod(p.getPeriodId());
            populateRecordTable(records, p.getPeriodName());
        } catch (SQLException ex) {
            showError("Failed to load records: " + ex.getMessage());
        }
    }

    // ── Data loading ───────────────────────────────────────────────────────

    private void loadPeriods() {
        try {
            periods = periodDAO.getAll();
            periodTableModel.setRowCount(0);
            for (PayrollPeriod p : periods) {
                periodTableModel.addRow(new Object[]{
                    p.getPeriodId(),
                    p.getPeriodName(),
                    p.getStartDate() != null ? p.getStartDate().format(DATE_FMT) : "",
                    p.getEndDate()   != null ? p.getEndDate().format(DATE_FMT)   : "",
                    p.getStatus()
                });
            }
            btnRunPayroll.setEnabled(false);
            btnClosePeriod.setEnabled(false);
            btnViewRecords.setEnabled(false);
        } catch (SQLException ex) {
            showError("Failed to load payroll periods: " + ex.getMessage());
        }
    }

    private void populateRecordTable(List<PayrollRecord> records, String periodName) {
        recordsBorder.setTitle(
                "Payroll Records — " + periodName + "  (" + records.size() + " employee(s))");
        recordsPanel.repaint();

        recordTableModel.setRowCount(0);
        for (PayrollRecord r : records) {
            recordTableModel.addRow(new Object[]{
                r.getEmployeeId(),
                r.getEmployeeName(),
                String.format("%.2f", r.getBasicSalary()),
                String.format("%.2f", r.getGrossPay()),
                String.format("%.2f", r.getSssDeduction()),
                String.format("%.2f", r.getPhilhealthDeduction()),
                String.format("%.2f", r.getPagibigDeduction()),
                String.format("%.2f", r.getWithholdingTax()),
                String.format("%.2f", r.getTotalDeductions()),
                String.format("%.2f", r.getNetPay()),
                r.getComputedAt() != null
                        ? r.getComputedAt().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                        : ""
            });
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
