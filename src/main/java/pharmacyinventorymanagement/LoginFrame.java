package pharmacyinventorymanagement;

// Login screen — authenticates staff and routes to DashboardFrame based on their role.

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;

public class LoginFrame extends javax.swing.JFrame {

    private static final Color SIDEBAR_BG   = new Color(15, 23, 42);
    private static final Color SIDEBAR_MID  = new Color(30, 41, 59);
    private static final Color ACCENT       = new Color(16, 185, 129);
    private static final Color ACCENT_DARK  = new Color(5, 150, 105);
    private static final Color CONTENT_BG   = new Color(248, 250, 252);
    private static final Color TEXT_DARK    = new Color(15, 23, 42);
    private static final Color TEXT_MUTED   = new Color(100, 116, 139);
    private static final Color BORDER_CLR   = new Color(203, 213, 225);
    private static final Color BORDER_FOCUS = new Color(16, 185, 129);
    private static final Color ERROR_RED    = new Color(239, 68, 68);

    private JTextField    txtUserName;
    private JPasswordField l_password;
    private JButton       btnLogin, btnClear;
    private JLabel        statusBadge, errorLabel;
    private boolean       passwordVisible = false;

    public LoginFrame() {
        initComponents();
        checkDbConnection();
        SwingUtilities.invokeLater(() -> txtUserName.requestFocusInWindow());
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(860, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Thin accent border around the whole window
            }
        };
        root.setBorder(BorderFactory.createLineBorder(ACCENT, 2));
        setContentPane(root);

        // Drag-to-move
        final Point[] drag = {null};
        root.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { drag[0] = e.getLocationOnScreen(); }
        });
        root.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (drag[0] != null) {
                    Point loc = getLocation(), cur = e.getLocationOnScreen();
                    setLocation(loc.x + cur.x - drag[0].x, loc.y + cur.y - drag[0].y);
                    drag[0] = cur;
                }
            }
        });

        root.add(buildLeft(), BorderLayout.WEST);
        root.add(buildRight(), BorderLayout.CENTER);
    }

    // ── LEFT PANEL ────────────────────────────────────────────────────────────
    private JPanel buildLeft() {
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Dark gradient background
                GradientPaint gp = new GradientPaint(0, 0, SIDEBAR_BG, 0, getHeight(), SIDEBAR_MID);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Subtle accent circle in top-right
                g2.setColor(new Color(16, 185, 129, 18));
                g2.fillOval(getWidth() - 120, -60, 200, 200);
                g2.setColor(new Color(16, 185, 129, 10));
                g2.fillOval(-40, getHeight() - 130, 180, 180);
                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(310, 0));
        left.setLayout(new BorderLayout());
        left.setOpaque(false);

        // ── Top: logo ──────────────────────────────────────────────────────────
        JPanel logoArea = new JPanel();
        logoArea.setOpaque(false);
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBorder(BorderFactory.createEmptyBorder(36, 28, 20, 28));

        JLabel icon = new JLabel("⚕");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        icon.setForeground(ACCENT);
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel appName = new JLabel("PharmTrack");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Pharmacy Inventory System");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tagline.setForeground(new Color(148, 163, 184));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoArea.add(icon);
        logoArea.add(Box.createVerticalStrut(8));
        logoArea.add(appName);
        logoArea.add(Box.createVerticalStrut(4));
        logoArea.add(tagline);
        left.add(logoArea, BorderLayout.NORTH);

        // ── Middle: role cards ────────────────────────────────────────────────
        JPanel rolesArea = new JPanel();
        rolesArea.setOpaque(false);
        rolesArea.setLayout(new BoxLayout(rolesArea, BoxLayout.Y_AXIS));
        rolesArea.setBorder(BorderFactory.createEmptyBorder(0, 28, 0, 28));

        JLabel rolesTitle = new JLabel("USER ROLES");
        rolesTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
        rolesTitle.setForeground(new Color(71, 85, 105));
        rolesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rolesArea.add(rolesTitle);
        rolesArea.add(Box.createVerticalStrut(10));

        rolesArea.add(roleCard("admin",   "Admin",
            "Full access — all modules,\nstaff management & reports",
            new Color(254, 226, 226), new Color(239, 68, 68)));
        rolesArea.add(Box.createVerticalStrut(8));
        rolesArea.add(roleCard("med",     "Pharmacist",
            "All modules except\nstaff management",
            new Color(219, 234, 254), new Color(59, 130, 246)));
        rolesArea.add(Box.createVerticalStrut(8));
        rolesArea.add(roleCard("agents",  "Technician",
            "Medicines & Billing only",
            new Color(220, 252, 231), new Color(16, 185, 129)));

        left.add(rolesArea, BorderLayout.CENTER);

        // ── Bottom: author ────────────────────────────────────────────────────
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBorder(BorderFactory.createEmptyBorder(16, 28, 20, 28));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        footer.add(sep);
        footer.add(Box.createVerticalStrut(12));

        JLabel author = new JLabel("Abdoullah Ndao · DAUST");
        author.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        author.setForeground(ACCENT);
        author.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel ver = new JLabel("v1.0  ·  Junior II");
        ver.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        ver.setForeground(new Color(51, 65, 85));
        ver.setAlignmentX(Component.LEFT_ALIGNMENT);
        footer.add(author);
        footer.add(Box.createVerticalStrut(3));
        footer.add(ver);
        left.add(footer, BorderLayout.SOUTH);

        return left;
    }

    private JPanel roleCard(String iconKey, String role, String desc, Color bgColor, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(new Color(bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue(), 30));
        card.setBorder(BorderFactory.createCompoundBorder(
            new RoundBorder(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 60), 8),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel iconLbl = new JLabel();
        iconLbl.setIcon(PharmIcons.large(iconKey));
        iconLbl.setForeground(accentColor);
        card.add(iconLbl, BorderLayout.WEST);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel roleLbl = new JLabel(role);
        roleLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        roleLbl.setForeground(accentColor);
        JLabel descLbl = new JLabel("<html><div style='width:170px'>" + desc.replace("\n", "<br>") + "</div></html>");
        descLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        descLbl.setForeground(new Color(148, 163, 184));
        text.add(roleLbl);
        text.add(descLbl);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    // ── RIGHT PANEL ───────────────────────────────────────────────────────────
    private JPanel buildRight() {
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(CONTENT_BG);

        // Top bar: DB status + close button
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CONTENT_BG);
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 16, 0, 10));

        statusBadge = new JLabel("● Connecting…");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        statusBadge.setForeground(TEXT_MUTED);
        topBar.add(statusBadge, BorderLayout.WEST);

        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setForeground(ERROR_RED);
        closeBtn.setBackground(CONTENT_BG);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { closeBtn.setForeground(Color.WHITE); closeBtn.setBackground(ERROR_RED); }
            public void mouseExited(MouseEvent e)  { closeBtn.setForeground(ERROR_RED);   closeBtn.setBackground(CONTENT_BG); }
        });
        closeBtn.addActionListener(e -> System.exit(0));
        topBar.add(closeBtn, BorderLayout.EAST);
        right.add(topBar, BorderLayout.NORTH);

        // ── Form card ──────────────────────────────────────────────────────────
        JPanel formCard = new JPanel();
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(32, 40, 32, 40)));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcome.setForeground(TEXT_DARK);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your PharmTrack account");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username field
        JLabel userLbl = formLabel("Username");
        txtUserName = new JTextField();
        txtUserName.putClientProperty("JTextField.placeholderText", "Enter your username");
        styleFocusField(txtUserName);

        // Password field with show/hide toggle
        JLabel passLbl = formLabel("Password");
        l_password = new JPasswordField();
        styleFocusField(l_password);

        JPanel pwdRow = new JPanel(new BorderLayout(0, 0));
        pwdRow.setBackground(Color.WHITE);
        pwdRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pwdRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel eyeBtn = new JLabel("Show");
        eyeBtn.setFont(new Font("Segoe UI", Font.BOLD, 10));
        eyeBtn.setForeground(TEXT_MUTED);
        eyeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeBtn.setToolTipText("Show / hide password");
        eyeBtn.setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 0));
        eyeBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                passwordVisible = !passwordVisible;
                l_password.setEchoChar(passwordVisible ? (char) 0 : '●');
                eyeBtn.setText(passwordVisible ? "Hide" : "Show");
                eyeBtn.setForeground(passwordVisible ? ACCENT : TEXT_MUTED);
            }
        });
        l_password.setEchoChar('●');
        pwdRow.add(l_password, BorderLayout.CENTER);
        pwdRow.add(eyeBtn, BorderLayout.EAST);

        // Buttons
        btnLogin = new JButton("Sign In  →");
        styleButton(btnLogin, ACCENT, ACCENT_DARK);
        btnLogin.setToolTipText("Press Enter or click to sign in");

        btnClear = new JButton("Clear");
        styleButton(btnClear, new Color(241, 245, 249), new Color(226, 232, 240));
        btnClear.setForeground(TEXT_MUTED);

        btnLogin.addActionListener(e -> btnLoginMouseClicked());
        btnClear.addActionListener(e -> btnClearMouseClicked());
        l_password.addActionListener(e -> btnLoginMouseClicked());
        txtUserName.addActionListener(e -> l_password.requestFocusInWindow());

        // Clear inline error as soon as the user starts typing again
        javax.swing.event.DocumentListener clearErr = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { setError(" "); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { setError(" "); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        };
        txtUserName.getDocument().addDocumentListener(clearErr);
        l_password.getDocument().addDocumentListener(clearErr);

        // Enter on username moves to password; Escape clears
        KeyStroke esc = KeyStroke.getKeyStroke("ESCAPE");
        txtUserName.getInputMap(JComponent.WHEN_FOCUSED).put(esc, "clr");
        l_password.getInputMap(JComponent.WHEN_FOCUSED).put(esc, "clr");
        javax.swing.AbstractAction clrAction = new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { btnClearMouseClicked(); }
        };
        txtUserName.getActionMap().put("clr", clrAction);
        l_password.getActionMap().put("clr", clrAction);

        // Inline error label — hidden until a login attempt fails
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        errorLabel.setForeground(ERROR_RED);
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(welcome);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(sub);
        formCard.add(Box.createVerticalStrut(28));
        formCard.add(userLbl);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(txtUserName);
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(passLbl);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(pwdRow);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(errorLabel);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(btnLogin);
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(btnClear);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(CONTENT_BG);
        centerWrapper.add(formCard);
        right.add(centerWrapper, BorderLayout.CENTER);
        return right;
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private JLabel formLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleFocusField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            BorderFactory.createEmptyBorder(7, 11, 7, 11)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_FOCUS, 2),
                    BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            }
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR),
                    BorderFactory.createEmptyBorder(7, 11, 7, 11)));
            }
        });
    }

    private void styleButton(JButton btn, Color bg, Color hover) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
    }

    private void checkDbConnection() {
        new Thread(() -> {
            try {
                DatabaseHelper.getConnection().close();
                SwingUtilities.invokeLater(() -> {
                    statusBadge.setText("● Connected");
                    statusBadge.setForeground(ACCENT);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    statusBadge.setText("● Database offline");
                    statusBadge.setForeground(ERROR_RED);
                });
            }
        }).start();
    }

    // ── Event handlers ────────────────────────────────────────────────────────

    private void btnLoginMouseClicked() {
        String user = txtUserName.getText().trim();
        String pwd  = new String(l_password.getPassword());

        if (user.isEmpty() || pwd.isEmpty()) {
            shakeField(user.isEmpty() ? txtUserName : l_password);
            setError(user.isEmpty() ? "Username is required." : "Password is required.");
            return;
        }

        // Loading state — runs query off the EDT so the UI stays responsive
        setError(" ");
        btnLogin.setText("Signing in…");
        btnLogin.setEnabled(false);
        btnClear.setEnabled(false);

        new Thread(() -> {
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT A_ROLE FROM AGENTS WHERE A_NAME = ? AND A_PASSWORD = ?")) {
                ps.setString(1, user);
                ps.setString(2, pwd);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String role = rs.getString("A_ROLE");
                        SwingUtilities.invokeLater(() -> {
                            new DashboardFrame(role).setVisible(true);
                            dispose();
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            shakeField(txtUserName);
                            shakeField(l_password);
                            setError("Incorrect username or password.");
                            markFieldError(txtUserName);
                            markFieldError(l_password);
                            resetButton();
                        });
                    }
                }
            } catch (SQLException e) {
                SwingUtilities.invokeLater(() -> {
                    setError("Database error — check your connection.");
                    resetButton();
                });
            }
        }).start();
    }

    private void setError(String msg) {
        errorLabel.setText(msg == null || msg.isEmpty() ? " " : msg);
    }

    private void markFieldError(JComponent f) {
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ERROR_RED, 2),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }

    private void resetButton() {
        btnLogin.setText("Sign In  →");
        btnLogin.setEnabled(true);
        btnClear.setEnabled(true);
    }

    private void btnClearMouseClicked() {
        txtUserName.setText("");
        l_password.setText("");
        setError(" ");
        styleFocusField(txtUserName);
        styleFocusField(l_password);
        txtUserName.requestFocus();
    }

    // Brief horizontal shake animation on the given field
    private void shakeField(JComponent field) {
        Point orig = field.getLocation();
        Timer t = new Timer(30, null);
        int[] step = {0};
        int[] offsets = {-8, 8, -6, 6, -4, 4, -2, 2, 0};
        t.addActionListener(e -> {
            if (step[0] < offsets.length) {
                field.setLocation(orig.x + offsets[step[0]], orig.y);
                step[0]++;
            } else {
                field.setLocation(orig);
                t.stop();
            }
        });
        t.start();
    }

    // ── Rounded border helper ─────────────────────────────────────────────────
    private static class RoundBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        RoundBorder(Color c, int r) { color = c; radius = r; }
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(4, 4, 4, 4); }
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
