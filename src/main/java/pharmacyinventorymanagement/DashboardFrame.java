package pharmacyinventorymanagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * DashboardFrame is the central navigation hub of the Pharmacy Tracking System.
 * It displays live inventory alerts (low-stock and near-expiry) sourced from
 * {@link ForecastingHelper}, applies role-based button permissions via
 * {@link #applyRolePermissions()}, and provides sidebar navigation to all
 * functional modules: Medicines, Agents, Suppliers, Billing, and Purchase Orders.
 *
 * @author Abdoullah Ndao
 */
public class DashboardFrame extends javax.swing.JFrame {

    // ── Design constants ──────────────────────────────────────────────────────
    private static final Color SIDEBAR_BG   = new Color(30, 41, 59);
    private static final Color SIDEBAR_HOVER= new Color(51, 65, 85);
    private static final Color ACCENT       = new Color(16, 185, 129);
    private static final Color CONTENT_BG   = new Color(241, 245, 249);
    private static final Color TEXT_DARK    = new Color(30, 41, 59);
    private static final Color WARNING_BG   = new Color(255, 251, 235);
    private static final Color DANGER_BG    = new Color(254, 242, 242);
    private static final Color WARNING_BORDER = new Color(251, 191, 36);
    private static final Color DANGER_BORDER  = new Color(239, 68, 68);

    private String userRole;

    public DashboardFrame() { this("Admin"); }

    public DashboardFrame(String role) {
        this.userRole = role;
        initComponents();
        loadAlerts();
        applyRolePermissions();
    }

    // ── Role-based access control ─────────────────────────────────────────────
    private void applyRolePermissions() {
        // Technician: restricted to Medicines and Billing only
        if ("Technician".equalsIgnoreCase(userRole)) {
            btnAgents.setEnabled(false);  btnAgents.setForeground(new Color(71, 85, 105));
            btnCompany.setEnabled(false); btnCompany.setForeground(new Color(71, 85, 105));
            btnPO.setEnabled(false);      btnPO.setForeground(new Color(71, 85, 105));
        // Pharmacist: full access except user management
        } else if ("Pharmacist".equalsIgnoreCase(userRole)) {
            btnAgents.setEnabled(false);  btnAgents.setForeground(new Color(71, 85, 105));
        }
        // Admin: no restrictions
    }

    // ── Alert panel population ────────────────────────────────────────────────
    private void loadAlerts() {
        List<String> stockAlerts = ForecastingHelper.getLowStockAlerts();
        List<String> expAlerts   = ForecastingHelper.getExpirationAlerts();
        DefaultListModel<String> model = new DefaultListModel<>();

        if (stockAlerts.isEmpty() && expAlerts.isEmpty()) {
            model.addElement("OK All stock levels healthy — no near-expiry items.");
        } else {
            for (String a : expAlerts)  model.addElement("EXPIRY: " + a);
            for (String a : stockAlerts) model.addElement("LOW STOCK: " + a);
        }

        double totalValue = ForecastingHelper.getInventoryValue();
        int    medCount   = ForecastingHelper.getMedicineCount();
        model.add(0, "─────────────────────────────────────────");
        model.add(0, "  Medicines: " + medCount + "   |   Inventory Value: $" + String.format("%.2f", totalValue));
        model.add(0, "─────────────────────────────────────────");
        alertList.setModel(model);
    }

    // ── UI construction ───────────────────────────────────────────────────────
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Pharmacy System — Dashboard");
        setSize(880, 580);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);
        setContentPane(root);

        // ── Sidebar ───────────────────────────────────────────────────────────
        JPanel sidebar = buildSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // ── Main content ──────────────────────────────────────────────────────
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(CONTENT_BG);
        root.add(content, BorderLayout.CENTER);

        // Header bar
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 64));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)
        ));

        JLabel headerTitle = new JLabel("Dashboard");
        headerTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerTitle.setForeground(TEXT_DARK);

        JLabel roleBadge = new JLabel("  " + userRole + "  ");
        roleBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        roleBadge.setForeground(ACCENT);
        roleBadge.setBackground(new Color(209, 250, 229));
        roleBadge.setOpaque(true);
        roleBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(167, 243, 208), 1),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)
        ));

        header.add(headerTitle, BorderLayout.WEST);
        header.add(roleBadge,   BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Alerts panel
        JPanel alertsWrapper = new JPanel(new BorderLayout());
        alertsWrapper.setBackground(CONTENT_BG);
        alertsWrapper.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel alertsHeader = new JPanel(new BorderLayout());
        alertsHeader.setBackground(CONTENT_BG);
        alertsHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        JLabel alertsTitle = new JLabel("Inventory Alerts & Forecasting");
        alertsTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        alertsTitle.setForeground(TEXT_DARK);

        JButton refreshBtn = new JButton("⟳ Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshBtn.setBackground(new Color(241, 245, 249));
        refreshBtn.setForeground(new Color(100, 116, 139));
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.setToolTipText("Reload inventory alerts from database");
        refreshBtn.addActionListener(e -> loadAlerts());

        alertsHeader.add(alertsTitle, BorderLayout.WEST);
        alertsHeader.add(refreshBtn, BorderLayout.EAST);
        alertsWrapper.add(alertsHeader, BorderLayout.NORTH);

        alertList = new JList<>();
        alertList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        alertList.setCellRenderer(new AlertCellRenderer());
        alertList.setBackground(Color.WHITE);

        JScrollPane scroll = new JScrollPane(alertList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        alertsWrapper.add(scroll, BorderLayout.CENTER);

        content.add(alertsWrapper, BorderLayout.CENTER);
    }

    // ── Sidebar builder ───────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo area
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(15, 23, 42));
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JLabel logo = new JLabel("⚕ PHARMA");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(ACCENT);
        logoPanel.add(logo, BorderLayout.CENTER);
        sidebar.add(logoPanel);

        // Divider
        sidebar.add(makeDivider());

        // Nav items
        btnMedicine = navLabel("💊  Medicines");
        btnAgents   = navLabel("👤  Agents");
        btnCompany  = navLabel("🏢  Suppliers");
        btnSelling  = navLabel("💳  Billing");
        btnPO       = navLabel("📦  Purchase Orders");

        btnMedicine.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnMedicine.isEnabled()) { new MedicineFrame().setVisible(true); dispose(); } }
        });
        btnAgents.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnAgents.isEnabled()) { new AgentsFrame().setVisible(true); dispose(); } }
        });
        btnCompany.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnCompany.isEnabled()) { new CompanyFrame().setVisible(true); dispose(); } }
        });
        btnSelling.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnSelling.isEnabled()) { new SellingFrame().setVisible(true); dispose(); } }
        });
        btnPO.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnPO.isEnabled()) { new PurchaseOrderFrame(userRole).setVisible(true); dispose(); } }
        });

        sidebar.add(btnMedicine);
        sidebar.add(btnAgents);
        sidebar.add(btnCompany);
        sidebar.add(btnSelling);
        sidebar.add(btnPO);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(makeDivider());

        // Logout
        btnLogout = navLabel("← Logout");
        btnLogout.setForeground(new Color(252, 165, 165));
        btnLogout.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new LoginFrame().setVisible(true); dispose(); }
        });
        sidebar.add(btnLogout);
        sidebar.add(Box.createVerticalStrut(8));

        return sidebar;
    }

    // ── Helper: sidebar nav label ─────────────────────────────────────────────
    private JLabel navLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(203, 213, 225));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 8));
        l.setOpaque(true);
        l.setBackground(SIDEBAR_BG);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        l.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (l.isEnabled()) l.setBackground(SIDEBAR_HOVER); }
            public void mouseExited(MouseEvent e)  { l.setBackground(SIDEBAR_BG); }
        });
        return l;
    }

    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        sep.setBackground(new Color(51, 65, 85));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    // ── Custom alert list cell renderer ──────────────────────────────────────
    private class AlertCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean hasFocus) {
            JLabel c = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
            String text = value == null ? "" : value.toString();
            c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0,
                    text.startsWith("LOW STOCK") ? DANGER_BORDER :
                    text.startsWith("EXPIRY")    ? WARNING_BORDER : new Color(226, 232, 240)),
                BorderFactory.createEmptyBorder(7, 10, 7, 8)
            ));
            if (!isSelected) {
                c.setBackground(
                    text.startsWith("LOW STOCK") ? DANGER_BG :
                    text.startsWith("EXPIRY")    ? WARNING_BG : Color.WHITE
                );
                c.setForeground(TEXT_DARK);
            }
            c.setFont(new Font("Segoe UI", text.startsWith("─") ? Font.BOLD : Font.PLAIN, 12));
            return c;
        }
    }

    // ── Field declarations (JLabel used for nav so setEnabled works visually) ─
    private JLabel btnMedicine;
    private JLabel btnAgents;
    private JLabel btnCompany;
    private JLabel btnSelling;
    private JLabel btnPO;
    private JLabel btnLogout;
    private JList<String> alertList;
}
