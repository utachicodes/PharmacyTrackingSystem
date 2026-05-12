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
    private static final String DB_URL = "jdbc:derby://localhost:1527/PharmaDb";
    private static final String USER = "User1";
    private static final String PASS = "User1";

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            // Fallback to embedded if network server is not running
            return DriverManager.getConnection("jdbc:derby:PharmaDb;create=true", USER, PASS);
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
            try {
                stmt.execute("CREATE TABLE MEDICINE (" +
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
            } catch (SQLException e) {
                // Table might already exist, try adding new columns if missing
                String[] newCols = {
                    "M_CATEGORY VARCHAR(50)", "M_STRENGTH VARCHAR(20)", 
                    "M_DOSAGE VARCHAR(50)", "M_UNIT_COST DOUBLE", 
                    "M_THRESHOLD INT DEFAULT 10", "M_BATCH VARCHAR(50)"
                };
                for (String col : newCols) {
                    try {
                        stmt.execute("ALTER TABLE MEDICINE ADD COLUMN " + col);
                    } catch (SQLException ex) { /* Column already exists */ }
                }
            }

            // Create AGENTS table and add ROLE column
            try {
                stmt.execute("CREATE TABLE AGENTS (" +
                        "A_ID INT PRIMARY KEY, " +
                        "A_NAME VARCHAR(50), " +
                        "A_AGE INT, " +
                        "A_PASSWORD VARCHAR(50), " +
                        "A_PHONE VARCHAR(20), " +
                        "A_GENDER VARCHAR(10), " +
                        "A_EMAIL VARCHAR(50), " +
                        "A_ROLE VARCHAR(20) DEFAULT 'Technician')");
            } catch (SQLException e) {
                try {
                    stmt.execute("ALTER TABLE AGENTS ADD COLUMN A_ROLE VARCHAR(20) DEFAULT 'Technician'");
                } catch (SQLException ex) { /* Column already exists */ }
            }

            // Create SALES table
            try {
                stmt.execute("CREATE TABLE SALES (" +
                        "S_ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "S_MED_NAME VARCHAR(100), " +
                        "S_DATE DATE, " +
                        "S_QTY INT, " +
                        "S_TOTAL DOUBLE)");
            } catch (SQLException e) { /* Table already exists */ }

            // Create PURCHASE_ORDERS table
            try {
                stmt.execute("CREATE TABLE PURCHASE_ORDERS (" +
                        "PO_ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "PO_MED_NAME VARCHAR(100), " +
                        "PO_SUPPLIER VARCHAR(100), " +
                        "PO_QTY INT, " +
                        "PO_STATUS VARCHAR(20) DEFAULT 'Pending', " +
                        "PO_DATE DATE)");
            } catch (SQLException e) { /* Table already exists */ }

            // Create COMPANY/SUPPLIER table with extra fields
            try {
                stmt.execute("CREATE TABLE COMPANY (" +
                        "C_ID INT PRIMARY KEY, " +
                        "C_NAME VARCHAR(50), " +
                        "C_ADDRESS VARCHAR(100), " +
                        "C_EXP INT, " +
                        "C_PHONE VARCHAR(20), " +
                        "C_EMAIL VARCHAR(50), " +
                        "C_LEADTIME INT DEFAULT 7, " +
                        "C_PREFERRED VARCHAR(10) DEFAULT 'No')");
            } catch (SQLException e) {
                 String[] newCols = {"C_EMAIL VARCHAR(50)", "C_LEADTIME INT DEFAULT 7", "C_PREFERRED VARCHAR(10) DEFAULT 'No'"};
                 for (String col : newCols) {
                    try {
                        stmt.execute("ALTER TABLE COMPANY ADD COLUMN " + col);
                    } catch (SQLException ex) { /* Column already exists */ }
                }
            }

            // Insert default admin if AGENTS table is empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM AGENTS")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    stmt.execute("INSERT INTO AGENTS (A_ID, A_NAME, A_AGE, A_PASSWORD, A_PHONE, A_GENDER, A_EMAIL, A_ROLE) " +
                            "VALUES (1, 'Admin', 30, 'admin123', '0000000000', 'Other', 'admin@pharma.com', 'Admin')");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
