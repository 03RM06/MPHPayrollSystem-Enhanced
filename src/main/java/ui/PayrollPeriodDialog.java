package ui;

import Model.PayrollPeriod;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.swing.*;

/**
 * Modal dialog for creating a new payroll period.
 * Returns a partially-filled PayrollPeriod on success (no periodId yet —
 * the DAO assigns that on INSERT).  Returns null if the user cancels.
 */
public class PayrollPeriodDialog extends JDialog {

    private boolean confirmed = false;
    private PayrollPeriod result;

    // ── Form fields ────────────────────────────────────────────────────────
    private final JTextField  tfPeriodName = new JTextField(24);
    private final JDateChooser dcStart      = new JDateChooser();
    private final JDateChooser dcEnd        = new JDateChooser();

    // ── Constructor ────────────────────────────────────────────────────────

    public PayrollPeriodDialog(Frame owner) {
        super(owner, "New Payroll Period", true);
        buildUi();
        setDefaults();
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    // ── Static factory ─────────────────────────────────────────────────────

    /**
     * Shows the dialog modally and returns the filled PayrollPeriod, or null
     * if the user cancelled.
     */
    public static PayrollPeriod showDialog(Component parent) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        Frame  frame = (owner instanceof Frame) ? (Frame) owner : null;
        PayrollPeriodDialog dlg = new PayrollPeriodDialog(frame);
        dlg.setVisible(true);
        return dlg.confirmed ? dlg.result : null;
    }

    // ── UI construction ────────────────────────────────────────────────────

    private void buildUi() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ---- form panel (GridBagLayout) ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 4, 16));
        GridBagConstraints lc = labelConstraints();
        GridBagConstraints fc = fieldConstraints();

        lc.gridy = 0; fc.gridy = 0;
        form.add(new JLabel("Period Name:"), lc);
        form.add(tfPeriodName, fc);

        lc.gridy = 1; fc.gridy = 1;
        form.add(new JLabel("Start Date:"), lc);
        form.add(dcStart, fc);

        lc.gridy = 2; fc.gridy = 2;
        form.add(new JLabel("End Date:"), lc);
        form.add(dcEnd, fc);

        // ---- button panel ----
        JButton btnSave   = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.setPreferredSize(new Dimension(80, 28));
        btnCancel.setPreferredSize(new Dimension(80, 28));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttons.add(btnSave);
        buttons.add(btnCancel);

        btnSave.addActionListener(e -> onSave());
        btnCancel.addActionListener(e -> dispose());

        // ---- root ----
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(form,    BorderLayout.CENTER);
        getContentPane().add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnSave);
    }

    private void setDefaults() {
        LocalDate today = LocalDate.now();
        // Default: first day of current month → last day
        dcStart.setDate(toDate(today.withDayOfMonth(1)));
        dcEnd.setDate(toDate(today));
    }

    // ── Event handlers ─────────────────────────────────────────────────────

    private void onSave() {
        String name = tfPeriodName.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Period name is required.", "Validation", JOptionPane.WARNING_MESSAGE);
            tfPeriodName.requestFocus();
            return;
        }

        Date rawStart = dcStart.getDate();
        Date rawEnd   = dcEnd.getDate();
        if (rawStart == null || rawEnd == null) {
            JOptionPane.showMessageDialog(this,
                "Both start and end dates are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate start = toLocalDate(rawStart);
        LocalDate end   = toLocalDate(rawEnd);
        if (start.isAfter(end)) {
            JOptionPane.showMessageDialog(this,
                "Start date must not be after end date.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        result = new PayrollPeriod(0, name, start, end, "OPEN");
        confirmed = true;
        dispose();
    }

    // ── Layout helpers ─────────────────────────────────────────────────────

    private static GridBagConstraints labelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx  = 0;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(6, 0, 6, 8);
        return c;
    }

    private static GridBagConstraints fieldConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx   = 1;
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        c.insets  = new Insets(6, 0, 6, 0);
        return c;
    }

    // ── Date conversion helpers ────────────────────────────────────────────

    private static Date toDate(LocalDate d) {
        return java.sql.Date.valueOf(d);
    }

    private static LocalDate toLocalDate(Date d) {
        // java.sql.Date.toInstant() throws UnsupportedOperationException;
        // handle it explicitly to support both java.util.Date and java.sql.Date.
        if (d instanceof java.sql.Date) {
            return ((java.sql.Date) d).toLocalDate();
        }
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
