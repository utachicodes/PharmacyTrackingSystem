package pharmacyinventorymanagement;

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
    private static final String USER = "root";
    private static final String PASS = ""; // Update this with your MySQL password

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }

    public static DefaultTableModel resultSetToTableModel(ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();

            // Get the column names
            for (int column = 1; column <= numberOfColumns; column++) {
                columnNames.add(metaData.getColumnLabel(column));
            }

            // Get all rows
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

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Create MEDICINE table if it doesn't exist
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

            // Create AGENTS table
            stmt.execute("CREATE TABLE IF NOT EXISTS AGENTS (" +
                    "A_ID INT PRIMARY KEY, " +
                    "A_NAME VARCHAR(50), " +
                    "A_AGE INT, " +
                    "A_PASSWORD VARCHAR(50), " +
                    "A_PHONE VARCHAR(20), " +
                    "A_GENDER VARCHAR(10), " +
                    "A_EMAIL VARCHAR(50), " +
                    "A_ROLE VARCHAR(20) DEFAULT 'Technician')");

            // Create SALES table
            stmt.execute("CREATE TABLE IF NOT EXISTS SALES (" +
                    "S_ID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "S_MED_NAME VARCHAR(100), " +
                    "S_DATE DATE, " +
                    "S_QTY INT, " +
                    "S_TOTAL DOUBLE)");

            // Create PURCHASE_ORDERS table
            stmt.execute("CREATE TABLE IF NOT EXISTS PURCHASE_ORDERS (" +
                    "PO_ID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "PO_MED_NAME VARCHAR(100), " +
                    "PO_SUPPLIER VARCHAR(100), " +
                    "PO_QTY INT, " +
                    "PO_STATUS VARCHAR(20) DEFAULT 'Pending', " +
                    "PO_DATE DATE)");

            // Create COMPANY table
            stmt.execute("CREATE TABLE IF NOT EXISTS COMPANY (" +
                    "C_ID INT PRIMARY KEY, " +
                    "C_NAME VARCHAR(50), " +
                    "C_ADDRESS VARCHAR(100), " +
                    "C_EXP INT, " +
                    "C_PHONE VARCHAR(20), " +
                    "C_EMAIL VARCHAR(50), " +
                    "C_LEADTIME INT DEFAULT 7, " +
                    "C_PREFERRED VARCHAR(10) DEFAULT 'No')");

            // Insert default admin if AGENTS table is empty
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
