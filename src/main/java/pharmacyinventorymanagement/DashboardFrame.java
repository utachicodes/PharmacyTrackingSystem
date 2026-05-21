package pharmacyinventorymanagement;

// Main navigation hub — shows live inventory alerts and routes to all modules by role.

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

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
        // Technician: restricted to Medicines, Billing, and Sales History only
        if ("Technician".equalsIgnoreCase(userRole)) {
            btnAgents.setEnabled(false);    btnAgents.setForeground(new Color(71, 85, 105));
            btnCompany.setEnabled(false);   btnCompany.setForeground(new Color(71, 85, 105));
            btnPO.setEnabled(false);        btnPO.setForeground(new Color(71, 85, 105));
            btnReports.setEnabled(false);   btnReports.setForeground(new Color(71, 85, 105));
        // Pharmacist: full access except user management
        } else if ("Pharmacist".equalsIgnoreCase(userRole)) {
            btnAgents.setEnabled(false);    btnAgents.setForeground(new Color(71, 85, 105));
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

        // Pin a summary row at the top of the alert list
        double totalValue = ForecastingHelper.getInventoryValue();
        int    medCount   = ForecastingHelper.getMedicineCount();
        model.add(0, "─────────────────────────────────────────");
        model.add(0, "  Medicines: " + medCount + "   |   Inventory Value: $" + String.format("%.2f", totalValue));
        model.add(0, "─────────────────────────────────────────");
        alertList.setModel(model);
    }

    // ── UI construction ───────────────────────────────────────────────────────
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (javax.swing.JOptionPane.showConfirmDialog(null, "Exit the application?", "Confirm Exit", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
        setTitle("PharmTrack — Dashboard");
        setSize(920, 600);
        setMinimumSize(new Dimension(780, 500));
        setLocationRelativeTo(null);

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

        JLabel dateLbl = new JLabel(new java.text.SimpleDateFormat("EEE, dd MMM yyyy").format(new java.util.Date()));
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLbl.setForeground(new Color(100, 116, 139));

        JPanel headerRight = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 12, 0));
        headerRight.setBackground(Color.WHITE);
        headerRight.add(dateLbl);
        headerRight.add(roleBadge);

        header.add(headerTitle, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);
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

        // Double-click a LOW STOCK alert to open a pre-filled Purchase Order
        alertList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String val = alertList.getSelectedValue();
                    if (val != null && val.startsWith("LOW STOCK:")) {
                        String medName = val.substring("LOW STOCK:".length()).trim();
                        int colon = medName.indexOf(" (");
                        if (colon > 0) medName = medName.substring(0, colon).trim();
                        openPrefilledPO(medName);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(alertList);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        alertsWrapper.add(scroll, BorderLayout.CENTER);

        // "Create PO" hint bar at the bottom of alerts panel
        JPanel hintBar = new JPanel(new BorderLayout());
        hintBar.setBackground(new Color(248, 250, 252));
        hintBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        JLabel hint = new JLabel("Tip: Double-click a LOW STOCK alert to create a Purchase Order instantly");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(100, 116, 139));
        hintBar.add(hint, BorderLayout.WEST);
        alertsWrapper.add(hintBar, BorderLayout.SOUTH);

        content.add(alertsWrapper, BorderLayout.CENTER);
    }

    // ── Sidebar builder ───────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(185, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Logo area
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(15, 23, 42));
        logoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        JPanel logoContent = new JPanel(new java.awt.GridLayout(2, 1, 0, 0));
        logoContent.setBackground(new Color(15, 23, 42));
        JLabel logo = new JLabel("⚕ PHARMA");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(ACCENT);
        JLabel logoSub = new JLabel("Tracking System v1.0");
        logoSub.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        logoSub.setForeground(new Color(71, 85, 105));
        logoContent.add(logo);
        logoContent.add(logoSub);
        logoPanel.add(logoContent, BorderLayout.CENTER);
        sidebar.add(logoPanel);

        // Divider
        sidebar.add(makeDivider());

        // Nav items
        btnMedicine = navLabel("Medicines");      PharmIcons.apply(btnMedicine, "med");
        btnAgents   = navLabel("Agents");         PharmIcons.apply(btnAgents,   "agents");
        btnCompany  = navLabel("Suppliers");      PharmIcons.apply(btnCompany,  "comp");
        btnSelling  = navLabel("Billing");        PharmIcons.apply(btnSelling,  "sell");
        btnPO       = navLabel("Purchase Orders");PharmIcons.apply(btnPO,       "po");
        btnSalesHist= navLabel("Sales History");  PharmIcons.apply(btnSalesHist,"sales");
        btnReports  = navLabel("Reports");        PharmIcons.apply(btnReports,  "reports");
        btnMedicine.setToolTipText("Manage medicine inventory — add, edit, delete stock records");
        btnAgents.setToolTipText("Manage staff accounts and assign roles");
        btnCompany.setToolTipText("Manage supplier directory with lead times");
        btnSelling.setToolTipText("Process sales and generate invoices");
        btnPO.setToolTipText("Create and receive purchase orders atomically");
        btnSalesHist.setToolTipText("Browse all past transactions with date filters");
        btnReports.setToolTipText("Top sellers, revenue by day, expiry overview");

        btnMedicine.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnMedicine.isEnabled()) { java.awt.Rectangle b = getBounds(); MedicineFrame f = new MedicineFrame(userRole); f.setBounds(b); f.setVisible(true); dispose(); } }
        });
        btnAgents.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnAgents.isEnabled()) { java.awt.Rectangle b = getBounds(); AgentsFrame f = new AgentsFrame(userRole); f.setBounds(b); f.setVisible(true); dispose(); } }
        });
        btnCompany.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnCompany.isEnabled()) { java.awt.Rectangle b = getBounds(); CompanyFrame f = new CompanyFrame(userRole); f.setBounds(b); f.setVisible(true); dispose(); } }
        });
        btnSelling.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnSelling.isEnabled()) { java.awt.Rectangle b = getBounds(); SellingFrame f = new SellingFrame(userRole); f.setBounds(b); f.setVisible(true); dispose(); } }
        });
        btnPO.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnPO.isEnabled()) { java.awt.Rectangle b = getBounds(); PurchaseOrderFrame f = new PurchaseOrderFrame(userRole); f.setBounds(b); f.setVisible(true); dispose(); } }
        });
        btnSalesHist.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnSalesHist.isEnabled()) { java.awt.Rectangle b = getBounds(); SalesHistoryFrame f = new SalesHistoryFrame(userRole); f.setBounds(b); f.setVisible(true); dispose(); } }
        });
        btnReports.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { if (btnReports.isEnabled()) { java.awt.Rectangle b = getBounds(); ReportsFrame f = new ReportsFrame(userRole); f.setBounds(b); f.setVisible(true); dispose(); } }
        });

        sidebar.add(btnMedicine);
        sidebar.add(btnAgents);
        sidebar.add(btnCompany);
        sidebar.add(btnSelling);
        sidebar.add(btnPO);
        sidebar.add(btnSalesHist);
        sidebar.add(btnReports);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(makeDivider());

        // Logout
        btnLogout = navLabel("Logout");
        PharmIcons.apply(btnLogout, "logout");
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
        l.setBorder(BorderFactory.createEmptyBorder(11, 12, 11, 8));
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

    // ── One-click reorder: opens PurchaseOrderFrame pre-filled for the medicine ─
    private void openPrefilledPO(String medName) {
        if ("Technician".equalsIgnoreCase(userRole)) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Technicians cannot create purchase orders.", "Access Denied",
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        java.awt.Rectangle b = getBounds();
        PurchaseOrderFrame po = new PurchaseOrderFrame(userRole, medName);
        po.setBounds(b);
        po.setVisible(true);
        dispose();
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
    private JLabel btnSalesHist;
    private JLabel btnReports;
    private JLabel btnLogout;
    private JList<String> alertList;
}
