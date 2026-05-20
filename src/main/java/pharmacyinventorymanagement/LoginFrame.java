package pharmacyinventorymanagement;

/**
 * LoginFrame provides role-based authentication for the Pharmacy Tracking System.
 * It queries the AGENTS table using a parameterised PreparedStatement to prevent
 * SQL injection, retrieves the user's role (Admin / Pharmacist / Technician),
 * and routes them to the appropriate DashboardFrame with the correct permissions.
 *
 * @author Abdoullah Ndao
 */

import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class LoginFrame extends javax.swing.JFrame {

    // ── Design constants ──────────────────────────────────────────────────────
    private static final Color SIDEBAR_BG  = new Color(30, 41, 59);
    private static final Color ACCENT      = new Color(16, 185, 129);
    private static final Color ACCENT_DARK = new Color(5, 150, 105);
    private static final Color CONTENT_BG  = new Color(248, 250, 252);
    private static final Color TEXT_DARK   = new Color(30, 41, 59);
    private static final Color TEXT_MUTED  = new Color(100, 116, 139);
    private static final Color BORDER_CLR  = new Color(203, 213, 225);

    // ── State ─────────────────────────────────────────────────────────────────
    Connection Con = null;
    Statement St = null;
    ResultSet Rs = null;

    public LoginFrame() {
        initComponents();
        // Focus username field immediately on open
        SwingUtilities.invokeLater(() -> txtUserName.requestFocusInWindow());
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(700, 440);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(BorderFactory.createLineBorder(ACCENT, 2));
        setContentPane(root);

        // Drag-to-move for undecorated window
        final Point[] dragStart = {null};
        root.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragStart[0] = e.getLocationOnScreen(); }
        });
        root.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (dragStart[0] != null) {
                    Point loc = getLocation();
                    Point cur = e.getLocationOnScreen();
                    setLocation(loc.x + cur.x - dragStart[0].x, loc.y + cur.y - dragStart[0].y);
                    dragStart[0] = cur;
                }
            }
        });

        // ── LEFT branding panel ───────────────────────────────────────────────
        JPanel left = new JPanel();
        left.setBackground(SIDEBAR_BG);
        left.setPreferredSize(new Dimension(280, 440));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(60, 30, 40, 30));

        JLabel iconLbl = new JLabel("⚕");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLbl.setForeground(ACCENT);
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("PHARMA SYSTEM");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagline = new JLabel("<html><div style='text-align:center'>Smart Inventory<br>Management</div></html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tagline.setForeground(new Color(148, 163, 184));
        tagline.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel author = new JLabel("Abdoullah Ndao · DAUST");
        author.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        author.setForeground(ACCENT);
        author.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel version = new JLabel("v1.0  ·  Junior II");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(71, 85, 105));
        version.setAlignmentX(Component.CENTER_ALIGNMENT);

        left.add(Box.createVerticalGlue());
        left.add(iconLbl);
        left.add(Box.createVerticalStrut(12));
        left.add(appName);
        left.add(Box.createVerticalStrut(10));
        left.add(tagline);
        left.add(Box.createVerticalGlue());
        left.add(author);
        left.add(Box.createVerticalStrut(4));
        left.add(version);

        root.add(left, BorderLayout.WEST);

        // ── RIGHT login panel ─────────────────────────────────────────────────
        JPanel right = new JPanel(new BorderLayout());
        right.setBackground(CONTENT_BG);

        // Close button top-right
        JButton closeBtn = new JButton("✕");
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setForeground(new Color(239, 68, 68));
        closeBtn.setBackground(CONTENT_BG);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { closeBtn.setForeground(Color.WHITE); closeBtn.setBackground(new Color(239, 68, 68)); }
            public void mouseExited(MouseEvent e)  { closeBtn.setForeground(new Color(239, 68, 68)); closeBtn.setBackground(CONTENT_BG); }
        });
        closeBtn.addActionListener(e -> System.exit(0));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        topBar.setBackground(CONTENT_BG);
        topBar.add(closeBtn);
        right.add(topBar, BorderLayout.NORTH);

        // Form card
        JPanel formCard = new JPanel();
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(30, 36, 30, 36)
        ));
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));

        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 22));
        welcome.setForeground(TEXT_DARK);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Sign in to your account");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLbl = new JLabel("Username");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLbl.setForeground(TEXT_MUTED);
        userLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUserName = new JTextField();
        styleField(txtUserName);

        JLabel passLbl = new JLabel("Password");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLbl.setForeground(TEXT_MUTED);
        passLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        l_password = new JPasswordField();
        styleField(l_password);

        btnLogin = new JButton("Sign In");
        styleButton(btnLogin, ACCENT, ACCENT_DARK);
        btnLogin.setToolTipText("Sign in with your username and password");

        btnClear = new JButton("Clear");
        styleButton(btnClear, new Color(226, 232, 240), new Color(203, 213, 225));
        btnClear.setForeground(TEXT_DARK);
        btnClear.setToolTipText("Clear both fields");

        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) { btnLoginMouseClicked(evt); }
        });
        btnClear.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) { btnClearMouseClicked(evt); }
        });
        // Enter on password field triggers login
        l_password.addActionListener(e -> btnLoginMouseClicked(null));
        // Escape clears both fields
        l_password.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ESCAPE"), "clear");
        l_password.getActionMap().put("clear", new javax.swing.AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) { btnClearMouseClicked(null); }
        });

        formCard.add(welcome);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(sub);
        formCard.add(Box.createVerticalStrut(22));
        formCard.add(userLbl);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(txtUserName);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(passLbl);
        formCard.add(Box.createVerticalStrut(6));
        formCard.add(l_password);
        formCard.add(Box.createVerticalStrut(20));
        formCard.add(btnLogin);
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(btnClear);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(CONTENT_BG);
        centerWrapper.add(formCard);
        right.add(centerWrapper, BorderLayout.CENTER);

        root.add(right, BorderLayout.CENTER);
    }

    // ── Styling helpers ───────────────────────────────────────────────────────

    private void styleField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleButton(JButton btn, Color bg, Color hover) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
    }

    // ── Event handlers ────────────────────────────────────────────────────────

    private void btnLoginMouseClicked(MouseEvent evt) {
        String pwd = new String(l_password.getPassword());
        // Use PreparedStatement to prevent SQL injection
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT * FROM AGENTS WHERE A_NAME = ? AND A_PASSWORD = ?")) {
            pstmt.setString(1, txtUserName.getText());
            pstmt.setString(2, pwd);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("A_ROLE");
                    new DashboardFrame(role).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void btnClearMouseClicked(MouseEvent evt) {
        txtUserName.setText("");
        l_password.setText("");
        txtUserName.requestFocus();
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // ── Field declarations ────────────────────────────────────────────────────
    private JTextField txtUserName;
    private JPasswordField l_password;
    private JButton btnLogin;
    private JButton btnClear;
}
