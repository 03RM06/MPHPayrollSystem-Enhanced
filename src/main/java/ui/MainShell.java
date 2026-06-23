package ui;

import Model.UserAccount;
import Services.SessionManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * Primary application window — shown after a successful login.
 *
 * Layout:
 *   WEST   — SidebarPanel (fixed 200 px, dark)
 *   CENTER — contentArea JPanel (swapped by showPanel())
 *
 * Navigation commands (action commands from SidebarPanel):
 *   DASHBOARD, EMPLOYEES, ATTENDANCE, PAYROLL, LEAVE, REPORTS, AUDIT_LOGS,
 *   USER_MGMT, LOGOUT
 */
public class MainShell extends JFrame {

    private static final Logger logger =
            Logger.getLogger(MainShell.class.getName());

    private final SidebarPanel sidebar;
    private final JPanel       contentArea;

    // ─────────────────────────────────────────────────────────────────────
    public MainShell() {
        super("MotorPH Payroll System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1200, 750));

        contentArea = new JPanel(new BorderLayout());
        sidebar     = new SidebarPanel(this::onNavigate);

        setLayout(new BorderLayout());
        add(sidebar,     BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);

        // Open on Dashboard
        showPanel("DASHBOARD");

        pack();
        setLocationRelativeTo(null);
    }

    // ── Navigation handler ────────────────────────────────────────────────

    private void onNavigate(ActionEvent e) {
        String cmd = e.getActionCommand();
        if ("LOGOUT".equals(cmd)) {
            doLogout();
        } else {
            showPanel(cmd);
        }
    }

    /**
     * Swaps the center content area to the panel corresponding to {@code command}.
     * Also highlights the matching nav button in the sidebar.
     */
    public void showPanel(String command) {
        sidebar.highlightActive(command);

        JPanel panel = buildPanel(command);
        contentArea.removeAll();
        contentArea.add(panel, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
    }

    // ── Panel factory ─────────────────────────────────────────────────────

    private JPanel buildPanel(String command) {
        UserAccount user = SessionManager.getInstance().getCurrentUser();

        switch (command) {
            case "DASHBOARD":
                return new DashboardPanel();

            case "EMPLOYEES":
                return new EmployeePage(user);

            case "PAYROLL":
                return new PayrollHistoryPanel();

            case "AUDIT_LOGS":
                return new AuditLogPanel();

            case "USER_MGMT":
                return new UserManagementPanel();

            case "LEAVE":
                return buildLeavePanel(user);

            default:
                return buildPlaceholder(command);
        }
    }

    /**
     * LeaveManagement is a JFrame.  We extract its content pane and embed
     * it in a wrapper JPanel so it appears inline in MainShell.
     * The "Back" button inside LeaveManagement is a no-op in this context
     * (navigate via the sidebar instead).
     */
    private JPanel buildLeavePanel(UserAccount user) {
        LeaveManagement lm = new LeaveManagement(user, null);
        // getContentPane() returns the single JPanel that fills the JFrame.
        // Adding it to a new parent automatically removes it from the JFrame.
        Container cp = lm.getContentPane();
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(cp, BorderLayout.CENTER);
        return wrapper;
    }

    private static JPanel buildPlaceholder(String command) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(command + "  —  coming soon");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lbl.setForeground(new Color(150, 150, 150));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }

    // ── Logout ────────────────────────────────────────────────────────────

    private void doLogout() {
        SessionManager.getInstance().logout();
        dispose();
        new Login().setVisible(true);
    }
}
