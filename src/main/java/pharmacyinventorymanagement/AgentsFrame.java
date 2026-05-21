package pharmacyinventorymanagement;

// Staff management screen — create, update, and delete agent accounts with role assignment.

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class AgentsFrame extends javax.swing.JFrame {

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

    private String userRole;

    // ── Form fields ───────────────────────────────────────────────────────────
    private JTextField a_id, a_name, a_age, a_phone, a_email;
    private JPasswordField a_password;
    private JComboBox<String> a_gender, a_role;

    // ── Table & buttons ───────────────────────────────────────────────────────
    private JTable AgentTable;
    private JButton btnAdd, btnDelete, btnUpdate, btnClear;
    private JLabel headerSubtitle;

    public AgentsFrame() { this("Admin"); }

    public AgentsFrame(String role) {
        this.userRole = role;
        initComponents();
        loadAgents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (javax.swing.JOptionPane.showConfirmDialog(null, "Exit the application?", "Confirm Exit", javax.swing.JOptionPane.YES_NO_OPTION) == javax.swing.JOptionPane.YES_OPTION)
                    System.exit(0);
            }
        });
        setTitle("Manage Agents – Pharmacy System");
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

        JLabel activeLabel = new JLabel("  Agents");
        activeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        activeLabel.setForeground(ACCENT);
        activeLabel.setBackground(new Color(6, 78, 59));
        activeLabel.setOpaque(true);
        activeLabel.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 8));
        activeLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        PharmIcons.apply(activeLabel, "agents");
        sidebar.add(activeLabel);
        sidebar.add(sep());

        boolean isTech  = "Technician".equalsIgnoreCase(userRole);
        boolean isPharm = "Pharmacist".equalsIgnoreCase(userRole);

        String[][] items = {
            {"Dashboard",       "dash"},
            {"Medicines",       "med"},
            {"Suppliers",       "comp"},
            {"Billing",         "sell"},
            {"Purchase Orders", "po"},
            {"Sales History",   "sales"},
            {"Reports",         "reports"}
        };
        for (String[] item : items) {
            final String key = item[1];
            boolean restricted =
                (isTech  && (key.equals("comp") || key.equals("po") || key.equals("reports"))) ||
                (isPharm && false);
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
                            case "comp":    next = new CompanyFrame(userRole);        break;
                            case "sell":    next = new SellingFrame(userRole);        break;
                            case "po":      next = new PurchaseOrderFrame(userRole);  break;
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

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(0, 24, 0, 24)));
        JLabel title = new JLabel("Manage Agents");
        title.setIcon(PharmIcons.hdr("agents"));
        title.setIconTextGap(9);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        headerSubtitle = new JLabel("Add and manage staff accounts");
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
        a_id    = field(); a_name  = field(); a_age   = field();
        a_phone = field(); a_email = field();
        a_password = new JPasswordField();
        a_password.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        a_password.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        a_password.setPreferredSize(new Dimension(160, 28));
        a_gender   = new JComboBox<>(new String[]{"Male", "Female", "Other"}); styleCombo(a_gender);
        a_role     = new JComboBox<>(new String[]{"Admin", "Pharmacist", "Technician"}); styleCombo(a_role);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(16, 20, 12, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 6, 5, 6);
        g.fill   = GridBagConstraints.HORIZONTAL;

        addRow(card, g, 0, "ID",       a_id,       "Phone",   a_phone);
        addRow(card, g, 1, "Name",     a_name,     "Email",   a_email);
        addRow(card, g, 2, "Age",      a_age,      "Gender",  a_gender);
        addRow(card, g, 3, "Password", a_password, "Role",    a_role);

        btnAdd    = actionBtn("＋ ADD",    ACCENT,    ACCENT_DARK);
        btnUpdate = actionBtn("↻ UPDATE",  new Color(59,130,246), new Color(37,99,235));
        btnDelete = actionBtn("✕ DELETE",  DANGER,    DANGER_DARK);
        btnClear  = actionBtn("⟳ CLEAR",   new Color(100,116,139), new Color(71,85,105));
        btnAdd.setToolTipText("Create a new staff account");
        btnUpdate.setToolTipText("Update the selected agent's information");
        btnDelete.setToolTipText("Permanently delete this agent account");
        btnClear.setToolTipText("Clear all form fields");

        btnAdd.addMouseListener(new MouseAdapter()    { public void mouseClicked(MouseEvent e) { btnAddMouseClicked(e); } });
        btnUpdate.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { btnUpdateMouseClicked(e); } });
        btnDelete.addMouseListener(new MouseAdapter() { public void mouseClicked(MouseEvent e) { btnDeleteMouseClicked(e); } });
        btnClear.addMouseListener(new MouseAdapter()  { public void mouseClicked(MouseEvent e) { btnClearMouseClicked(e); } });
        a_email.addActionListener(e -> btnAddMouseClicked(null));
        a_email.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), "clr");
        a_email.getActionMap().put("clr", new javax.swing.AbstractAction() {
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

    private JLabel agentRowCount;

    private JPanel buildTablePanel() {
        AgentTable = new JTable();
        AgentTable.setAutoCreateRowSorter(true);
        styleTable(AgentTable);
        AgentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { AgentTableMouseClicked(e); }
        });
        JScrollPane scroll = new JScrollPane(AgentTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));

        JTextField searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR), BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        searchField.putClientProperty("JTextField.placeholderText", "Search agents...");
        searchField.setToolTipText("Filter the agent list");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                javax.swing.table.TableRowSorter<javax.swing.table.TableModel> s =
                    new javax.swing.table.TableRowSorter<>(AgentTable.getModel());
                AgentTable.setRowSorter(s);
                String t = searchField.getText().trim();
                s.setRowFilter(t.isEmpty() ? null : javax.swing.RowFilter.regexFilter("(?i)" + t));
                if (agentRowCount != null) agentRowCount.setText("  " + AgentTable.getRowCount() + " agents  ");
            }
        });

        agentRowCount = new JLabel("  0 agents");
        agentRowCount.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        agentRowCount.setForeground(TEXT_MUTED);

        JPanel topBar = new JPanel(new BorderLayout(8, 0));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JLabel tblHdr = new JLabel("  Agent List");
        tblHdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblHdr.setForeground(TEXT_MUTED);
        topBar.add(tblHdr, BorderLayout.WEST);
        topBar.add(searchField, BorderLayout.CENTER);
        topBar.add(agentRowCount, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F5"), "refresh");
        panel.getActionMap().put("refresh", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { loadAgents(); }
        });
        return panel;
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

    // ── Database operations ───────────────────────────────────────────────────
    public void loadAgents() {
        try {
            Con = DatabaseHelper.getConnection();
            St = Con.createStatement();
            Rs = St.executeQuery("SELECT * FROM AGENTS");
            AgentTable.setModel(DatabaseHelper.resultSetToTableModel(Rs));
            if (agentRowCount != null) agentRowCount.setText("  " + AgentTable.getRowCount() + " agents  ");
            setAgentColumnWidths();
            applyRoleRenderer();
            if (headerSubtitle != null) headerSubtitle.setText(AgentTable.getRowCount() + " staff accounts");
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @Deprecated public void SelectMed() { loadAgents(); }

    // Colours the Role column: Admin = red, Pharmacist = blue, Technician = green.
    private void applyRoleRenderer() {
        int roleCol = -1;
        for (int i = 0; i < AgentTable.getColumnCount(); i++)
            if ("A_ROLE".equalsIgnoreCase(AgentTable.getColumnName(i))) { roleCol = i; break; }
        if (roleCol < 0) return;
        final int rc = roleCol;
        AgentTable.getColumnModel().getColumn(rc).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String role = v == null ? "" : v.toString();
                if (!sel) {
                    if ("Admin".equalsIgnoreCase(role))           { c.setBackground(new Color(254,242,242)); c.setForeground(new Color(185,28,28)); }
                    else if ("Pharmacist".equalsIgnoreCase(role)) { c.setBackground(new Color(239,246,255)); c.setForeground(new Color(29,78,216)); }
                    else                                          { c.setBackground(new Color(240,253,244)); c.setForeground(new Color(21,128,61)); }
                }
                c.setFont(new Font("Segoe UI", Font.BOLD, 11));
                c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    private void setAgentColumnWidths() {
        if (AgentTable.getColumnCount() < 4) return;
        // Hide the password column (index 3) — never show passwords on screen
        javax.swing.table.TableColumn pwdCol = AgentTable.getColumnModel().getColumn(3);
        pwdCol.setMinWidth(0); pwdCol.setMaxWidth(0); pwdCol.setWidth(0); pwdCol.setResizable(false);
        int[] widths = {50, 160, 50, 0, 120, 70, 180, 100};
        for (int i = 0; i < Math.min(widths.length, AgentTable.getColumnCount()); i++)
            AgentTable.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
    }

    private void highlightRequired(JTextField... fields) {
        for (JTextField f : fields)
            f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(f.getText().trim().isEmpty() ? new Color(239,68,68) : BORDER_CLR),
                BorderFactory.createEmptyBorder(4,8,4,8)));
    }

    private void btnAddMouseClicked(MouseEvent evt) {
        highlightRequired(a_id, a_name, a_age, a_phone, a_email);
        String pwd = new String(a_password.getPassword());
        a_password.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(pwd.isEmpty() ? new Color(239,68,68) : BORDER_CLR),
            BorderFactory.createEmptyBorder(4,8,4,8)));
        try {
            Con = DatabaseHelper.getConnection();
            try (PreparedStatement add = Con.prepareStatement("INSERT INTO AGENTS VALUES(?,?,?,?,?,?,?,?)")) {
                add.setInt(1, Integer.parseInt(a_id.getText()));
                add.setString(2, a_name.getText());
                add.setInt(3, Integer.parseInt(a_age.getText()));
                add.setString(4, pwd);
                add.setString(5, a_phone.getText());
                add.setString(6, a_gender.getSelectedItem().toString());
                add.setString(7, a_email.getText());
                add.setString(8, a_role.getSelectedItem().toString());
                add.executeUpdate();
            }
            loadAgents(); Con.close();
            setStatus("Agent added: " + a_name.getText());
            JOptionPane.showMessageDialog(this, "Agent added successfully.");
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Error: ID already exists.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage()); e.printStackTrace();
        }
    }

    private void btnDeleteMouseClicked(MouseEvent evt) {
        if (a_id.getText().isEmpty()) { JOptionPane.showMessageDialog(this, "Enter the agent ID to delete."); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete agent " + a_id.getText() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            Con = DatabaseHelper.getConnection();
            try (PreparedStatement del = Con.prepareStatement("DELETE FROM AGENTS WHERE A_ID=?")) {
                del.setInt(1, Integer.parseInt(a_id.getText())); del.executeUpdate();
            }
            loadAgents(); setStatus("Agent deleted: ID " + a_id.getText()); JOptionPane.showMessageDialog(this, "Agent deleted.");
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "SQL Error: " + e.getMessage()); }
    }

    private void btnUpdateMouseClicked(MouseEvent evt) {
        if (a_id.getText().isEmpty() || a_name.getText().isEmpty() || a_age.getText().isEmpty()
                || a_phone.getText().isEmpty() || a_email.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields."); return;
        }
        try {
            Con = DatabaseHelper.getConnection();
            int id = Integer.parseInt(a_id.getText());
            try (PreparedStatement chk = Con.prepareStatement("SELECT A_ID FROM AGENTS WHERE A_ID=?")) {
                chk.setInt(1, id);
                if (!chk.executeQuery().next()) { JOptionPane.showMessageDialog(this, "Agent ID " + id + " not found."); return; }
            }
            String newPwd = new String(a_password.getPassword());
            if (newPwd.isEmpty()) {
                // Password left blank — keep the existing one
                try (PreparedStatement upd = Con.prepareStatement(
                        "UPDATE AGENTS SET A_NAME=?,A_AGE=?,A_PHONE=?,A_GENDER=?,A_EMAIL=?,A_ROLE=? WHERE A_ID=?")) {
                    upd.setString(1, a_name.getText()); upd.setInt(2, Integer.parseInt(a_age.getText()));
                    upd.setString(3, a_phone.getText());
                    upd.setString(4, a_gender.getSelectedItem().toString());
                    upd.setString(5, a_email.getText()); upd.setString(6, a_role.getSelectedItem().toString());
                    upd.setInt(7, id); upd.executeUpdate();
                }
            } else {
                try (PreparedStatement upd = Con.prepareStatement(
                        "UPDATE AGENTS SET A_NAME=?,A_AGE=?,A_PHONE=?,A_PASSWORD=?,A_GENDER=?,A_EMAIL=?,A_ROLE=? WHERE A_ID=?")) {
                    upd.setString(1, a_name.getText()); upd.setInt(2, Integer.parseInt(a_age.getText()));
                    upd.setString(3, a_phone.getText()); upd.setString(4, newPwd);
                    upd.setString(5, a_gender.getSelectedItem().toString());
                    upd.setString(6, a_email.getText()); upd.setString(7, a_role.getSelectedItem().toString());
                    upd.setInt(8, id); upd.executeUpdate();
                }
            }
            loadAgents(); setStatus("Updated agent: " + a_name.getText()); JOptionPane.showMessageDialog(this, "Agent updated.");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void AgentTableMouseClicked(MouseEvent evt) {
        DefaultTableModel model = (DefaultTableModel) AgentTable.getModel();
        int i = AgentTable.getSelectedRow(); if (i < 0) return;
        a_id.setText(model.getValueAt(i, 0).toString());
        a_name.setText(model.getValueAt(i, 1).toString());
        a_age.setText(model.getValueAt(i, 2).toString());
        // Skip column 3 (A_PASSWORD) — never show password; leave field blank so admin types a new one if needed
        a_phone.setText(model.getValueAt(i, 4).toString());
        a_gender.setSelectedItem(model.getValueAt(i, 5).toString());
        a_email.setText(model.getValueAt(i, 6).toString());
        a_role.setSelectedItem(model.getValueAt(i, 7) == null ? "Technician" : model.getValueAt(i, 7).toString());
        a_password.setText("");
    }

    private void resetFieldBorder(JTextField... fields) {
        for (JTextField f : fields)
            f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR), BorderFactory.createEmptyBorder(4,8,4,8)));
    }

    private void btnClearMouseClicked(MouseEvent evt) {
        a_id.setText(""); a_name.setText(""); a_age.setText("");
        a_phone.setText(""); a_password.setText(""); a_email.setText("");
        a_gender.setSelectedIndex(0); a_role.setSelectedIndex(2);
        resetFieldBorder(a_id, a_name, a_age, a_phone, a_email);
        a_password.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR), BorderFactory.createEmptyBorder(4,8,4,8)));
        setStatus("Form cleared");
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new AgentsFrame().setVisible(true));
    }
}
