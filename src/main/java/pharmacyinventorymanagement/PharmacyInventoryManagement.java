/*
 */
package pharmacyinventorymanagement;

/**
 *
 * @author Abdoullah Ndao
 */
/**
 * PharmacyInventoryManagement is the application entry point.
 * It initialises the FlatLaf look-and-feel, triggers the auto-create database
 * schema via {@link DatabaseHelper#initializeDatabase()}, and launches the
 * {@link SplashFrame} which transitions to the login screen after loading.
 */
public class PharmacyInventoryManagement {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            com.formdev.flatlaf.FlatIntelliJLaf.setup();
            // Global accent matching the system green theme
            javax.swing.UIManager.put("Component.accentColor", new java.awt.Color(16, 185, 129));
            javax.swing.UIManager.put("Button.arc", 6);
            javax.swing.UIManager.put("TextComponent.arc", 4);
            javax.swing.UIManager.put("Component.focusWidth", 1);
            javax.swing.UIManager.put("ScrollBar.thumbArc", 999);
            javax.swing.UIManager.put("ScrollBar.width", 8);
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }
        DatabaseHelper.initializeDatabase();
        new SplashFrame().startApp();
    }
    
}
