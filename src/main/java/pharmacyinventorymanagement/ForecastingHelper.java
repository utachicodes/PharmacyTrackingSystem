package pharmacyinventorymanagement;

// Demand forecasting (30-day SMA), low-stock alerts, expiry alerts, and inventory value calculation.

import java.sql.*;
import java.util.*;
import java.time.LocalDate;

public class ForecastingHelper {

    private static final int SALES_WINDOW_DAYS    = 30; // look-back window for SMA
    private static final int FORECAST_HORIZON_DAYS = 7; // days ahead to forecast

    // Predicts how many units of a medicine will be needed in the next 7 days using a 30-day SMA.
    public static int predictDemand(String medicineName) {
        int totalQty = 0;
        int count = 0;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT S_QTY FROM SALES WHERE S_MED_NAME = ? AND S_DATE >= ?")) {

            // Look at all sales of this medicine in the last 30 days
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(SALES_WINDOW_DAYS);
            pstmt.setString(1, medicineName);
            pstmt.setDate(2, java.sql.Date.valueOf(thirtyDaysAgo));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    totalQty += rs.getInt("S_QTY");
                    count++;
                }
            }

            // No sales history — cannot make a forecast
            if (count == 0) return 0;

            // SMA: average daily sales over 30 days × 7-day forecast horizon
            double avgDaily = (double) totalQty / (double) SALES_WINDOW_DAYS;
            return (int) Math.ceil(avgDaily * FORECAST_HORIZON_DAYS);

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Returns alerts for medicines below their reorder threshold or below 7-day SMA demand.
    public static List<String> getLowStockAlerts() {
        List<String> alerts = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT M_NAME, M_QUANTITY, M_THRESHOLD FROM MEDICINE")) {

            while (rs.next()) {
                String name    = rs.getString("M_NAME");
                int currentQty = rs.getInt("M_QUANTITY");
                int threshold  = rs.getInt("M_THRESHOLD"); // per-medicine reorder point
                int predicted  = predictDemand(name);      // 7-day SMA forecast

                // Alert if stock is below the SMA forecast or below the reorder threshold
                if (currentQty < predicted || currentQty < threshold) {
                    alerts.add(name + " (Current: " + currentQty + ", Threshold: " + threshold + ", Needed: " + predicted + ")");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alerts;
    }

    // Returns alerts for medicines expiring within the next 30 days.
    public static List<String> getExpirationAlerts() {
        List<String> alerts = new ArrayList<>();
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT M_NAME, M_EXPDATE FROM MEDICINE")) {

            LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(SALES_WINDOW_DAYS);
            while (rs.next()) {
                String name = rs.getString("M_NAME");
                java.sql.Date expDate = rs.getDate("M_EXPDATE");
                if (expDate != null) {
                    LocalDate expiry = expDate.toLocalDate();
                    // isBefore covers both already-expired and near-expiry items
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

    // Returns the total number of medicines in the inventory.
    public static int getMedicineCount() {
        try (java.sql.Connection conn = DatabaseHelper.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM MEDICINE")) {
            if (rs.next()) return rs.getInt(1);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Returns total inventory value (quantity × unit cost across all medicines).
    public static double getInventoryValue() {
        double totalValue = 0;
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT M_QUANTITY, M_UNIT_COST FROM MEDICINE")) {

            while (rs.next()) {
                totalValue += rs.getInt("M_QUANTITY") * rs.getDouble("M_UNIT_COST");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalValue;
    }
}
