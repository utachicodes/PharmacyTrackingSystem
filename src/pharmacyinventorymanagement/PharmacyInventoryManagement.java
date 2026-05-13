/*
 */
package pharmacyinventorymanagement;

/**
 *
 * @author Abdoullah Ndao
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
