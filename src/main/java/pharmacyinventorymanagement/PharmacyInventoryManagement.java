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
            com.formdev.flatlaf.FlatLightLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }
        DatabaseHelper.initializeDatabase();
        new SplashFrame().startApp();
    }
    
}
