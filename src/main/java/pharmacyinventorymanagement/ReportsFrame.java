package pharmacyinventorymanagement;

// Reports screen — top-selling medicines, daily revenue, inventory summary, expiry overview.

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ReportsFrame extends javax.swing.JFrame {

    private static final Color SIDEBAR_BG    = new Color(30, 41, 59);
    private static final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    private static final Color ACCENT        = new Color(16, 185, 129);
    private static final Color ACCENT_DARK   = new Color(5, 150, 105);
    private static final Color CONTENT_BG    = new Color(241, 245, 249);
    private static final Color TEXT_DARK     = new Color(30, 41, 59);
    private static final Color TEXT_MUTED    = new Color(100, 116, 139);
    private static final Color BORDER_CLR    = new Color(203, 213, 225);
    private static final Color DANGER_BG     = new Color(255, 220, 220);
    private static final Color WARNING_BG    = new Color(255, 255, 204);

    private String userRole;

    private JTable topSellersTable, revenueTable, expiryTable;
    private JLabel statMeds, statRevenue, statOrders, statExpiring;

    public ReportsFrame() { this("Admin"); }

    public ReportsFrame(String role) {
        this.userRole = role;
        initComponents();
        loadAll();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (javax.swing.JOptionPane.showConfirmDialog(null, "Exit the application?", "Confirm Exit", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
        setTitle("Reports & Analytics – Pharmacy System");
        setSize(1100, 720);
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);
        setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(CONTENT_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)));
        JLabel title = new JLabel("Reports & Analytics");
        title.setIcon(PharmIcons.hdr("reports"));
        title.setIconTextGap(9);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("Inventory insights and sales performance");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setBackground(Color.WHITE);
        titleBox.add(title); titleBox.add(sub);

        JButton refreshBtn = new JButton("⟳ Refresh");
        refreshBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        refreshBtn.setBackground(new Color(241, 245, 249));
        refreshBtn.setForeground(TEXT_MUTED);
        refreshBtn.setBorderPainted(false);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> loadAll());

        header.add(titleBox, BorderLayout.CENTER);
        header.add(refreshBtn, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(CONTENT_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        body.add(buildStatCards(), BorderLayout.NORTH);
        body.add(buildTablesPanel(), BorderLayout.CENTER);
        content.add(body, BorderLayout.CENTER);
        return content;
    }

    // ── Top stat cards ────────────────────────────────────────────────────────
    private JPanel buildStatCards() {
        statMeds     = new JLabel("—");
        statRevenue  = new JLabel("—");
        statOrders   = new JLabel("—");
        statExpiring = new JLabel("—");

        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setBackground(CONTENT_BG);
        row.add(statCard("Medicines",     statMeds,     new Color(239, 246, 255), new Color(29, 78, 216)));
        row.add(statCard("Total Revenue", statRevenue,  new Color(240, 253, 244), new Color(21, 128, 61)));
        row.add(statCard("Pending POs",   statOrders,   new Color(255, 251, 235), new Color(146, 64, 14)));
        row.add(statCard("Expiring Soon", statExpiring, new Color(255, 241, 242), new Color(190, 18, 60)));
        return row;
    }

    private JPanel statCard(String title, JLabel valueLabel, Color bg, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.setForeground(accent);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accent);
        card.add(t, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    // ── Three side-by-side report tables ─────────────────────────────────────
    private JPanel buildTablesPanel() {
        topSellersTable = new JTable();
        revenueTable    = new JTable();
        expiryTable     = new JTable();
        styleTable(topSellersTable);
        styleTable(revenueTable);
        styleTable(expiryTable);

        JPanel panel = new JPanel(new GridLayout(1, 3, 12, 0));
        panel.setBackground(CONTENT_BG);
        panel.add(tableCard("🏆 Top Sellers (30 days)", topSellersTable));
        panel.add(tableCard("Revenue by Day (14 days)", revenueTable));
        panel.add(tableCard("Expiry Overview", expiryTable));
        return panel;
    }

    private JPanel tableCard(String title, JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        JLabel hdr = new JLabel(title);
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 12));
        hdr.setForeground(TEXT_DARK);
        topBar.add(hdr, BorderLayout.WEST);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        card.add(topBar, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ── Data loading ──────────────────────────────────────────────────────────
    private void loadAll() {
        loadStatCards();
        loadTopSellers();
        loadRevenueByDay();
        loadExpiryOverview();
    }

    private void loadStatCards() {
        try (Connection conn = DatabaseHelper.getConnection()) {
            try (Statement st = conn.createStatement()) {
                // Medicine count
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM MEDICINE");
                if (rs.next()) statMeds.setText(String.valueOf(rs.getInt(1)));
                rs.close();

                // Total revenue
                rs = st.executeQuery("SELECT SUM(S_TOTAL) FROM SALES");
                if (rs.next()) statRevenue.setText("$" + String.format("%.0f", rs.getDouble(1)));
                rs.close();

                // Pending POs
                rs = st.executeQuery("SELECT COUNT(*) FROM PURCHASE_ORDERS WHERE PO_STATUS='Pending'");
                if (rs.next()) statOrders.setText(String.valueOf(rs.getInt(1)));
                rs.close();

                // Expiring within 30 days
                rs = st.executeQuery(
                    "SELECT COUNT(*) FROM MEDICINE WHERE M_EXPDATE IS NOT NULL " +
                    "AND M_EXPDATE <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)");
                if (rs.next()) {
                    int n = rs.getInt(1);
                    statExpiring.setText(String.valueOf(n));
                    statExpiring.setForeground(n > 0 ? new Color(190, 18, 60) : new Color(21, 128, 61));
                }
                rs.close();
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadTopSellers() {
        String sql =
            "SELECT S_MED_NAME AS Medicine, SUM(S_QTY) AS Units_Sold, " +
            "SUM(S_TOTAL) AS Revenue " +
            "FROM SALES WHERE S_DATE >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
            "GROUP BY S_MED_NAME ORDER BY Units_Sold DESC LIMIT 10";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            topSellersTable.setModel(DatabaseHelper.resultSetToTableModel(rs));
            styleTable(topSellersTable);
            applyRevenueRenderer(topSellersTable, "Revenue");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadRevenueByDay() {
        String sql =
            "SELECT DATE_FORMAT(S_DATE, '%d %b') AS Date, " +
            "COUNT(*) AS Txns, SUM(S_TOTAL) AS Revenue " +
            "FROM SALES WHERE S_DATE >= DATE_SUB(CURDATE(), INTERVAL 14 DAY) " +
            "GROUP BY S_DATE ORDER BY S_DATE DESC";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            revenueTable.setModel(DatabaseHelper.resultSetToTableModel(rs));
            styleTable(revenueTable);
            applyRevenueRenderer(revenueTable, "Revenue");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void loadExpiryOverview() {
        String sql =
            "SELECT M_NAME AS Medicine, M_QUANTITY AS Qty, M_EXPDATE AS Expires, " +
            "DATEDIFF(M_EXPDATE, CURDATE()) AS Days_Left " +
            "FROM MEDICINE WHERE M_EXPDATE IS NOT NULL " +
            "ORDER BY M_EXPDATE ASC LIMIT 20";
        try (Connection conn = DatabaseHelper.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            expiryTable.setModel(DatabaseHelper.resultSetToTableModel(rs));
            styleTable(expiryTable);
            applyExpiryRenderer();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void applyRevenueRenderer(JTable table, String colName) {
        for (int c = 0; c < table.getColumnCount(); c++) {
            if (colName.equalsIgnoreCase(table.getColumnName(c))) {
                final int col = c;
                table.getColumnModel().getColumn(col).setCellRenderer(new DefaultTableCellRenderer() {
                    public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col2) {
                        if (v != null) try { v = "$" + String.format("%.2f", Double.parseDouble(v.toString())); } catch (Exception ignored) {}
                        Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col2);
                        if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                        setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                        return c;
                    }
                });
                break;
            }
        }
    }

    private void applyExpiryRenderer() {
        int daysCol = -1;
        for (int c = 0; c < expiryTable.getColumnCount(); c++)
            if ("Days_Left".equalsIgnoreCase(expiryTable.getColumnName(c))) { daysCol = c; break; }
        if (daysCol < 0) return;
        final int dc = daysCol;
        expiryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) {
                    Object daysVal = t.getValueAt(row, dc);
                    try {
                        int days = Integer.parseInt(daysVal.toString());
                        c.setBackground(days < 0 ? new Color(255, 200, 200) : days < 15 ? DANGER_BG : days < 30 ? WARNING_BG : row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    } catch (Exception ignored) {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    }
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    private void styleTable(JTable t) {
        t.setRowHeight(28);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        t.setGridColor(new Color(226, 232, 240));
        t.setShowVerticalLines(false);
        t.setSelectionBackground(new Color(209, 250, 229));
        t.setSelectionForeground(TEXT_DARK);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        t.getTableHeader().setBackground(new Color(241, 245, 249));
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(185, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel logoArea = new JPanel(new BorderLayout());
        logoArea.setBackground(new Color(15, 23, 42));
        logoArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        logoArea.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 8));
        JLabel logo = new JLabel("⚕ PHARMA");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 15));
        logo.setForeground(ACCENT);
        logoArea.add(logo, BorderLayout.CENTER);
        sidebar.add(logoArea);
        sidebar.add(sep());

        JLabel active = new JLabel("  Reports");
        active.setFont(new Font("Segoe UI", Font.BOLD, 12));
        active.setForeground(ACCENT);
        active.setBackground(new Color(6, 78, 59));
        active.setOpaque(true);
        active.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 8));
        active.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        PharmIcons.apply(active, "reports");
        sidebar.add(active);
        sidebar.add(sep());

        boolean isTech  = "Technician".equalsIgnoreCase(userRole);
        boolean isPharm = "Pharmacist".equalsIgnoreCase(userRole);

        String[][] items = {
            {"Dashboard",       "dash"},
            {"Medicines",       "med"},
            {"Agents",          "agents"},
            {"Suppliers",       "comp"},
            {"Billing",         "sell"},
            {"Purchase Orders", "po"},
            {"Sales History",   "sales"}
        };
        for (String[] item : items) {
            final String key = item[1];
            boolean restricted =
                (isTech  && (key.equals("agents") || key.equals("comp") || key.equals("po"))) ||
                (isPharm && key.equals("agents"));
            JLabel nav = navLabel(item[0], restricted);
            PharmIcons.apply(nav, key);
            if (!restricted) {
                nav.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        java.awt.Rectangle b = getBounds();
                        JFrame next = null;
                        switch (key) {
                            case "dash":   next = new DashboardFrame(userRole);      break;
                            case "med":    next = new MedicineFrame(userRole);       break;
                            case "agents": next = new AgentsFrame(userRole);         break;
                            case "comp":   next = new CompanyFrame(userRole);        break;
                            case "sell":   next = new SellingFrame(userRole);        break;
                            case "po":     next = new PurchaseOrderFrame(userRole);  break;
                            case "sales":  next = new SalesHistoryFrame(userRole);   break;
                        }
                        if (next != null) { next.setBounds(b); next.setVisible(true); dispose(); }
                    }
                });
            }
            sidebar.add(nav);
        }
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(sep());
        JLabel logout = navLabel("Logout", false);
        PharmIcons.apply(logout, "logout");
        logout.setForeground(new Color(252, 165, 165));
        logout.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new LoginFrame().setVisible(true); dispose(); }
        });
        sidebar.add(logout);
        sidebar.add(Box.createVerticalStrut(8));
        return sidebar;
    }

    private JLabel navLabel(String text, boolean disabled) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(disabled ? new Color(71, 85, 105) : new Color(203, 213, 225));
        if (!disabled) l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.setBorder(BorderFactory.createEmptyBorder(11, 12, 11, 8));
        l.setOpaque(true); l.setBackground(SIDEBAR_BG);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        if (!disabled) {
            l.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { l.setBackground(SIDEBAR_HOVER); }
                public void mouseExited(MouseEvent e)  { l.setBackground(SIDEBAR_BG); }
            });
        }
        return l;
    }

    private JSeparator sep() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(51, 65, 85)); s.setBackground(new Color(51, 65, 85));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }
}
