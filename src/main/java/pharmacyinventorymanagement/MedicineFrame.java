package pharmacyinventorymanagement;

/**
 * MedicineFrame provides the full inventory management interface for the
 * Pharmacy Tracking System. It supports adding, updating, deleting, and
 * viewing medicine records across 14 fields including batch, category, and
 * per-medicine reorder thresholds. Rows are highlighted red (low stock) or
 * yellow (near expiry) using a custom cell renderer.
 *
 * @author Abdoullah Ndao
 */

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MedicineFrame extends javax.swing.JFrame {

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
    private static final Color DANGER_BG     = new Color(255, 220, 220);
    private static final Color WARNING_BG    = new Color(255, 255, 204);

    // ── JDBC state ────────────────────────────────────────────────────────────
    Connection Con = null;
    Statement St = null;
    ResultSet Rs = null;
    java.util.Date FDate, EDate;
    java.sql.Date MyFabdate, MyExpDate;

    // ── Form fields ───────────────────────────────────────────────────────────
    private JTextField m_id, m_name, m_quantity, m_price, m_owner;
    private JTextField m_strength, m_dosage, m_unitcost, m_threshold, m_batch;
    private JComboBox<String> m_company, m_category;
    private JDateChooser m_expdate, m_mftdate;

    // ── Table & action buttons ────────────────────────────────────────────────
    private JTable medicine_table;
    private JButton btnAdd, btnDelete, btnUpdate, btnClear;
    private JLabel headerSubtitle;

    public MedicineFrame() {
        initComponents();
        loadMedicines();
        applyTableHighlighters();
    }

    // ── Table row highlighter (low-stock = red, near-expiry = yellow) ─────────
    private void applyTableHighlighters() {
        medicine_table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                try {
                    int qty       = Integer.parseInt(table.getValueAt(row, 2).toString());
                    int threshold = 10;
                    try { threshold = Integer.parseInt(table.getValueAt(row, 12).toString()); } catch (Exception ignored) {}
                    java.sql.Date expDate    = (java.sql.Date) table.getValueAt(row, 4);
                    long          daysToExp  = (expDate.getTime() - System.currentTimeMillis()) / 86_400_000L;

                    if (!isSelected) {
                        if (qty <= threshold)  c.setBackground(DANGER_BG);
                        else if (daysToExp < 30) c.setBackground(WARNING_BG);
                        else                   c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    }
                    setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                } catch (Exception ignored) {}
                return c;
            }
        });
    }

    // ── UI construction ───────────────────────────────────────────────────────
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Manage Medicines – Pharmacy System");
        setSize(1120, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CONTENT_BG);
        setContentPane(root);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
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

        // Active module indicator
        JLabel activeLabel = new JLabel("  💊 Medicines");
        activeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        activeLabel.setForeground(ACCENT);
        activeLabel.setBackground(new Color(6, 78, 59));
        activeLabel.setOpaque(true);
        activeLabel.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 8));
        activeLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        sidebar.add(activeLabel);
        sidebar.add(sep());

        String[][] navItems = {
            {"🏠  Dashboard",  "dashboard"},
            {"👤  Agents",     "agents"},
            {"🏢  Suppliers",  "company"},
            {"💳  Billing",    "sell"},
            {"📦  Purchase Orders", "po"}
        };
        for (String[] item : navItems) {
            JLabel nav = navLabel(item[0]);
            final String key = item[1];
            nav.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    switch (key) {
                        case "dashboard": new DashboardFrame().setVisible(true); dispose(); break;
                        case "agents":    new AgentsFrame().setVisible(true);    dispose(); break;
                        case "company":   new CompanyFrame().setVisible(true);   dispose(); break;
                        case "sell":      new SellingFrame().setVisible(true);   dispose(); break;
                        case "po":        new PurchaseOrderFrame().setVisible(true); dispose(); break;
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
                int c = JOptionPane.showConfirmDialog(MedicineFrame.this, "Exit application?", "Confirm Exit", JOptionPane.YES_NO_OPTION);
                if (c == JOptionPane.YES_OPTION) System.exit(0);
            }
        });
        sidebar.add(exit);
        sidebar.add(Box.createVerticalStrut(8));
        return sidebar;
    }

    // ── Content area (header + form card + table) ─────────────────────────────
    private JPanel buildContent() {
        JPanel content = new JPanel(new BorderLayout(0, 0));
        content.setBackground(CONTENT_BG);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)));
        JLabel title = new JLabel("💊  Medicine Inventory");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        headerSubtitle = new JLabel("Manage stock records");
        headerSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        headerSubtitle.setForeground(TEXT_MUTED);
        JLabel sub = headerSubtitle;
        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setBackground(Color.WHITE);
        titleBox.add(title); titleBox.add(sub);
        header.add(titleBox, BorderLayout.CENTER);
        content.add(header, BorderLayout.NORTH);

        // Body (form + table in scroll area)
        JPanel body = new JPanel(new BorderLayout(0, 12));
        body.setBackground(CONTENT_BG);
        body.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        body.add(buildFormCard(), BorderLayout.NORTH);
        JSeparator divider = new JSeparator();
        divider.setForeground(new Color(226, 232, 240));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        body.add(divider, BorderLayout.CENTER);
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

    private void setStatus(String msg) {
        if (statusBar != null) statusBar.setText(msg);
    }

    // ── Form card ─────────────────────────────────────────────────────────────
    private JPanel buildFormCard() {
        // Initialise all form fields
        m_id        = field(); m_name     = field(); m_quantity  = field();
        m_price     = field(); m_owner    = field(); m_strength  = field();
        m_dosage    = field(); m_unitcost = field();
        m_threshold = field("10"); m_batch = field();
        m_company   = new JComboBox<>(); styleCombo(m_company);
        m_category  = new JComboBox<>(new String[]{"Tablet","Syrup","Injection","Capsule","Ointment","Other"}); styleCombo(m_category);
        m_expdate   = new JDateChooser(); m_mftdate = new JDateChooser();
        styleDate(m_expdate); styleDate(m_mftdate);
        loadCompanyComboBox();

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(16, 20, 12, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        // Row 0
        addRow(card, g, 0, "ID",           m_id,       "EXP. DATE",  m_expdate);
        addRow(card, g, 1, "Name",         m_name,     "MFT. DATE",  m_mftdate);
        addRow(card, g, 2, "Quantity",     m_quantity, "Supplier",   m_company);
        addRow(card, g, 3, "Unit Price",   m_price,    "Owner",      m_owner);
        addRow(card, g, 4, "Category",     m_category, "Strength",   m_strength);
        addRow(card, g, 5, "Dosage",       m_dosage,   "Unit Cost",  m_unitcost);
        addRow(card, g, 6, "Min. Stock",   m_threshold,"Batch No.",  m_batch);

        // Buttons row
        btnAdd    = actionBtn("＋ ADD",    ACCENT,    ACCENT_DARK);
        btnUpdate = actionBtn("↻ UPDATE",  new Color(59, 130, 246), new Color(37, 99, 235));
        btnDelete = actionBtn("✕ DELETE",  DANGER,    new Color(185, 28, 28));
        btnClear  = actionBtn("⟳ CLEAR",   new Color(100,116,139),  new Color(71,85,105));
        btnAdd.setToolTipText("Add a new medicine record to inventory");
        btnUpdate.setToolTipText("Update the selected medicine record");
        btnDelete.setToolTipText("Permanently delete the selected medicine");
        btnClear.setToolTipText("Clear all form fields");

        btnAdd.addMouseListener(new MouseAdapter()    { public void mouseClicked(MouseEvent e) { btnAddMouseClicked(e); } });
        btnUpdate.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { btnUpdateMouseClicked(e); } });
        btnDelete.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { btnDeleteMouseClicked(e); } });
        btnClear.addMouseListener(new MouseAdapter()  { public void mouseClicked(MouseEvent e) { btnClearMouseClicked(e); } });
        // Escape clears the form; Enter on m_batch (last field) triggers ADD
        m_batch.addActionListener(e -> btnAddMouseClicked(null));
        m_batch.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), "clr");
        m_batch.getActionMap().put("clr", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { btnClearMouseClicked(null); }
        });

        g.gridy = 7; g.gridx = 0; g.gridwidth = 4; g.weighty = 0;
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.add(btnAdd); btnRow.add(btnUpdate); btnRow.add(btnDelete); btnRow.add(btnClear);
        card.add(btnRow, g);
        g.gridwidth = 1;

        return card;
    }

    private void addRow(JPanel card, GridBagConstraints g, int row, String lbl1, Component f1, String lbl2, Component f2) {
        g.gridy = row; g.weightx = 0;
        g.gridx = 0; card.add(fLabel(lbl1), g);
        g.gridx = 1; g.weightx = 1; card.add(f1, g);
        g.gridx = 2; g.weightx = 0; card.add(fLabel(lbl2), g);
        g.gridx = 3; g.weightx = 1; card.add(f2, g);
    }

    // ── Table panel ───────────────────────────────────────────────────────────
    private JLabel rowCountLabel;

    private JPanel buildTablePanel() {
        medicine_table = new JTable();
        styleTable(medicine_table);
        medicine_table.setAutoCreateRowSorter(true);
        medicine_table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { medicine_tableMouseClicked(e); }
        });
        JScrollPane scroll = new JScrollPane(medicine_table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        // Live search field
        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        searchField.putClientProperty("JTextField.placeholderText", "🔍  Search medicines...");
        searchField.setToolTipText("Filter the medicine list by any column");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            private void filterTable() {
                javax.swing.table.TableRowSorter<javax.swing.table.TableModel> sorter =
                    new javax.swing.table.TableRowSorter<>(medicine_table.getModel());
                medicine_table.setRowSorter(sorter);
                String text = searchField.getText().trim();
                sorter.setRowFilter(text.isEmpty() ? null : javax.swing.RowFilter.regexFilter("(?i)" + text));
                updateRowCount();
            }
        });

        rowCountLabel = new JLabel("  0 items");
        rowCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        rowCountLabel.setForeground(TEXT_MUTED);

        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));

        JLabel tblHeader = new JLabel("  Medicine List");
        tblHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblHeader.setForeground(TEXT_MUTED);
        topBar.add(tblHeader, BorderLayout.WEST);
        topBar.add(searchField, BorderLayout.CENTER);
        topBar.add(rowCountLabel, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        // F5 to refresh
        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "refresh");
        panel.getActionMap().put("refresh", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { loadMedicines(); }
        });
        return panel;
    }

    private void updateRowCount() {
        if (rowCountLabel != null)
            rowCountLabel.setText("  " + medicine_table.getRowCount() + " items  ");
    }

    private void setMedicineColumnWidths() {
        if (medicine_table.getColumnCount() < 5) return;
        int[] widths = {50, 160, 70, 70, 90, 90, 110, 70, 90, 70, 80, 70, 60, 80};
        for (int i = 0; i < Math.min(widths.length, medicine_table.getColumnCount()); i++)
            medicine_table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
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

    private JLabel fLabel(String text) {
        JLabel l = new JLabel(text);
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
            public void mouseExited(MouseEvent e)  { b.setBackground(bg);    }
        });
        return b;
    }

    private void styleCombo(JComboBox<String> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setPreferredSize(new Dimension(160, 28));
    }

    private void styleDate(JDateChooser dc) {
        dc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dc.setPreferredSize(new Dimension(160, 28));
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
            public void mouseExited(MouseEvent e)  { l.setBackground(SIDEBAR_BG);    }
        });
        return l;
    }

    private JSeparator sep() {
        JSeparator s = new JSeparator();
        s.setForeground(new Color(51, 65, 85)); s.setBackground(new Color(51, 65, 85));
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return s;
    }

    // ── Company combobox loader ────────────────────────────────────────────────
    public void loadCompanyComboBox() {
        m_company.removeAllItems();
        try (Connection c = DatabaseHelper.getConnection();
             Statement s  = c.createStatement();
             ResultSet r  = s.executeQuery("SELECT C_NAME FROM COMPANY ORDER BY C_NAME")) {
            while (r.next()) m_company.addItem(r.getString("C_NAME"));
        } catch (SQLException ignored) {}
        if (m_company.getItemCount() == 0)
            for (String s : new String[]{"Dakar Pharma","MedSupply","HealthCo","Pfizer","Novartis"}) m_company.addItem(s);
    }

    // ── Database operations ───────────────────────────────────────────────────

    public void loadMedicines() {
        try {
            Con = DatabaseHelper.getConnection();
            St  = Con.createStatement();
            Rs  = St.executeQuery("SELECT * FROM MEDICINE");
            medicine_table.setModel(DatabaseHelper.resultSetToTableModel(Rs));
            applyTableHighlighters();
            updateRowCount();
            setMedicineColumnWidths();
            if (headerSubtitle != null)
                headerSubtitle.setText(medicine_table.getRowCount() + " records in inventory");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Error loading medicines: " + e.getMessage());
        }
    }

    @Deprecated public void SelectMed() { loadMedicines(); }

    private void highlightRequired(JTextField... fields) {
        for (JTextField f : fields)
            f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(f.getText().trim().isEmpty() ? new Color(239,68,68) : BORDER_CLR),
                BorderFactory.createEmptyBorder(4,8,4,8)));
    }

    private void btnAddMouseClicked(MouseEvent evt) {
        highlightRequired(m_id, m_name, m_quantity, m_price);
        try {
            Con = DatabaseHelper.getConnection();
            String Id = m_id.getText();
            try (PreparedStatement chk = Con.prepareStatement("SELECT M_ID FROM MEDICINE WHERE M_ID=?")) {
                chk.setInt(1, Integer.parseInt(Id));
                if (chk.executeQuery().next()) {
                    JOptionPane.showMessageDialog(this, "Medicine ID " + Id + " already exists."); return;
                }
            }
            FDate = m_mftdate.getDate(); MyFabdate = new java.sql.Date(FDate.getTime());
            EDate = m_expdate.getDate(); MyExpDate  = new java.sql.Date(EDate.getTime());
            try (PreparedStatement add = Con.prepareStatement(
                    "INSERT INTO MEDICINE VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                add.setInt(1, Integer.parseInt(m_id.getText()));
                add.setString(2, m_name.getText());
                add.setInt(3, Integer.parseInt(m_quantity.getText()));
                add.setDouble(4, Double.parseDouble(m_price.getText()));
                add.setDate(5, MyExpDate); add.setDate(6, MyFabdate);
                add.setString(7, m_company.getSelectedItem().toString());
                add.setString(8, m_owner.getText().isEmpty() ? "Main" : m_owner.getText());
                add.setString(9, m_category.getSelectedItem().toString());
                add.setString(10, m_strength.getText()); add.setString(11, m_dosage.getText());
                add.setDouble(12, m_unitcost.getText().isEmpty() ? 0.0 : Double.parseDouble(m_unitcost.getText()));
                add.setInt(13, m_threshold.getText().isEmpty() ? 10 : Integer.parseInt(m_threshold.getText()));
                add.setString(14, m_batch.getText());
                add.executeUpdate();
            }
            Con.close(); loadMedicines();
            setStatus("Medicine added: " + m_name.getText());
            JOptionPane.showMessageDialog(this, "Medicine added successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void btnDeleteMouseClicked(MouseEvent evt) {
        if (m_id.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Enter the ID to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete medicine " + m_id.getText() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Con = DatabaseHelper.getConnection();
            try (PreparedStatement chk = Con.prepareStatement("SELECT M_ID FROM MEDICINE WHERE M_ID=?")) {
                chk.setInt(1, Integer.parseInt(m_id.getText()));
                if (!chk.executeQuery().next()) { JOptionPane.showMessageDialog(this, "ID not found."); return; }
            }
            try (PreparedStatement del = Con.prepareStatement("DELETE FROM MEDICINE WHERE M_ID=?")) {
                del.setInt(1, Integer.parseInt(m_id.getText())); del.executeUpdate();
            }
            loadMedicines(); JOptionPane.showMessageDialog(this, "Medicine deleted.");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void btnUpdateMouseClicked(MouseEvent evt) {
        if (m_id.getText().isEmpty() || m_name.getText().isEmpty() || m_price.getText().isEmpty() || m_quantity.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill ID, Name, Quantity and Price."); return;
        }
        try {
            Con = DatabaseHelper.getConnection();
            int id = Integer.parseInt(m_id.getText());
            try (PreparedStatement chk = Con.prepareStatement("SELECT M_ID FROM MEDICINE WHERE M_ID=?")) {
                chk.setInt(1, id);
                if (!chk.executeQuery().next()) { JOptionPane.showMessageDialog(this, "Medicine ID " + id + " not found."); return; }
            }
            FDate = m_mftdate.getDate(); MyFabdate = new java.sql.Date(FDate.getTime());
            EDate = m_expdate.getDate(); MyExpDate  = new java.sql.Date(EDate.getTime());
            try (PreparedStatement upd = Con.prepareStatement(
                    "UPDATE MEDICINE SET M_NAME=?,M_PRICE=?,M_QUANTITY=?,M_MFTDATE=?,M_EXPDATE=?,M_COMPANY=?,M_OWNER=?,M_CATEGORY=?,M_STRENGTH=?,M_DOSAGE=?,M_UNIT_COST=?,M_THRESHOLD=?,M_BATCH=? WHERE M_ID=?")) {
                upd.setString(1, m_name.getText()); upd.setDouble(2, Double.parseDouble(m_price.getText()));
                upd.setInt(3, Integer.parseInt(m_quantity.getText()));
                upd.setDate(4, MyFabdate); upd.setDate(5, MyExpDate);
                upd.setString(6, m_company.getSelectedItem().toString());
                upd.setString(7, m_owner.getText().isEmpty() ? "Main" : m_owner.getText());
                upd.setString(8, m_category.getSelectedItem().toString());
                upd.setString(9, m_strength.getText()); upd.setString(10, m_dosage.getText());
                upd.setDouble(11, m_unitcost.getText().isEmpty() ? 0.0 : Double.parseDouble(m_unitcost.getText()));
                upd.setInt(12, m_threshold.getText().isEmpty() ? 10 : Integer.parseInt(m_threshold.getText()));
                upd.setString(13, m_batch.getText()); upd.setInt(14, id);
                upd.executeUpdate();
            }
            loadMedicines(); JOptionPane.showMessageDialog(this, "Medicine updated.");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void medicine_tableMouseClicked(MouseEvent evt) {
        DefaultTableModel model = (DefaultTableModel) medicine_table.getModel();
        int i = medicine_table.getSelectedRow();
        if (i < 0) return;
        m_id.setText(model.getValueAt(i, 0).toString());
        m_name.setText(model.getValueAt(i, 1).toString());
        m_quantity.setText(model.getValueAt(i, 2).toString());
        m_price.setText(model.getValueAt(i, 3).toString());
        m_expdate.setDate((java.sql.Date) model.getValueAt(i, 4));
        m_mftdate.setDate((java.sql.Date) model.getValueAt(i, 5));
        m_company.setSelectedItem(model.getValueAt(i, 6).toString());
        m_owner.setText(nullSafe(model.getValueAt(i, 7), ""));
        m_category.setSelectedItem(nullSafe(model.getValueAt(i, 8), "Tablet"));
        m_strength.setText(nullSafe(model.getValueAt(i, 9), ""));
        m_dosage.setText(nullSafe(model.getValueAt(i, 10), ""));
        m_unitcost.setText(nullSafe(model.getValueAt(i, 11), ""));
        m_threshold.setText(nullSafe(model.getValueAt(i, 12), "10"));
        m_batch.setText(nullSafe(model.getValueAt(i, 13), ""));
    }

    private void resetFieldBorder(JTextField... fields) {
        for (JTextField f : fields)
            f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR), BorderFactory.createEmptyBorder(4,8,4,8)));
    }

    private void btnClearMouseClicked(MouseEvent evt) {
        m_id.setText(""); m_name.setText(""); m_quantity.setText(""); m_price.setText("");
        m_owner.setText(""); m_strength.setText(""); m_dosage.setText("");
        m_unitcost.setText(""); m_threshold.setText("10"); m_batch.setText("");
        m_category.setSelectedIndex(0); m_company.setSelectedIndex(0);
        m_expdate.setDate(null); m_mftdate.setDate(null);
        resetFieldBorder(m_id, m_name, m_quantity, m_price, m_owner, m_strength, m_dosage, m_unitcost, m_threshold, m_batch);
        setStatus("Form cleared");
    }

    private String nullSafe(Object v, String def) { return v == null ? def : v.toString(); }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new MedicineFrame().setVisible(true));
    }
}
