package ui;

import Model.Permission;
import Services.SessionManager;
import Model.UserAccount;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 * Left-side navigation sidebar panel.
 * Displays company branding, logged-in user info, and role-filtered nav buttons.
 */
public class SidebarPanel extends JPanel {

    // ── colour constants ───────────────────────────────────────────────────
    private static final Color SIDEBAR_BG    = new Color(44,  62,  80);
    private static final Color BTN_NORMAL_BG = new Color(44,  62,  80);
    private static final Color BTN_HOVER_BG  = new Color(52,  73,  94);
    private static final Color BTN_ACTIVE_BG = new Color(26, 188, 156);
    private static final Color BTN_FG        = Color.WHITE;
    private static final Color USER_INFO_FG  = new Color(189, 195, 199);
    private static final Color SEPARATOR_FG  = new Color(80, 100, 120);

    private final List<JButton>  navButtons = new ArrayList<>();
    private final ActionListener navigationListener;

    // ─────────────────────────────────────────────────────────────────────
    /**
     * @param navigationListener receives ActionEvents when a nav item is clicked.
     *        The action command matches one of the constants used by MainShell
     *        (e.g. "DASHBOARD", "EMPLOYEES", "PAYROLL", "LOGOUT", …).
     */
    public SidebarPanel(ActionListener navigationListener) {
        this.navigationListener = navigationListener;

        setPreferredSize(new Dimension(200, 0));
        setBackground(SIDEBAR_BG);
        setLayout(new BorderLayout());

        add(buildHeader(),  BorderLayout.NORTH);
        add(buildNavArea(), BorderLayout.CENTER);
        add(buildFooter(),  BorderLayout.SOUTH);
    }

    // ── Header (company name + user info) ─────────────────────────────────

    private JPanel buildHeader() {
        JPanel header = new JPanel();
        header.setBackground(SIDEBAR_BG);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 15));

        JLabel companyLabel = new JLabel("MotorPH");
        companyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        companyLabel.setForeground(Color.WHITE);
        companyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(companyLabel);

        header.add(Box.createVerticalStrut(8));

        UserAccount user = SessionManager.getInstance().getCurrentUser();
        String displayName = (user != null) ? user.getUsername() : "Guest";
        String roleName    = (user != null && user.getRole() != null)
                ? user.getRole().getDisplayName() : "";

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        nameLabel.setForeground(USER_INFO_FG);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(nameLabel);

        JLabel roleLabel = new JLabel(roleName);
        roleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        roleLabel.setForeground(USER_INFO_FG);
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.add(roleLabel);

        header.add(Box.createVerticalStrut(12));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(SEPARATOR_FG);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        header.add(sep);

        return header;
    }

    // ── Navigation area (role-filtered buttons) ───────────────────────────

    private JPanel buildNavArea() {
        JPanel nav = new JPanel();
        nav.setBackground(SIDEBAR_BG);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        SessionManager sm = SessionManager.getInstance();

        // Dashboard — always visible to every logged-in user
        addNavButton(nav, "Dashboard",      "DASHBOARD");

        // Employees — VIEW_ALL_EMPLOYEES
        if (sm.hasPermission(Permission.VIEW_ALL_EMPLOYEES)) {
            addNavButton(nav, "Employees",  "EMPLOYEES");
        }

        // Attendance — VIEW_ATTENDANCE_REPORTS
        if (sm.hasPermission(Permission.VIEW_ATTENDANCE_REPORTS)) {
            addNavButton(nav, "Attendance", "ATTENDANCE");
        }

        // Payroll — PROCESS_PAYROLL, APPROVE_PAYROLL, or VIEW_ALL_PAYSLIPS
        if (sm.hasPermission(Permission.PROCESS_PAYROLL)
                || sm.hasPermission(Permission.APPROVE_PAYROLL)
                || sm.hasPermission(Permission.VIEW_ALL_PAYSLIPS)) {
            addNavButton(nav, "Payroll", "PAYROLL");
        }

        // Leave Management — CREATE_LEAVE_REQUEST, VIEW_ALL_LEAVE_REQUESTS, or APPROVE_LEAVE
        if (sm.hasPermission(Permission.CREATE_LEAVE_REQUEST)
                || sm.hasPermission(Permission.VIEW_ALL_LEAVE_REQUESTS)
                || sm.hasPermission(Permission.APPROVE_LEAVE)) {
            addNavButton(nav, "Leave Management", "LEAVE");
        }

        // Reports — GENERATE_REPORTS or VIEW_FINANCIAL_REPORTS
        if (sm.hasPermission(Permission.GENERATE_REPORTS)
                || sm.hasPermission(Permission.VIEW_FINANCIAL_REPORTS)) {
            addNavButton(nav, "Reports", "REPORTS");
        }

        // Audit Logs — VIEW_SYSTEM_LOGS
        if (sm.hasPermission(Permission.VIEW_SYSTEM_LOGS)) {
            addNavButton(nav, "Audit Logs", "AUDIT_LOGS");
        }

        // User Management — MANAGE_PERMISSIONS, CREATE_USER, or EDIT_USER
        if (sm.hasPermission(Permission.MANAGE_PERMISSIONS)
                || sm.hasPermission(Permission.CREATE_USER)
                || sm.hasPermission(Permission.EDIT_USER)) {
            addNavButton(nav, "User Management", "USER_MGMT");
        }

        return nav;
    }

    private void addNavButton(JPanel container, String text, String command) {
        JButton btn = new JButton(text);
        btn.setActionCommand(command);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(200, 40));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(BTN_FG);
        btn.setBackground(BTN_NORMAL_BG);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!BTN_ACTIVE_BG.equals(btn.getBackground()))
                    btn.setBackground(BTN_HOVER_BG);
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!BTN_ACTIVE_BG.equals(btn.getBackground()))
                    btn.setBackground(BTN_NORMAL_BG);
            }
        });

        btn.addActionListener(navigationListener);
        navButtons.add(btn);
        container.add(btn);
    }

    // ── Footer (logout) ───────────────────────────────────────────────────

    private JPanel buildFooter() {
        JPanel footer = new JPanel();
        footer.setBackground(SIDEBAR_BG);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(SEPARATOR_FG);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        footer.add(sep);

        footer.add(Box.createVerticalStrut(10));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setActionCommand("LOGOUT");
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        logoutBtn.setForeground(new Color(231, 76, 60));
        logoutBtn.setBackground(BTN_NORMAL_BG);
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setHorizontalAlignment(SwingConstants.LEFT);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(navigationListener);

        footer.add(logoutBtn);
        return footer;
    }

    // ── Public API ────────────────────────────────────────────────────────

    /**
     * Visually highlights the active navigation button and restores the rest.
     *
     * @param command the action command that is currently active
     *                (e.g. "DASHBOARD", "EMPLOYEES", …)
     */
    public void highlightActive(String command) {
        for (JButton btn : navButtons) {
            if (command.equals(btn.getActionCommand())) {
                btn.setBackground(BTN_ACTIVE_BG);
            } else {
                btn.setBackground(BTN_NORMAL_BG);
            }
        }
    }
}
