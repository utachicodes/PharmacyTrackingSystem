package pharmacyinventorymanagement;

/**
 * CompanyFrame provides the supplier management interface for the Pharmacy Tracking
 * System. It stores contact details, lead times, and preferred-supplier flags for
 * all medicine suppliers, which are used to populate the Supplier dropdown in the
 * Medicine and Purchase Order modules.
 *
 * @author Abdoullah Ndao
 */

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CompanyFrame extends javax.swing.JFrame {

    // ── Design constants ──────────────────────────────────────────────────────
    private static final Color SIDEBAR_BG    = new Color(30, 41, 59);
    private static final Color SIDEBAR_HOVER = new Color(51, 65, 85);
    private static final Color ACCENT        = new Color(16, 185, 129);
    private static final Color ACCENT_DARK   = new Color(5, 150, 105);
    private static final Color CONTENT_BG    = new Color(241, 245, 249);
    private static final Color TEXT_DARK     = new Color(30, 41, 59);
    private static final Color TEXT_MUTED    = new Color(100, 116, 139);
    private static final Color BORDER_CLR    = new Color(203, 213, 225);
    private static final Color DANGER        = new Color(239, 68, 68);
    private static final Color DANGER_DARK   = new Color(185, 28, 28);

    // ── JDBC state ────────────────────────────────────────────────────────────
    Connection Con = null;
    Statement St = null;
    ResultSet Rs = null;

    // ── Form fields ───────────────────────────────────────────────────────────
    private JTextField c_id, c_name, c_address, c_phone, c_exp, c_email, c_leadtime;
    private JComboBox<String> c_preferred;

    // ── Table & buttons ───────────────────────────────────────────────────────
    private JTable company_table;
    private JButton btnAdd, btnDelete, btnUpdate, btnClear;
    private JLabel headerSubtitle;

    public CompanyFrame() {
        initComponents();
        loadCompanies();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Supplier Management – Pharmacy System");
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

        JLabel activeLabel = new JLabel("  🏢 Suppliers");
        activeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        activeLabel.setForeground(ACCENT);
        activeLabel.setBackground(new Color(6, 78, 59));
        activeLabel.setOpaque(true);
        activeLabel.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 8));
        activeLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        sidebar.add(activeLabel);
        sidebar.add(sep());

        String[][] items = {
            {"🏠  Dashboard", "dash"}, {"💊  Medicines", "med"},
            {"👤  Agents", "agents"},  {"💳  Billing", "sell"},
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
                        case "sell":   new SellingFrame().setVisible(true);       dispose(); break;
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
                int c = JOptionPane.showConfirmDialog(CompanyFrame.this, "Exit application?", "Confirm", JOptionPane.YES_NO_OPTION);
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

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)));
        JLabel title = new JLabel("🏢  Supplier Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        headerSubtitle = new JLabel("Manage supplier contacts and lead times");
        headerSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        headerSubtitle.setForeground(TEXT_MUTED);
        JLabel sub = headerSubtitle;
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
        content.add(body, BorderLayout.CENTER);
        content.add(buildStatusBar(), BorderLayout.SOUTH);
        return content;
    }

    private JLabel statusBar;

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(248, 250, 252));
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(4, 16, 4, 16)));
        statusBar = new JLabel("Ready");
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.setForeground(TEXT_MUTED);
        bar.add(statusBar, BorderLayout.WEST);
        return bar;
    }

    private void setStatus(String msg) { if (statusBar != null) statusBar.setText(msg); }

    private JPanel buildFormCard() {
        c_id       = field(); c_name    = field(); c_address = field();
        c_phone    = field(); c_exp     = field(); c_email   = field();
        c_leadtime = field("7");
        c_preferred = new JComboBox<>(new String[]{"No", "Yes"}); styleCombo(c_preferred);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(16, 20, 12, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        addRow(card, g, 0, "ID",          c_id,       "Phone",     c_phone);
        addRow(card, g, 1, "Name",        c_name,     "Email",     c_email);
        addRow(card, g, 2, "Address",     c_address,  "Exp. Yrs",  c_exp);
        addRow(card, g, 3, "Lead Time",   c_leadtime, "Preferred", c_preferred);

        btnAdd    = actionBtn("＋ ADD",    ACCENT,    ACCENT_DARK);
        btnUpdate = actionBtn("↻ UPDATE",  new Color(59,130,246), new Color(37,99,235));
        btnDelete = actionBtn("✕ DELETE",  DANGER,    DANGER_DARK);
        btnClear  = actionBtn("⟳ CLEAR",   new Color(100,116,139), new Color(71,85,105));
        btnAdd.setToolTipText("Add a new supplier to the directory");
        btnUpdate.setToolTipText("Update the selected supplier's information");
        btnDelete.setToolTipText("Remove this supplier from the directory");
        btnClear.setToolTipText("Clear all form fields");

        btnAdd.addMouseListener(new MouseAdapter()    { public void mouseClicked(MouseEvent e) { btnAddMouseClicked(e); } });
        btnUpdate.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { btnUpdateMouseClicked(e); } });
        btnDelete.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { btnDeleteMouseClicked(e); } });
        btnClear.addMouseListener(new MouseAdapter()  { public void mouseClicked(MouseEvent e) { btnClearMouseClicked(e); } });
        c_email.addActionListener(e -> btnAddMouseClicked(null));
        c_email.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), "clr");
        c_email.getActionMap().put("clr", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { btnClearMouseClicked(null); }
        });

        g.gridy = 4; g.gridx = 0; g.gridwidth = 4;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.add(btnAdd); btnRow.add(btnUpdate); btnRow.add(btnDelete); btnRow.add(btnClear);
        card.add(btnRow, g);
        return card;
    }

    private void addRow(JPanel p, GridBagConstraints g, int row, String l1, Component f1, String l2, Component f2) {
        g.gridy = row; g.weightx = 0;
        g.gridx = 0; p.add(fLabel(l1), g);
        g.gridx = 1; g.weightx = 1; p.add(f1, g);
        g.gridx = 2; g.weightx = 0; p.add(fLabel(l2), g);
        g.gridx = 3; g.weightx = 1; p.add(f2, g);
    }

    private JLabel companyRowCount;

    private JPanel buildTablePanel() {
        company_table = new JTable();
        company_table.setAutoCreateRowSorter(true);
        styleTable(company_table);
        company_table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { company_tableMouseClicked(e); }
        });
        JScrollPane scroll = new JScrollPane(company_table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        searchField.putClientProperty("JTextField.placeholderText", "🔍  Search suppliers...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                javax.swing.table.TableRowSorter<javax.swing.table.TableModel> s =
                    new javax.swing.table.TableRowSorter<>(company_table.getModel());
                company_table.setRowSorter(s);
                String t = searchField.getText().trim();
                s.setRowFilter(t.isEmpty() ? null : javax.swing.RowFilter.regexFilter("(?i)" + t));
                if (companyRowCount != null) companyRowCount.setText("  " + company_table.getRowCount() + " suppliers  ");
            }
        });

        companyRowCount = new JLabel("  0 suppliers");
        companyRowCount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        companyRowCount.setForeground(TEXT_MUTED);

        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JLabel hdr = new JLabel("  Supplier List");
        hdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        hdr.setForeground(TEXT_MUTED);
        topBar.add(hdr, BorderLayout.WEST);
        topBar.add(searchField, BorderLayout.CENTER);
        topBar.add(companyRowCount, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "refresh");
        panel.getActionMap().put("refresh", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { loadCompanies(); }
        });
        return panel;
    }

    // ── Styling helpers ───────────────────────────────────────────────────────
    private JTextField field(String... def) {
        JTextField f = new JTextField(def.length > 0 ? def[0] : "");
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
        l.setPreferredSize(new Dimension(90, 28));
        return l;
    }
    private JButton actionBtn(String text, Color bg, Color hover) {
        JButton b = new JButton(text);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(110, 32));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { b.setBackground(bg); }
        });
        return b;
    }
    private void styleCombo(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setPreferredSize(new Dimension(160, 28));
    }
    private void styleTable(JTable t) {
        t.setRowHeight(30);
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
    public void loadCompanies() {
        try {
            Con = DatabaseHelper.getConnection();
            St = Con.createStatement();
            Rs = St.executeQuery("SELECT * FROM COMPANY");
            company_table.setModel(DatabaseHelper.resultSetToTableModel(Rs));
            if (companyRowCount != null) companyRowCount.setText("  " + company_table.getRowCount() + " suppliers  ");
            setCompanyColumnWidths();
            if (headerSubtitle != null) headerSubtitle.setText(company_table.getRowCount() + " suppliers on record");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage());
        }
    }
    @Deprecated public void SelectCompany() { loadCompanies(); }

    private void setCompanyColumnWidths() {
        if (company_table.getColumnCount() < 5) return;
        int[] widths = {50, 160, 180, 70, 120, 160, 80, 80};
        for (int i = 0; i < Math.min(widths.length, company_table.getColumnCount()); i++)
            company_table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private void highlightRequired(JTextField... fields) {
        for (JTextField f : fields)
            f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(f.getText().trim().isEmpty() ? new Color(239,68,68) : BORDER_CLR),
                BorderFactory.createEmptyBorder(4,8,4,8)));
    }

    private void btnAddMouseClicked(MouseEvent evt) {
        highlightRequired(c_id, c_name, c_address, c_phone, c_exp);
        try {
            Con = DatabaseHelper.getConnection();
            try (PreparedStatement add = Con.prepareStatement("INSERT INTO COMPANY VALUES(?,?,?,?,?,?,?,?)")) {
                add.setInt(1, Integer.parseInt(c_id.getText()));
                add.setString(2, c_name.getText()); add.setString(3, c_address.getText());
                add.setInt(4, Integer.parseInt(c_exp.getText()));
                add.setString(5, c_phone.getText()); add.setString(6, c_email.getText());
                add.setInt(7, Integer.parseInt(c_leadtime.getText()));
                add.setString(8, c_preferred.getSelectedItem().toString());
                add.executeUpdate();
            }
            Con.close(); loadCompanies();
            setStatus("Supplier added: " + c_name.getText());
            JOptionPane.showMessageDialog(this, "Supplier added successfully.");
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error: ID or name already exists.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void btnDeleteMouseClicked(MouseEvent evt) {
        if (c_id.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Enter the supplier ID to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete supplier " + c_id.getText() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Con = DatabaseHelper.getConnection();
            try (PreparedStatement chk = Con.prepareStatement("SELECT C_ID FROM COMPANY WHERE C_ID=?")) {
                chk.setInt(1, Integer.parseInt(c_id.getText()));
                if (!chk.executeQuery().next()) { JOptionPane.showMessageDialog(this, "Supplier ID not found."); return; }
            }
            try (PreparedStatement del = Con.prepareStatement("DELETE FROM COMPANY WHERE C_ID=?")) {
                del.setInt(1, Integer.parseInt(c_id.getText())); del.executeUpdate();
            }
            loadCompanies(); JOptionPane.showMessageDialog(this, "Supplier deleted.");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void btnUpdateMouseClicked(MouseEvent evt) {
        if (c_id.getText().isEmpty() || c_name.getText().isEmpty() || c_address.getText().isEmpty()
                || c_exp.getText().isEmpty() || c_phone.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill ID, Name, Address, Experience, and Phone."); return;
        }
        try {
            Con = DatabaseHelper.getConnection();
            int id = Integer.parseInt(c_id.getText());
            try (PreparedStatement chk = Con.prepareStatement("SELECT C_ID FROM COMPANY WHERE C_ID=?")) {
                chk.setInt(1, id);
                if (!chk.executeQuery().next()) { JOptionPane.showMessageDialog(this, "Supplier ID not found."); return; }
            }
            try (PreparedStatement upd = Con.prepareStatement(
                    "UPDATE COMPANY SET C_NAME=?,C_ADDRESS=?,C_EXP=?,C_PHONE=?,C_EMAIL=?,C_LEADTIME=?,C_PREFERRED=? WHERE C_ID=?")) {
                upd.setString(1, c_name.getText()); upd.setString(2, c_address.getText());
                upd.setInt(3, Integer.parseInt(c_exp.getText()));
                upd.setString(4, c_phone.getText()); upd.setString(5, c_email.getText());
                upd.setInt(6, Integer.parseInt(c_leadtime.getText()));
                upd.setString(7, c_preferred.getSelectedItem().toString());
                upd.setInt(8, id); upd.executeUpdate();
            }
            loadCompanies(); JOptionPane.showMessageDialog(this, "Supplier updated.");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void company_tableMouseClicked(MouseEvent evt) {
        DefaultTableModel model = (DefaultTableModel) company_table.getModel();
        int i = company_table.getSelectedRow(); if (i < 0) return;
        c_id.setText(model.getValueAt(i, 0).toString());
        c_name.setText(model.getValueAt(i, 1).toString());
        c_address.setText(model.getValueAt(i, 2).toString());
        c_exp.setText(model.getValueAt(i, 3).toString());
        c_phone.setText(model.getValueAt(i, 4).toString());
        c_email.setText(model.getValueAt(i, 5) == null ? "" : model.getValueAt(i, 5).toString());
        c_leadtime.setText(model.getValueAt(i, 6) == null ? "7" : model.getValueAt(i, 6).toString());
        c_preferred.setSelectedItem(model.getValueAt(i, 7) == null ? "No" : model.getValueAt(i, 7).toString());
    }

    private void btnClearMouseClicked(MouseEvent evt) {
        c_id.setText(""); c_name.setText(""); c_address.setText("");
        c_phone.setText(""); c_exp.setText(""); c_email.setText("");
        c_leadtime.setText("7"); c_preferred.setSelectedIndex(0);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new CompanyFrame().setVisible(true));
    }
}
