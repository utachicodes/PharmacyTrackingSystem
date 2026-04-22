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
                        "M_OWNER VARCHAR(50) DEFAULT 'Main')");
            } catch (SQLException e) {
                // Table might already exist, try adding M_OWNER if missing
                try {
                    stmt.execute("ALTER TABLE MEDICINE ADD COLUMN M_OWNER VARCHAR(50) DEFAULT 'Main'");
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

            // Other tables (AGENTS, COMPANY) are assumed to exist or will be handled in their frames
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
