package pharmacyinventorymanagement;

// App entry point: sets up FlatLaf theme, creates DB tables on first launch, and shows the splash screen.
public class PharmacyInventoryManagement {

    public static void main(String[] args) {
        try {
            // Apply FlatLaf IntelliJ look-and-feel for a modern UI
            com.formdev.flatlaf.FlatIntelliJLaf.setup();
            // Customize component styles — green accent, rounded corners, thin scrollbar
            javax.swing.UIManager.put("Component.accentColor", new java.awt.Color(16, 185, 129));
            javax.swing.UIManager.put("Button.arc", 6);
            javax.swing.UIManager.put("TextComponent.arc", 4);
            javax.swing.UIManager.put("Component.focusWidth", 1);
            javax.swing.UIManager.put("ScrollBar.thumbArc", 999);
            javax.swing.UIManager.put("ScrollBar.width", 8);
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }
        // Create all 5 tables and seed the default admin account if not already done
        DatabaseHelper.initializeDatabase();
        // Show splash screen which transitions to LoginFrame when done
        new SplashFrame().startApp();
    }
    
}
