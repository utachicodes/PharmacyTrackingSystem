package pharmacyinventorymanagement;

import java.awt.*;
import javax.swing.*;

/**
 * SplashFrame displays the application loading screen with an animated progress
 * bar while the system initialises. It uses a {@link javax.swing.SwingWorker} to
 * drive the animation off the Event Dispatch Thread, then transitions to
 * {@link LoginFrame} once loading is complete.
 *
 * @author Abdoullah Ndao
 */
public class SplashFrame extends javax.swing.JFrame {

    // Design system colours
    private static final Color SIDEBAR_BG   = new Color(30, 41, 59);
    private static final Color ACCENT       = new Color(16, 185, 129);
    private static final Color TEXT_MUTED   = new Color(100, 116, 139);

    public SplashFrame() {
        initComponents();
    }

    public void startApp() {
        this.setVisible(true);
        new javax.swing.SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(25);
                    publish(i);
                }
                return null;
            }
            @Override
            protected void process(java.util.List<Integer> chunks) {
                int latest = chunks.get(chunks.size() - 1);
                progressBar.setValue(latest);
                percentage.setText(latest + "%");
            }
            @Override
            protected void done() {
                new LoginFrame().setVisible(true);
                dispose();
            }
        }.execute();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(500, 300);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(SIDEBAR_BG);
        root.setBorder(BorderFactory.createLineBorder(ACCENT, 2));
        setContentPane(root);

        // ── CENTER: logo + titles ──────────────────────────────────────────────
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 20, 30));

        JLabel logoLabel = new JLabel("⚕ PHARMA SYSTEM");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Pharmacy Tracking System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_MUTED);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel authorLabel = new JLabel("by Abdoullah Ndao");
        authorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        authorLabel.setForeground(ACCENT);
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerPanel.add(logoLabel);
        centerPanel.add(Box.createVerticalStrut(8));
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(authorLabel);

        root.add(centerPanel, BorderLayout.CENTER);

        // ── SOUTH: progress bar + percentage ──────────────────────────────────
        JPanel southPanel = new JPanel();
        southPanel.setOpaque(false);
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        southPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 16, 20));

        percentage = new JLabel("0%");
        percentage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        percentage.setForeground(Color.WHITE);
        percentage.setAlignmentX(Component.CENTER_ALIGNMENT);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setForeground(ACCENT);
        progressBar.setBackground(new Color(51, 65, 85));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(460, 6));
        progressBar.setMaximumSize(new Dimension(Short.MAX_VALUE, 6));
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);

        southPanel.add(percentage);
        southPanel.add(Box.createVerticalStrut(4));
        southPanel.add(progressBar);

        root.add(southPanel, BorderLayout.SOUTH);
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new SplashFrame().startApp());
    }

    // Field declarations
    private JLabel percentage;
    private JProgressBar progressBar;
}
