package pharmacyinventorymanagement;

// Provides MySQL connection, schema auto-creation on first launch, and ResultSet-to-table conversion.

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSetMetaData;
import java.util.Vector;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/PharmaDb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";

    // Change USER and PASS to match your local MySQL installation.
    private static final String USER = "root";
    private static final String PASS = "";

    // Opens a new connection to the local MySQL database.
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }

    // Converts a ResultSet into a Swing DefaultTableModel for display in a JTable.
    public static DefaultTableModel resultSetToTableModel(ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();

            // Read column names from result metadata
            for (int column = 1; column <= numberOfColumns; column++) {
                columnNames.add(metaData.getColumnLabel(column));
            }

            // Read each row into a Vector
            Vector<Vector<Object>> rows = new Vector<>();
            while (rs.next()) {
                Vector<Object> newRow = new Vector<>();
                for (int columnIndex = 1; columnIndex <= numberOfColumns; columnIndex++) {
                    newRow.add(rs.getObject(columnIndex));
                }
                rows.add(newRow);
            }

            return new DefaultTableModel(rows, columnNames);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Creates all 5 tables on first run and seeds a default admin if AGENTS is empty.
    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Medicine stock records — 14 fields including batch, category, thresholds
            stmt.execute("CREATE TABLE IF NOT EXISTS MEDICINE (" +
                    "M_ID INT PRIMARY KEY, " +
                    "M_NAME VARCHAR(50), " +
                    "M_QUANTITY INT, " +
                    "M_PRICE DOUBLE, " +
                    "M_EXPDATE DATE, " +
                    "M_MFTDATE DATE, " +
                    "M_COMPANY VARCHAR(50), " +
                    "M_OWNER VARCHAR(50) DEFAULT 'Main', " +
                    "M_CATEGORY VARCHAR(50), " +
                    "M_STRENGTH VARCHAR(20), " +
                    "M_DOSAGE VARCHAR(50), " +
                    "M_UNIT_COST DOUBLE, " +
                    "M_THRESHOLD INT DEFAULT 10, " +
                    "M_BATCH VARCHAR(50))");

            // Staff accounts with role (Admin / Pharmacist / Technician)
            stmt.execute("CREATE TABLE IF NOT EXISTS AGENTS (" +
                    "A_ID INT PRIMARY KEY, " +
                    "A_NAME VARCHAR(50), " +
                    "A_AGE INT, " +
                    "A_PASSWORD VARCHAR(50), " +
                    "A_PHONE VARCHAR(20), " +
                    "A_GENDER VARCHAR(10), " +
                    "A_EMAIL VARCHAR(50), " +
                    "A_ROLE VARCHAR(20) DEFAULT 'Technician')");

            // Sales audit log — used by ForecastingHelper for demand calculations
            stmt.execute("CREATE TABLE IF NOT EXISTS SALES (" +
                    "S_ID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "S_MED_NAME VARCHAR(100), " +
                    "S_DATE DATE, " +
                    "S_QTY INT, " +
                    "S_TOTAL DOUBLE)");

            // Purchase orders with status (Pending / Received)
            stmt.execute("CREATE TABLE IF NOT EXISTS PURCHASE_ORDERS (" +
                    "PO_ID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "PO_MED_NAME VARCHAR(100), " +
                    "PO_SUPPLIER VARCHAR(100), " +
                    "PO_QTY INT, " +
                    "PO_STATUS VARCHAR(20) DEFAULT 'Pending', " +
                    "PO_DATE DATE)");

            // Supplier directory — feeds the Supplier dropdown in Medicine and PO screens
            stmt.execute("CREATE TABLE IF NOT EXISTS COMPANY (" +
                    "C_ID INT PRIMARY KEY, " +
                    "C_NAME VARCHAR(50), " +
                    "C_ADDRESS VARCHAR(100), " +
                    "C_EXP INT, " +
                    "C_PHONE VARCHAR(20), " +
                    "C_EMAIL VARCHAR(50), " +
                    "C_LEADTIME INT DEFAULT 7, " +
                    "C_PREFERRED VARCHAR(10) DEFAULT 'No')");

            // Seed a default admin account so the app works out-of-the-box
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM AGENTS")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO AGENTS (A_ID, A_NAME, A_AGE, A_PASSWORD, A_PHONE, A_GENDER, A_EMAIL, A_ROLE) " +
                            "VALUES (1, 'Admin', 30, 'admin123', '0000000000', 'Other', 'admin@pharma.com', 'Admin')");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
