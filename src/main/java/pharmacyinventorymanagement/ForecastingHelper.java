package pharmacyinventorymanagement;

import java.sql.*;
import java.util.*;
import java.time.LocalDate;

/**
 * ForecastingHelper provides analytics and alerting services for the pharmacy
 * inventory. It implements a Simple Moving Average (SMA) demand forecasting
 * algorithm over a configurable trailing window (default 30 days), identifies
 * medicines at risk of stockout using per-medicine reorder thresholds, flags
 * items expiring within 30 days, and calculates the total current inventory value.
 */
public class ForecastingHelper {

    /**
     * Number of trailing calendar days used as the SMA calculation window.
     * Increase this value to smooth out demand spikes; decrease for more
     * responsiveness to recent sales trends.
     */
    private static final int SALES_WINDOW_DAYS = 30;

    /**
     * Number of days ahead to forecast demand (the forecast horizon).
     * A 7-day window aligns with weekly restocking cycles.
     */
    private static final int FORECAST_HORIZON_DAYS = 7;

    /**
     * Predicts demand for a medicine based on past sales.
     * Uses a simple moving average or trend analysis.
     */
    public static int predictDemand(String medicineName) {
        int totalQty = 0;
        int count = 0;

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT S_QTY FROM SALES WHERE S_MED_NAME = ? AND S_DATE >= ?")) {

            // SMA window: look at sales transactions in the last 30 days
            LocalDate thirtyDaysAgo = LocalDate.now().minusDays(SALES_WINDOW_DAYS);
            pstmt.setString(1, medicineName);
            pstmt.setDate(2, java.sql.Date.valueOf(thirtyDaysAgo));

            // Accumulate total units sold across all matching transactions
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    totalQty += rs.getInt("S_QTY");
                    count++;
                }
            }

            // If no sales history exists, return 0 (no forecast possible)
            if (count == 0) return 0;

            // SMA forecast: average daily sales over the window * forecast horizon
            double avgDaily = (double) totalQty / (double) SALES_WINDOW_DAYS;
            return (int) Math.ceil(avgDaily * FORECAST_HORIZON_DAYS);

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
             // Fetch per-medicine reorder threshold stored in the MEDICINE table
             ResultSet rs = stmt.executeQuery("SELECT M_NAME, M_QUANTITY, M_THRESHOLD FROM MEDICINE")) {

            while (rs.next()) {
                String name = rs.getString("M_NAME");
                int currentQty = rs.getInt("M_QUANTITY");
                // Use the medicine-specific threshold, not a global hardcoded value
                int threshold = rs.getInt("M_THRESHOLD");
                // Get SMA-based demand forecast for the next horizon period
                int predicted = predictDemand(name);

                // Alert if stock is below the SMA forecast OR below the reorder threshold
                if (currentQty < predicted || currentQty < threshold) {
                    alerts.add(name + " (Current: " + currentQty + ", Threshold: " + threshold + ", Needed: " + predicted + ")");
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
             ResultSet rs = stmt.executeQuery("SELECT M_NAME, M_EXPDATE FROM MEDICINE")) {

            // Flag any medicine expiring within the next 30 days
            LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(SALES_WINDOW_DAYS);
            while (rs.next()) {
                String name = rs.getString("M_NAME");
                java.sql.Date expDate = rs.getDate("M_EXPDATE");
                if (expDate != null) {
                    LocalDate expiry = expDate.toLocalDate();
                    // isBefore check covers both expired and near-expiry items
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

    /**
     * Calculates the total value of the inventory.
     */
    /**
     * Returns the total number of distinct medicines currently in the inventory.
     */
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

    public static double getInventoryValue() {
        double totalValue = 0;
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT M_QUANTITY, M_UNIT_COST FROM MEDICINE")) {

            // Sum value = quantity * unit cost for every medicine in stock
            while (rs.next()) {
                totalValue += rs.getInt("M_QUANTITY") * rs.getDouble("M_UNIT_COST");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalValue;
    }
}
