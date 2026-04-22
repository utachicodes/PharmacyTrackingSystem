/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
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
        DatabaseHelper.initializeDatabase();
        new SplashFrame().startApp();
    }
    
}
