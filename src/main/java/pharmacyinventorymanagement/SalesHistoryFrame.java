package pharmacyinventorymanagement;

// Sales history screen — browse, filter by date range, and search all past transactions.

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class SalesHistoryFrame extends javax.swing.JFrame {

    private static final Color SIDEBAR_BG    = new Color(30, 41, 59);
    private static final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    private static final Color ACCENT        = new Color(16, 185, 129);
    private static final Color ACCENT_DARK   = new Color(5, 150, 105);
    private static final Color CONTENT_BG    = new Color(241, 245, 249);
    private static final Color TEXT_DARK     = new Color(30, 41, 59);
    private static final Color TEXT_MUTED    = new Color(100, 116, 139);
    private static final Color BORDER_CLR    = new Color(203, 213, 225);

    private String userRole;

    private JTable salesTable;
    private JTextField searchField;
    private com.toedter.calendar.JDateChooser dateFrom, dateTo;
    private JLabel rowCountLabel, totalRevenueLabel;

    public SalesHistoryFrame() { this("Admin"); }

    public SalesHistoryFrame(String role) {
        this.userRole = role;
        initComponents();
        loadSales(null, null, "");
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (javax.swing.JOptionPane.showConfirmDialog(null, "Exit the application?", "Confirm Exit", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
        setTitle("Sales History – Pharmacy System");
        setSize(1050, 680);
        setMinimumSize(new Dimension(900, 580));
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
        JLabel title = new JLabel("Sales History");
        title.setIcon(PharmIcons.hdr("sales"));
        title.setIconTextGap(9);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("Browse and filter all past transactions");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setBackground(Color.WHITE);
        titleBox.add(title); titleBox.add(sub);
        header.add(titleBox, BorderLayout.CENTER);
        content.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(CONTENT_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        body.add(buildFilterBar(), BorderLayout.NORTH);
        body.add(buildTablePanel(), BorderLayout.CENTER);
        content.add(body, BorderLayout.CENTER);
        content.add(buildStatusBar(), BorderLayout.SOUTH);
        return content;
    }

    private JPanel buildFilterBar() {
        dateFrom = new com.toedter.calendar.JDateChooser();
        dateTo   = new com.toedter.calendar.JDateChooser();
        styleDate(dateFrom); styleDate(dateTo);

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        searchField.putClientProperty("JTextField.placeholderText", "Search by medicine name...");
        searchField.setPreferredSize(new Dimension(220, 30));

        JButton btnFilter = btn("Apply Filter", ACCENT, ACCENT_DARK);
        btnFilter.setToolTipText("Filter sales by date range");
        btnFilter.addActionListener(e -> applyFilter());

        JButton btnClear = btn("Clear", new Color(100, 116, 139), new Color(71, 85, 105));
        btnClear.setToolTipText("Reset all filters");
        btnClear.addActionListener(e -> {
            dateFrom.setDate(null); dateTo.setDate(null); searchField.setText("");
            loadSales(null, null, "");
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { applyFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyFilter(); }
        });

        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        card.add(fLabel("From:")); card.add(dateFrom);
        card.add(fLabel("To:"));   card.add(dateTo);
        card.add(fLabel("Search:")); card.add(searchField);
        card.add(btnFilter); card.add(btnClear);

        totalRevenueLabel = new JLabel("Total: $0.00");
        totalRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        totalRevenueLabel.setForeground(ACCENT);
        totalRevenueLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CONTENT_BG);
        wrapper.add(card, BorderLayout.CENTER);
        wrapper.add(totalRevenueLabel, BorderLayout.EAST);
        return wrapper;
    }

    private void applyFilter() {
        java.util.Date from = dateFrom.getDate();
        java.util.Date to   = dateTo.getDate();
        java.sql.Date sqlFrom = from != null ? new java.sql.Date(from.getTime()) : null;
        java.sql.Date sqlTo   = to   != null ? new java.sql.Date(to.getTime())   : null;
        loadSales(sqlFrom, sqlTo, searchField.getText().trim());
    }

    private void loadSales(java.sql.Date from, java.sql.Date to, String search) {
        boolean hasSearch = search != null && !search.isEmpty();
        StringBuilder sql = new StringBuilder(
            "SELECT S_ID, S_MED_NAME, S_DATE, S_QTY, S_TOTAL, S_PRESCRIPTION FROM SALES WHERE 1=1");
        if (from      != null) sql.append(" AND S_DATE >= ?");
        if (to        != null) sql.append(" AND S_DATE <= ?");
        if (hasSearch)         sql.append(" AND S_MED_NAME LIKE ?");
        sql.append(" ORDER BY S_DATE DESC, S_ID DESC");

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (from      != null) ps.setDate(idx++, from);
            if (to        != null) ps.setDate(idx++, to);
            if (hasSearch)         ps.setString(idx++, "%" + search + "%");
            try (ResultSet rs = ps.executeQuery()) {
                salesTable.setModel(DatabaseHelper.resultSetToTableModel(rs));
            }
            salesTable.setAutoCreateRowSorter(true);
            styleTableColumns();
            updateSummary();
        } catch (SQLException e) {
            loadSalesFallback(from, to, search);
        }
    }

    private void loadSalesFallback(java.sql.Date from, java.sql.Date to, String search) {
        boolean hasSearch = search != null && !search.isEmpty();
        StringBuilder sql = new StringBuilder(
            "SELECT S_ID, S_MED_NAME, S_DATE, S_QTY, S_TOTAL FROM SALES WHERE 1=1");
        if (from      != null) sql.append(" AND S_DATE >= ?");
        if (to        != null) sql.append(" AND S_DATE <= ?");
        if (hasSearch)         sql.append(" AND S_MED_NAME LIKE ?");
        sql.append(" ORDER BY S_DATE DESC, S_ID DESC");

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int idx = 1;
            if (from      != null) ps.setDate(idx++, from);
            if (to        != null) ps.setDate(idx++, to);
            if (hasSearch)         ps.setString(idx++, "%" + search + "%");
            try (ResultSet rs = ps.executeQuery()) {
                salesTable.setModel(DatabaseHelper.resultSetToTableModel(rs));
            }
            salesTable.setAutoCreateRowSorter(true);
            styleTableColumns();
            updateSummary();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateSummary() {
        int rows = salesTable.getRowCount();
        double total = 0;
        int totalCol = -1;
        for (int c = 0; c < salesTable.getColumnCount(); c++)
            if ("S_TOTAL".equalsIgnoreCase(salesTable.getColumnName(c))) { totalCol = c; break; }
        if (totalCol >= 0) {
            for (int r = 0; r < rows; r++) {
                try { total += Double.parseDouble(salesTable.getValueAt(r, totalCol).toString()); }
                catch (Exception ignored) {}
            }
        }
        if (rowCountLabel    != null) rowCountLabel.setText("  " + rows + " records  ");
        if (totalRevenueLabel != null) totalRevenueLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private void styleTableColumns() {
        salesTable.setRowHeight(30);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        salesTable.setGridColor(new Color(226, 232, 240));
        salesTable.setShowVerticalLines(false);
        salesTable.setSelectionBackground(new Color(209, 250, 229));
        salesTable.setSelectionForeground(TEXT_DARK);
        salesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        salesTable.getTableHeader().setBackground(new Color(241, 245, 249));
        salesTable.getTableHeader().setForeground(TEXT_MUTED);
        salesTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));
        salesTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                String colName = t.getColumnName(col);
                if ("S_TOTAL".equalsIgnoreCase(colName) && v != null) {
                    try { setText("$" + String.format("%.2f", Double.parseDouble(v.toString()))); } catch (Exception ignored) {}
                }
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
        // Column widths
        int[] widths = {50, 200, 100, 60, 90, 140};
        for (int i = 0; i < Math.min(widths.length, salesTable.getColumnCount()); i++)
            salesTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private JPanel buildTablePanel() {
        salesTable = new JTable();
        JScrollPane scroll = new JScrollPane(salesTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        rowCountLabel = new JLabel("  0 records");
        rowCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rowCountLabel.setForeground(TEXT_MUTED);

        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JLabel hdr = new JLabel("  Transaction Log");
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hdr.setForeground(TEXT_MUTED);
        topBar.add(hdr, BorderLayout.WEST);
        topBar.add(rowCountLabel, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JLabel statusBar;

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(248, 250, 252));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(4, 16, 4, 16)));
        statusBar = new JLabel("Use the date filters above to narrow results. Click a column header to sort.");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.setForeground(TEXT_MUTED);
        bar.add(statusBar, BorderLayout.WEST);
        return bar;
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

        JLabel activeLabel = new JLabel("  Sales History");
        activeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        activeLabel.setForeground(ACCENT);
        activeLabel.setBackground(new Color(6, 78, 59));
        activeLabel.setOpaque(true);
        activeLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 8));
        activeLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        PharmIcons.apply(activeLabel, "sales");
        sidebar.add(activeLabel);
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
            {"Reports",         "reports"}
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
                            case "dash":    next = new DashboardFrame(userRole);       break;
                            case "med":     next = new MedicineFrame(userRole);        break;
                            case "agents":  next = new AgentsFrame(userRole);          break;
                            case "comp":    next = new CompanyFrame(userRole);         break;
                            case "sell":    next = new SellingFrame(userRole);         break;
                            case "po":      next = new PurchaseOrderFrame(userRole);   break;
                            case "reports": next = new ReportsFrame(userRole);         break;
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

    // ── Helpers ───────────────────────────────────────────────────────────────
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

    private JButton btn(String text, Color bg, Color hover) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 30));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }

    private JLabel fLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(TEXT_MUTED);
        return l;
    }

    private void styleDate(com.toedter.calendar.JDateChooser dc) {
        dc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dc.setPreferredSize(new Dimension(130, 30));
    }
}
