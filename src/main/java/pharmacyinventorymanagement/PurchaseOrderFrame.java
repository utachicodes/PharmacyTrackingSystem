package pharmacyinventorymanagement;

// Purchase order screen — create POs and receive stock atomically via a two-step JDBC transaction.

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PurchaseOrderFrame extends javax.swing.JFrame {

    // ── Design constants ──────────────────────────────────────────────────────
    private static final Color SIDEBAR_BG    = new Color(30, 41, 59);
    private static final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    private static final Color ACCENT        = new Color(16, 185, 129);
    private static final Color ACCENT_DARK   = new Color(5, 150, 105);
    private static final Color CONTENT_BG    = new Color(241, 245, 249);
    private static final Color TEXT_DARK     = new Color(30, 41, 59);
    private static final Color TEXT_MUTED    = new Color(100, 116, 139);
    private static final Color BORDER_CLR    = new Color(203, 213, 225);

    private String userRole;

    // ── UI fields ─────────────────────────────────────────────────────────────
    private JTextField txtMedName, txtSupplier, txtQty;
    private JTable     poTable;
    private JButton    btnCreatePO, btnReceive, btnBack;

    public PurchaseOrderFrame()                           { this("Admin"); }
    public PurchaseOrderFrame(String role)                { this(role, null); }
    public PurchaseOrderFrame(String role, String prefill) {
        this.userRole = role;
        initComponents();
        loadPOs();
        if (prefill != null && !prefill.isEmpty()) {
            txtMedName.setText(prefill);
            txtQty.setText("50");
            txtQty.requestFocusInWindow();
        }
    }

    // ── Business logic (unchanged) ────────────────────────────────────────────
    private void loadPOs() {
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM PURCHASE_ORDERS")) {
            poTable.setModel(DatabaseHelper.resultSetToTableModel(rs));
            applyStatusRenderer();
            if (poRowCount != null) poRowCount.setText("  " + poTable.getRowCount() + " orders  ");
            setPoColumnWidths();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void setPoColumnWidths() {
        if (poTable.getColumnCount() < 5) return;
        int[] widths = {60, 180, 160, 70, 90, 100};
        for (int i = 0; i < Math.min(widths.length, poTable.getColumnCount()); i++)
            poTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private void createPO() {
        if (txtMedName.getText().isEmpty() || txtSupplier.getText().isEmpty() || txtQty.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields."); return;
        }
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO PURCHASE_ORDERS (PO_MED_NAME, PO_SUPPLIER, PO_QTY, PO_DATE) VALUES (?,?,?,?)")) {
            ps.setString(1, txtMedName.getText());
            ps.setString(2, txtSupplier.getText());
            ps.setInt(3, Integer.parseInt(txtQty.getText()));
            ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            ps.executeUpdate();
            String createdFor = txtMedName.getText();
            loadPOs();
            txtMedName.setText(""); txtSupplier.setText(""); txtQty.setText("");
            if (poStatusBar != null) poStatusBar.setText("PO created for: " + createdFor);
            JOptionPane.showMessageDialog(this, "Purchase Order created.");
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void receiveStock() {
        int row = poTable.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a PO row first."); return; }
        int    poId    = (int)    poTable.getValueAt(row, 0);
        String medName = (String) poTable.getValueAt(row, 1);
        int    qty     = (int)    poTable.getValueAt(row, 3);
        String status  = (String) poTable.getValueAt(row, 4);
        if ("Received".equals(status)) { JOptionPane.showMessageDialog(this, "Already received."); return; }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Receive " + qty + " units of \"" + medName + "\" from this PO?\nThis will update inventory stock.",
            "Confirm Stock Receipt", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DatabaseHelper.getConnection()) {
            // Disable auto-commit so both updates happen as a single atomic transaction
            conn.setAutoCommit(false);
            try {
                // Step 1: mark the PO as Received
                try (PreparedStatement upPO = conn.prepareStatement(
                        "UPDATE PURCHASE_ORDERS SET PO_STATUS='Received' WHERE PO_ID=?")) {
                    upPO.setInt(1, poId); upPO.executeUpdate();
                }
                // Step 2: increment the medicine stock
                try (PreparedStatement upMed = conn.prepareStatement(
                        "UPDATE MEDICINE SET M_QUANTITY = M_QUANTITY + ? WHERE M_NAME = ?")) {
                    upMed.setInt(1, qty); upMed.setString(2, medName);
                    if (upMed.executeUpdate() == 0)
                        JOptionPane.showMessageDialog(this, "Warning: medicine not found in inventory.");
                }
                // Both succeeded — commit together
                conn.commit(); loadPOs();
                JOptionPane.showMessageDialog(this, "Stock received and inventory updated.");
            } catch (SQLException e) {
                // One step failed — roll back both so inventory stays consistent
                conn.rollback(); throw e;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // ── Status badge renderer ─────────────────────────────────────────────────
    private void applyStatusRenderer() {
        int statusCol = -1;
        for (int i = 0; i < poTable.getColumnCount(); i++)
            if ("PO_STATUS".equalsIgnoreCase(poTable.getColumnName(i))) { statusCol = i; break; }
        if (statusCol < 0) return;
        final int sc = statusCol;
        poTable.getColumnModel().getColumn(sc).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(
                    JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String s = v == null ? "" : v.toString();
                if (!sel) {
                    if ("Received".equalsIgnoreCase(s)) {
                        c.setBackground(new Color(209, 250, 229)); c.setForeground(new Color(6, 95, 70));
                    } else {
                        c.setBackground(new Color(255, 237, 213)); c.setForeground(new Color(154, 52, 18));
                    }
                }
                c.setFont(new Font("Segoe UI", Font.BOLD, 11));
                c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
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
        setTitle("Purchase Orders – Pharmacy System");
        setSize(1050, 680);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);
        setContentPane(root);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
    }

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

        JLabel activeLabel = new JLabel("  Purchase Orders");
        activeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        activeLabel.setForeground(ACCENT);
        activeLabel.setBackground(new Color(6, 78, 59));
        activeLabel.setOpaque(true);
        activeLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 8));
        activeLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        PharmIcons.apply(activeLabel, "po");
        sidebar.add(activeLabel);
        sidebar.add(sep());

        boolean isPharm = "Pharmacist".equalsIgnoreCase(userRole);

        String[][] items = {
            {"Dashboard",       "dash"},
            {"Medicines",       "med"},
            {"Agents",          "agents"},
            {"Suppliers",       "comp"},
            {"Billing",         "sell"},
            {"Sales History",   "sales"},
            {"Reports",         "reports"}
        };
        for (String[] item : items) {
            final String key = item[1];
            boolean restricted = isPharm && key.equals("agents");
            JLabel nav = navLabel(item[0], restricted);
            PharmIcons.apply(nav, key);
            if (!restricted) {
                nav.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        java.awt.Rectangle b = getBounds();
                        JFrame next = null;
                        switch (key) {
                            case "dash":    next = new DashboardFrame(userRole);      break;
                            case "med":     next = new MedicineFrame(userRole);       break;
                            case "agents":  next = new AgentsFrame(userRole);         break;
                            case "comp":    next = new CompanyFrame(userRole);        break;
                            case "sell":    next = new SellingFrame(userRole);        break;
                            case "sales":   next = new SalesHistoryFrame(userRole);   break;
                            case "reports": next = new ReportsFrame(userRole);        break;
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

    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(CONTENT_BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)));
        JLabel title = new JLabel("Purchase Orders & Receiving");
        title.setIcon(PharmIcons.hdr("po"));
        title.setIconTextGap(9);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("Create orders and receive stock atomically");
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
        body.add(buildFormCard(), BorderLayout.NORTH);
        body.add(buildTablePanel(), BorderLayout.CENTER);
        body.add(buildActionBar(), BorderLayout.SOUTH);
        content.add(body, BorderLayout.CENTER);
        content.add(buildStatusBar(), BorderLayout.SOUTH);
        return content;
    }

    private JLabel poStatusBar;

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(248, 250, 252));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(4, 16, 4, 16)));
        poStatusBar = new JLabel("Ready — select a Pending PO and click Receive Stock");
        poStatusBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        poStatusBar.setForeground(TEXT_MUTED);
        bar.add(poStatusBar, BorderLayout.WEST);
        return bar;
    }

    private JPanel buildFormCard() {
        txtMedName = field(); txtSupplier = field(); txtQty = field();
        btnCreatePO = actionBtn("＋ Create PO", ACCENT, ACCENT_DARK);
        btnCreatePO.setToolTipText("Create a new purchase order for the specified medicine and supplier");
        btnCreatePO.addActionListener(e -> createPO());

        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        card.add(fLabel("Medicine:")); card.add(txtMedName);
        card.add(fLabel("Supplier:")); card.add(txtSupplier);
        card.add(fLabel("Quantity:")); card.add(txtQty);
        card.add(btnCreatePO);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(CONTENT_BG);
        JLabel formTitle = new JLabel("New Purchase Order");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formTitle.setForeground(TEXT_MUTED);
        formTitle.setBorder(BorderFactory.createEmptyBorder(0, 2, 6, 0));
        wrapper.add(formTitle, BorderLayout.NORTH);
        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildTablePanel() {
        poTable = new JTable();
        poTable.setAutoCreateRowSorter(true);
        poTable.setRowHeight(30);
        poTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        poTable.setGridColor(new Color(226, 232, 240));
        poTable.setShowVerticalLines(false);
        poTable.setSelectionBackground(new Color(209, 250, 229));
        poTable.setSelectionForeground(TEXT_DARK);
        poTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        poTable.getTableHeader().setBackground(new Color(241, 245, 249));
        poTable.getTableHeader().setForeground(TEXT_MUTED);
        poTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));
        poTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
        poTable.setFillsViewportHeight(true);

        JScrollPane scroll = new JScrollPane(poTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        poRowCount = new JLabel("  0 orders");
        poRowCount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        poRowCount.setForeground(TEXT_MUTED);

        JTextField poSearch = new JTextField();
        poSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        poSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        poSearch.putClientProperty("JTextField.placeholderText", "Search orders...");
        poSearch.setToolTipText("Filter purchase orders by any column");
        poSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterPO(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterPO(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterPO(); }
            private void filterPO() {
                javax.swing.table.TableRowSorter<javax.swing.table.TableModel> s =
                    new javax.swing.table.TableRowSorter<>(poTable.getModel());
                poTable.setRowSorter(s);
                String t = poSearch.getText().trim();
                s.setRowFilter(t.isEmpty() ? null : javax.swing.RowFilter.regexFilter("(?i)" + t));
                if (poRowCount != null) poRowCount.setText("  " + poTable.getRowCount() + " orders  ");
            }
        });

        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JLabel hdr = new JLabel("  Purchase Orders");
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hdr.setForeground(TEXT_MUTED);
        topBar.add(hdr, BorderLayout.WEST);
        topBar.add(poSearch, BorderLayout.CENTER);
        topBar.add(poRowCount, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "refresh");
        panel.getActionMap().put("refresh", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { loadPOs(); }
        });
        return panel;
    }

    private JLabel poRowCount;

    private JPanel buildActionBar() {
        btnReceive = actionBtn("✓ Receive Stock", ACCENT, ACCENT_DARK);
        btnBack    = actionBtn("← Dashboard",     new Color(100,116,139), new Color(71,85,105));
        btnReceive.setToolTipText("Mark selected PO as received and add quantity to inventory");
        btnBack.setToolTipText("Return to the main dashboard");
        btnReceive.addActionListener(e -> receiveStock());
        btnBack.addActionListener(e -> { java.awt.Rectangle b = getBounds(); DashboardFrame f = new DashboardFrame(userRole); f.setBounds(b); f.setVisible(true); dispose(); });

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setBackground(CONTENT_BG);
        bar.add(btnReceive); bar.add(btnBack);
        return bar;
    }

    // ── Styling helpers ───────────────────────────────────────────────────────
    private JTextField field() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setPreferredSize(new Dimension(160, 28));
        return f;
    }
    private JLabel fLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(TEXT_MUTED);
        return l;
    }
    private JButton actionBtn(String text, Color bg, Color hover) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(150, 32));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
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
        s.setForeground(new Color(51,65,85)); s.setBackground(new Color(51,65,85));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }
}
