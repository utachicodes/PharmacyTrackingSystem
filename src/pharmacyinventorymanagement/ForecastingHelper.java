package pharmacyinventorymanagement;

import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class ForecastingHelper {

    /**
     * Predicts demand for a medicine based on past sales.
     * Uses a simple moving average or trend analysis.
     */
    public static int predictDemand(String medicineName) {
        int totalQty = 0;
        int count = 0;
        
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT S_QTY FROM User1.SALES WHERE S_MED_NAME = ? AND S_DATE >= ?")) {
            
            // Look at the last 30 days of sales
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
            pstmt.setString(1, medicineName);
            pstmt.setDate(2, java.sql.Date.valueOf(thirtyDaysAgo));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    totalQty += rs.getInt("S_QTY");
                    count++;
                }
            }
            
            if (count == 0) return 0;
            
            // Simple forecast: average daily sales * next 7 days
            double avgDaily = (double) totalQty / 30.0;
            return (int) Math.ceil(avgDaily * 7);
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Identifies medicines that are likely to go out of stock soon.
     */
    public static List<String> getLowStockAlerts() {
        List<String> alerts = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT M_NAME, M_QUANTITY FROM User1.MEDICINE")) {
            
            while (rs.next()) {
                String name = rs.getString("M_NAME");
                int currentQty = rs.getInt("M_QUANTITY");
                int predicted = predictDemand(name);
                
                if (currentQty < predicted || currentQty < 10) {
                    alerts.add(name + " (Current: " + currentQty + ", Needed: " + predicted + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    /**
     * Identifies medicines that are expiring within the next 30 days.
     */
    public static List<String> getExpirationAlerts() {
        List<String> alerts = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT M_NAME, M_EXPDATE FROM User1.MEDICINE")) {
            
            LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
            while (rs.next()) {
                String name = rs.getString("M_NAME");
                Date expDate = rs.getDate("M_EXPDATE");
                if (expDate != null) {
                    LocalDate expiry = expDate.toLocalDate();
                    if (expiry.isBefore(thirtyDaysFromNow)) {
                        alerts.add("EXPIRY: " + name + " (Expires: " + expiry + ")");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }
}
