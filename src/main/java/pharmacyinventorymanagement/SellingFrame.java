package pharmacyinventorymanagement;

/**
 * SellingFrame provides the Point-of-Sale billing interface for the Pharmacy
 * Tracking System. It deducts stock from the MEDICINE table on each sale,
 * records every transaction to the SALES audit table for forecasting, and
 * generates a printable invoice with running totals.
 *
 * @author Abdoullah Ndao
 */

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SellingFrame extends javax.swing.JFrame {

    // ── Design constants ──────────────────────────────────────────────────────
    private static final Color SIDEBAR_BG    = new Color(30, 41, 59);
    private static final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    private static final Color ACCENT        = new Color(16, 185, 129);
    private static final Color ACCENT_DARK   = new Color(5, 150, 105);
    private static final Color CONTENT_BG    = new Color(241, 245, 249);
    private static final Color TEXT_DARK     = new Color(30, 41, 59);
    private static final Color TEXT_MUTED    = new Color(100, 116, 139);
    private static final Color BORDER_CLR    = new Color(203, 213, 225);
    private static final Color INVOICE_BG    = new Color(15, 23, 42);

    // ── JDBC & billing state ──────────────────────────────────────────────────
    Connection Con = null;
    Statement St = null;
    ResultSet Rs = null;
    double price = 0;
    int medId = 0, mQty = 0;
    int billID = 0;
    double billTotal = 0.0;

    // ── UI fields (referenced by business logic) ──────────────────────────────
    private JTextField b_id, b_medName, b_quantity;
    private JLabel date_text;
    private JTable medicine_table;
    private JTextArea b_textArea;
    private JButton btnAddToBill, btnClear, btnPrint;
    private JLabel totalLabel;

    public SellingFrame() {
        initComponents();
        setTitle("Billing – Pharmacy System");
        ShowDate();
        loadMedicines();
    }

    public void ShowDate() {
        date_text.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Billing – Pharmacy System");
        setSize(1150, 720);
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

        JLabel activeLabel = new JLabel("  💳 Billing");
        activeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        activeLabel.setForeground(ACCENT);
        activeLabel.setBackground(new Color(6, 78, 59));
        activeLabel.setOpaque(true);
        activeLabel.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 8));
        activeLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        sidebar.add(activeLabel);
        sidebar.add(sep());

        String[][] items = {
            {"🏠  Dashboard",  "dash"}, {"💊  Medicines",  "med"},
            {"👤  Agents",     "agents"}, {"🏢  Suppliers", "comp"},
            {"📦  Purchase Orders", "po"}
        };
        for (String[] item : items) {
            JLabel nav = navLabel(item[0]);
            final String key = item[1];
            nav.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    switch (key) {
                        case "dash":   new DashboardFrame().setVisible(true);     dispose(); break;
                        case "med":    new MedicineFrame().setVisible(true);      dispose(); break;
                        case "agents": new AgentsFrame().setVisible(true);        dispose(); break;
                        case "comp":   new CompanyFrame().setVisible(true);       dispose(); break;
                        case "po":     new PurchaseOrderFrame().setVisible(true); dispose(); break;
                    }
                }
            });
            sidebar.add(nav);
        }
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(sep());
        JLabel exit = navLabel("✕  Exit");
        exit.setForeground(new Color(252, 165, 165));
        exit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int c = JOptionPane.showConfirmDialog(SellingFrame.this, "Exit application?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (c == JOptionPane.YES_OPTION) System.exit(0);
            }
        });
        sidebar.add(exit);
        sidebar.add(Box.createVerticalStrut(8));
        return sidebar;
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
        JLabel title = new JLabel("💳  Billing & Sales");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        date_text = new JLabel();
        date_text.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        date_text.setForeground(TEXT_MUTED);
        header.add(title, BorderLayout.WEST);
        header.add(date_text, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        // Body: split left (stock table) | right (billing form + invoice)
        JPanel body = new JPanel(new BorderLayout(12, 0));
        body.setBackground(CONTENT_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        body.add(buildStockPanel(), BorderLayout.CENTER);
        body.add(buildBillingPanel(), BorderLayout.EAST);

        content.add(body, BorderLayout.CENTER);
        return content;
    }

    // ── Left: stock table ─────────────────────────────────────────────────────
    private JPanel buildStockPanel() {
        medicine_table = new JTable();
        styleTable(medicine_table);
        medicine_table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { medicine_tableMouseClicked(e); }
        });
        JScrollPane scroll = new JScrollPane(medicine_table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        stockRowCount = new JLabel("  0 items");
        stockRowCount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        stockRowCount.setForeground(TEXT_MUTED);

        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JLabel hdr = new JLabel("  Available Stock  (click a row to select)");
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hdr.setForeground(TEXT_MUTED);
        topBar.add(hdr, BorderLayout.WEST);
        topBar.add(stockRowCount, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JLabel stockRowCount;

    // ── Right: billing form + invoice ─────────────────────────────────────────
    private JPanel buildBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(CONTENT_BG);
        panel.setPreferredSize(new Dimension(400, 0));

        // Form card
        b_id       = field(); b_medName  = field(); b_quantity = field();
        btnAddToBill = actionBtn("＋ Add to Bill", ACCENT, ACCENT_DARK);
        btnClear     = actionBtn("⟳ Clear",        new Color(100,116,139), new Color(71,85,105));
        btnPrint     = actionBtn("🖨 Print",        new Color(59,130,246),  new Color(37,99,235));
        btnAddToBill.setToolTipText("Deduct selected quantity from stock and add to invoice");
        btnClear.setToolTipText("Clear invoice and reset bill total");
        btnPrint.setToolTipText("Print the current invoice");

        btnAddToBill.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { btnAddToBillMouseClicked(e); } });
        btnClear.addMouseListener(new MouseAdapter()     { public void mouseClicked(MouseEvent e) { btnClearMouseClicked(e); } });
        btnPrint.addMouseListener(new MouseAdapter()     { public void mouseClicked(MouseEvent e) { btnPrintMouseClicked(e); } });

        JPanel formCard = new JPanel(new GridBagLayout());
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(14, 16, 10, 16)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4); g.fill = GridBagConstraints.HORIZONTAL;
        g.gridy = 0; g.gridx = 0; g.weightx = 0; formCard.add(fLabel("Bill #"), g);
        g.gridx = 1; g.weightx = 1; formCard.add(b_id, g);
        g.gridy = 1; g.gridx = 0; g.weightx = 0; formCard.add(fLabel("Medicine"), g);
        g.gridx = 1; g.weightx = 1; formCard.add(b_medName, g);
        g.gridy = 2; g.gridx = 0; g.weightx = 0; formCard.add(fLabel("Quantity"), g);
        g.gridx = 1; g.weightx = 1; formCard.add(b_quantity, g);
        g.gridy = 3; g.gridx = 0; g.gridwidth = 2;
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btns.setBackground(Color.WHITE);
        btns.add(btnAddToBill); btns.add(btnClear);
        formCard.add(btns, g);

        // Invoice area
        b_textArea = new JTextArea();
        b_textArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        b_textArea.setBackground(INVOICE_BG);
        b_textArea.setForeground(ACCENT);
        b_textArea.setCaretColor(ACCENT);
        b_textArea.setText("*** PHARMA-EASY ***\n ID  Medicine      Price   Qty   Net");
        b_textArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane invoiceScroll = new JScrollPane(b_textArea);
        invoiceScroll.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85)));

        totalLabel = new JLabel("TOTAL: $0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalLabel.setForeground(ACCENT);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 4));

        JPanel invoiceWrapper = new JPanel(new BorderLayout());
        invoiceWrapper.setBackground(Color.WHITE);
        invoiceWrapper.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        JLabel invHdr = new JLabel("  Invoice");
        invHdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        invHdr.setForeground(TEXT_MUTED);
        invHdr.setPreferredSize(new Dimension(0, 34));
        invHdr.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));
        invoiceWrapper.add(invHdr, BorderLayout.NORTH);
        invoiceWrapper.add(invoiceScroll, BorderLayout.CENTER);
        JPanel invoiceSouth = new JPanel(new BorderLayout());
        invoiceSouth.setBackground(Color.WHITE);
        invoiceSouth.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));
        invoiceSouth.add(totalLabel, BorderLayout.CENTER);
        invoiceSouth.add(btnPrint, BorderLayout.EAST);
        invoiceWrapper.add(invoiceSouth, BorderLayout.SOUTH);

        panel.add(formCard, BorderLayout.NORTH);
        panel.add(invoiceWrapper, BorderLayout.CENTER);
        return panel;
    }

    // ── Styling helpers ───────────────────────────────────────────────────────
    private JTextField field() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        f.setPreferredSize(new Dimension(200, 28));
        return f;
    }
    private JLabel fLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(TEXT_MUTED);
        l.setPreferredSize(new Dimension(72, 28));
        return l;
    }
    private JButton actionBtn(String text, Color bg, Color hover) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(130, 32));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
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
        t.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable tbl, Object val, boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, val, sel, foc, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }
    private JLabel navLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        l.setForeground(new Color(203, 213, 225));
        l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        l.setBorder(BorderFactory.createEmptyBorder(11, 18, 11, 8));
        l.setOpaque(true); l.setBackground(SIDEBAR_BG);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        l.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { l.setBackground(SIDEBAR_HOVER); }
            public void mouseExited(MouseEvent e)  { l.setBackground(SIDEBAR_BG); }
        });
        return l;
    }
    private JSeparator sep() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(51,65,85)); s.setBackground(new Color(51,65,85));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    // ── Database operations ───────────────────────────────────────────────────
    public void loadMedicines() {
        try {
            Con = DatabaseHelper.getConnection();
            St  = Con.createStatement();
            Rs  = St.executeQuery("SELECT * FROM MEDICINE");
            medicine_table.setModel(DatabaseHelper.resultSetToTableModel(Rs));
            if (stockRowCount != null) stockRowCount.setText("  " + medicine_table.getRowCount() + " items  ");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage());
        }
    }
    @Deprecated public void SelectMed() { loadMedicines(); }

    public boolean updateQty() {
        int orderQty = Integer.parseInt(b_quantity.getText());
        if (mQty >= orderQty) {
            try {
                int newQty = mQty - orderQty;
                mQty = newQty;
                Con = DatabaseHelper.getConnection();
                try (PreparedStatement upd = Con.prepareStatement(
                        "UPDATE MEDICINE SET M_QUANTITY=? WHERE M_ID=?")) {
                    upd.setInt(1, newQty); upd.setInt(2, medId); upd.executeUpdate();
                }
                recordSale(medId, b_medName.getText(), orderQty, price * orderQty);
                loadMedicines(); Con.close();
                return true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Insufficient stock!\n  Available: " + mQty + "\n  Ordered: " + b_quantity.getText());
        }
        return false;
    }

    public void recordSale(int mId, String medName, int qty, double total) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO SALES (S_MED_NAME, S_DATE, S_QTY, S_TOTAL) VALUES (?,?,?,?)")) {
            ps.setString(1, medName);
            ps.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            ps.setInt(3, qty); ps.setDouble(4, total);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // ── Event handlers ────────────────────────────────────────────────────────
    private void medicine_tableMouseClicked(MouseEvent evt) {
        DefaultTableModel model = (DefaultTableModel) medicine_table.getModel();
        int i = medicine_table.getSelectedRow(); if (i < 0) return;
        b_medName.setText(model.getValueAt(i, 1).toString());
        medId = Integer.parseInt(model.getValueAt(i, 0).toString());
        mQty  = Integer.parseInt(model.getValueAt(i, 2).toString());
        price = Double.parseDouble(model.getValueAt(i, 3).toString());
    }

    private void btnAddToBillMouseClicked(MouseEvent evt) {
        if (b_medName.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Select a medicine first."); return; }
        boolean ok = updateQty();
        if (ok) {
            billID++;
            b_id.setText(String.valueOf(billID));
            try {
                double lineTotal = Integer.parseInt(b_quantity.getText()) * price;
                billTotal += lineTotal;
                String line = String.format("\n %-3d  %-16s $%-8.2f %-5s $%.2f",
                    billID, b_medName.getText(), price, b_quantity.getText(), lineTotal);
                if (billID == 1) {
                    b_textArea.setText("*** PHARMA-EASY ***\n ID  Medicine         Price    Qty   Net" + line);
                } else {
                    b_textArea.append(line);
                }
                totalLabel.setText("TOTAL: $" + String.format("%.2f", billTotal));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid quantity."); billID--;
            }
        }
    }

    private void btnPrintMouseClicked(MouseEvent evt) {
        try { b_textArea.print(); } catch (Exception e) { e.printStackTrace(); }
    }

    private void btnClearMouseClicked(MouseEvent evt) {
        b_id.setText(""); b_medName.setText(""); b_quantity.setText("");
        billID = 0; billTotal = 0.0;
        b_textArea.setText("*** PHARMA-EASY ***\n ID  Medicine         Price    Qty   Net");
        totalLabel.setText("TOTAL: $0.00");
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new SellingFrame().setVisible(true));
    }
}
