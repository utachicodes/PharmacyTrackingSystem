package pharmacyinventorymanagement;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

/**
 * Centralized UI component factory — ensures consistent sizing, styling, and spacing across all frames.
 * Eliminates the scattered component creation that was causing the "slop" appearance.
 */
public class UIHelper {

    // ─────────────────────── COLOR PALETTE ──────────────────────────
    public static final Color SIDEBAR_BG     = new Color(30, 41, 59);
    public static final Color SIDEBAR_HOVER  = new Color(51, 65, 85);
    public static final Color ACCENT         = new Color(16, 185, 129);
    public static final Color ACCENT_DARK    = new Color(5, 150, 105);
    public static final Color CONTENT_BG     = new Color(241, 245, 249);
    public static final Color TEXT_DARK      = new Color(30, 41, 59);
    public static final Color TEXT_MUTED     = new Color(100, 116, 139);
    public static final Color BORDER_CLR     = new Color(203, 213, 225);
    public static final Color DANGER         = new Color(239, 68, 68);
    public static final Color DANGER_DARK    = new Color(185, 28, 28);
    public static final Color DANGER_BG      = new Color(255, 220, 220);
    public static final Color WARNING_BG     = new Color(255, 255, 204);
    public static final Color SUCCESS_BG     = new Color(220, 252, 231);

    // ─────────────────── STANDARD SIZES ───────────────────────────
    public static final int SIDEBAR_WIDTH    = 185;
    public static final int HEADER_HEIGHT    = 60;
    public static final int BUTTON_HEIGHT    = 42;
    public static final int FIELD_HEIGHT     = 36;
    public static final int COMBO_HEIGHT     = 36;

    public static final int PADDING_SMALL    = 8;
    public static final int PADDING_MEDIUM   = 12;
    public static final int PADDING_LARGE    = 16;
    public static final int PADDING_XL       = 24;

    public static final int SPACING_TIGHT    = 4;
    public static final int SPACING_NORMAL   = 8;
    public static final int SPACING_LOOSE    = 12;

    // ─────────────────── STANDARD FONTS ───────────────────────────
    public static final Font FONT_HEADER     = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_TITLE      = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_SUBTITLE   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BODY       = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_SMALL      = new Font("Segoe UI", Font.PLAIN, 10);
    public static final Font FONT_LABEL      = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_LOGO       = new Font("Segoe UI", Font.BOLD, 15);

    // ═════════════════════════════════════════════════════════════════════
    // ─ TEXT FIELD CREATION
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Creates a standardized text field with consistent height and styling.
     */
    public static JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setFont(FONT_BODY);
        tf.setForeground(TEXT_DARK);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(SPACING_NORMAL - 2, SPACING_NORMAL, SPACING_NORMAL - 2, SPACING_NORMAL)
        ));
        tf.setPreferredSize(new Dimension(0, FIELD_HEIGHT));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        return tf;
    }

    /**
     * Creates a standardized text field with value (no placeholder).
     */
    public static JTextField createTextField() {
        return createTextField("");
    }

    /**
     * Creates a standardized password field.
     */
    public static JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(FONT_BODY);
        pf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(SPACING_NORMAL - 2, SPACING_NORMAL, SPACING_NORMAL - 2, SPACING_NORMAL)
        ));
        pf.setPreferredSize(new Dimension(0, FIELD_HEIGHT));
        pf.setMaximumSize(new Dimension(Integer.MAX_VALUE, FIELD_HEIGHT));
        return pf;
    }

    // ═════════════════════════════════════════════════════════════════════
    // ─ BUTTON CREATION
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Creates a primary action button (green, prominent).
     */
    public static JButton createPrimaryButton(String label) {
        return createButton(label, ACCENT, ACCENT_DARK);
    }

    /**
     * Creates a secondary action button (blue).
     */
    public static JButton createSecondaryButton(String label) {
        return createButton(label, new Color(59, 130, 246), new Color(37, 99, 235));
    }

    /**
     * Creates a danger action button (red).
     */
    public static JButton createDangerButton(String label) {
        return createButton(label, DANGER, DANGER_DARK);
    }

    /**
     * Creates a neutral/cancel button (gray).
     */
    public static JButton createNeutralButton(String label) {
        return createButton(label, new Color(100, 116, 139), new Color(71, 85, 105));
    }

    /**
     * Creates a custom-colored button.
     */
    public static JButton createButton(String label, Color baseColor, Color hoverColor) {
        JButton btn = new JButton(label);
        btn.setFont(FONT_LABEL);
        btn.setForeground(Color.WHITE);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(true);
        btn.setPreferredSize(new Dimension(100, BUTTON_HEIGHT));
        btn.setMaximumSize(new Dimension(100, BUTTON_HEIGHT));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(baseColor);
            }
        });

        return btn;
    }

    // ═════════════════════════════════════════════════════════════════════
    // ─ COMBOBOX CREATION
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Creates a standardized combo box.
     */
    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> combo = new JComboBox<>(items);
        combo.setFont(FONT_BODY);
        combo.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1));
        combo.setPreferredSize(new Dimension(0, COMBO_HEIGHT));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, COMBO_HEIGHT));
        combo.setBackground(Color.WHITE);
        combo.setForeground(TEXT_DARK);
        return combo;
    }

    /**
     * Creates an empty standardized combo box (for dynamic population).
     */
    public static <T> JComboBox<T> createComboBox() {
        return createComboBox((T[]) new Object[0]);
    }

    // ═════════════════════════════════════════════════════════════════════
    // ─ LABEL CREATION
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Creates a form field label.
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setForeground(TEXT_DARK);
        return label;
    }

    /**
     * Creates a header title label.
     */
    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_HEADER);
        label.setForeground(TEXT_DARK);
        return label;
    }

    /**
     * Creates a subtitle label.
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_MUTED);
        return label;
    }

    /**
     * Creates a body text label.
     */
    public static JLabel createBodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(TEXT_DARK);
        return label;
    }

    // ═════════════════════════════════════════════════════════════════════
    // ─ PANEL CREATION
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Creates a white card panel with proper border and padding.
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1),
            BorderFactory.createEmptyBorder(PADDING_LARGE, PADDING_LARGE, PADDING_LARGE, PADDING_LARGE)
        ));
        return panel;
    }

    /**
     * Creates a header panel (white background with bottom border).
     */
    public static JPanel createHeaderPanel(String title, String subtitle) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR),
            BorderFactory.createEmptyBorder(0, PADDING_LARGE, 0, PADDING_LARGE)
        ));

        JLabel titleLabel = createHeaderLabel(title);
        JLabel subtitleLabel = createSubtitleLabel(subtitle);

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setBackground(Color.WHITE);
        titleBox.add(titleLabel);
        titleBox.add(subtitleLabel);

        panel.add(titleBox, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Creates a sidebar with standard dark background.
     */
    public static JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        return sidebar;
    }

    /**
     * Creates a horizontal separator line.
     */
    public static JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(51, 65, 85));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    /**
     * Creates a sidebar navigation label.
     */
    public static JLabel createNavLabel(String text) {
        JLabel nav = new JLabel("  " + text);
        nav.setFont(FONT_LABEL);
        nav.setForeground(new Color(148, 163, 184));
        nav.setBackground(SIDEBAR_BG);
        nav.setOpaque(true);
        nav.setBorder(BorderFactory.createEmptyBorder(SPACING_NORMAL - 2, SPACING_NORMAL, SPACING_NORMAL - 2, SPACING_NORMAL));
        nav.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        nav.setCursor(new Cursor(Cursor.HAND_CURSOR));

        nav.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                nav.setBackground(SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                nav.setBackground(SIDEBAR_BG);
            }
        });

        return nav;
    }

    /**
     * Creates an active state indicator label (for showing current module).
     */
    public static JLabel createActiveNavLabel(String text) {
        JLabel label = new JLabel("  " + text);
        label.setFont(FONT_LABEL);
        label.setForeground(ACCENT);
        label.setBackground(new Color(6, 78, 59));
        label.setOpaque(true);
        label.setBorder(BorderFactory.createEmptyBorder(SPACING_NORMAL - 2, SPACING_NORMAL, SPACING_NORMAL - 2, SPACING_NORMAL));
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return label;
    }

    // ═════════════════════════════════════════════════════════════════════
    // ─ FORM LAYOUT HELPERS
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Adds a row of components to a GridBagLayout panel with consistent spacing.
     * Pattern: label1 | field1 | label2 | field2
     */
    public static void addFormRow(JPanel panel, GridBagConstraints g, int row,
                                  String label1, JComponent field1,
                                  String label2, JComponent field2) {
        g.gridy = row;
        g.insets = new Insets(SPACING_NORMAL, SPACING_NORMAL, SPACING_NORMAL, SPACING_NORMAL);

        // Label 1
        g.gridx = 0;
        g.weightx = 0;
        panel.add(createLabel(label1), g);

        // Field 1
        g.gridx = 1;
        g.weightx = 1;
        panel.add(field1, g);

        // Label 2
        g.gridx = 2;
        g.weightx = 0;
        panel.add(createLabel(label2), g);

        // Field 2
        g.gridx = 3;
        g.weightx = 1;
        panel.add(field2, g);
    }

    /**
     * Adds a single-field row to a GridBagLayout panel.
     * Pattern: label | field (spanning remaining space)
     */
    public static void addFormRowSingle(JPanel panel, GridBagConstraints g, int row,
                                         String label, JComponent field) {
        g.gridy = row;
        g.insets = new Insets(SPACING_NORMAL, SPACING_NORMAL, SPACING_NORMAL, SPACING_NORMAL);
        g.fill = GridBagConstraints.HORIZONTAL;

        // Label
        g.gridx = 0;
        g.weightx = 0;
        panel.add(createLabel(label), g);

        // Field spanning 3 columns
        g.gridx = 1;
        g.weightx = 1;
        g.gridwidth = 3;
        panel.add(field, g);
        g.gridwidth = 1;
    }

    // ═════════════════════════════════════════════════════════════════════
    // ─ TABLE STYLING
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Applies standard styling to a table.
     */
    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(32);
        table.setGridColor(BORDER_CLR);
        table.setForeground(TEXT_DARK);
        table.setBackground(Color.WHITE);
        table.getTableHeader().setFont(FONT_LABEL);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(TEXT_DARK);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDER_CLR));
        table.setSelectionBackground(new Color(191, 219, 254));
        table.setSelectionForeground(TEXT_DARK);
    }

    // ═════════════════════════════════════════════════════════════════════
    // ─ DIALOG & MESSAGE HELPERS
    // ═════════════════════════════════════════════════════════════════════

    /**
     * Shows a styled information message.
     */
    public static void showInfo(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a styled error message.
     */
    public static void showError(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a styled warning message.
     */
    public static void showWarning(JFrame parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Shows a confirmation dialog.
     */
    public static boolean showConfirmation(JFrame parent, String title, String message) {
        return JOptionPane.showConfirmDialog(parent, message, title, JOptionPane.YES_NO_OPTION)
            == JOptionPane.YES_OPTION;
    }
}
