package pharmacyinventorymanagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class PurchaseOrderFrame extends javax.swing.JFrame {

    public PurchaseOrderFrame() {
        initComponents();
        loadPOs();
    }

    private void loadPOs() {
        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM PURCHASE_ORDERS")) {
            poTable.setModel(DatabaseHelper.resultSetToTableModel(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPO() {
        String medName = txtMedName.getText();
        String supplier = txtSupplier.getText();
        String qtyStr = txtQty.getText();

        if (medName.isEmpty() || supplier.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO PURCHASE_ORDERS (PO_MED_NAME, PO_SUPPLIER, PO_QTY, PO_DATE) VALUES (?, ?, ?, ?)")) {
            
            pstmt.setString(1, medName);
            pstmt.setString(2, supplier);
            pstmt.setInt(3, Integer.parseInt(qtyStr));
            pstmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            pstmt.executeUpdate();
            
            loadPOs();
            JOptionPane.showMessageDialog(this, "Purchase Order Created");
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void receiveStock() {
        int selectedRow = poTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a PO to receive");
            return;
        }

        int poId = (int) poTable.getValueAt(selectedRow, 0);
        String medName = (String) poTable.getValueAt(selectedRow, 1);
        int qty = (int) poTable.getValueAt(selectedRow, 3);
        String status = (String) poTable.getValueAt(selectedRow, 4);

        if ("Received".equals(status)) {
            JOptionPane.showMessageDialog(this, "This order is already received");
            return;
        }

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update PO status
                PreparedStatement updatePO = conn.prepareStatement(
                    "UPDATE PURCHASE_ORDERS SET PO_STATUS = 'Received' WHERE PO_ID = ?");
                updatePO.setInt(1, poId);
                updatePO.executeUpdate();

                // Update Medicine stock
                PreparedStatement updateMed = conn.prepareStatement(
                    "UPDATE MEDICINE SET M_QUANTITY = M_QUANTITY + ? WHERE M_NAME = ?");
                updateMed.setInt(1, qty);
                updateMed.setString(2, medName);
                int updatedRows = updateMed.executeUpdate();

                if (updatedRows == 0) {
                    // Medicine not found, maybe handle creating it?
                    JOptionPane.showMessageDialog(this, "Warning: Medicine not found in inventory. Stock not updated.");
                }

                conn.commit();
                loadPOs();
                JOptionPane.showMessageDialog(this, "Stock received and inventory updated!");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error processing receipt: " + e.getMessage());
        }
    }

    private void initComponents() {
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtMedName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtSupplier = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtQty = new javax.swing.JTextField();
        btnCreatePO = new javax.swing.JButton();
        btnReceive = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        poTable = new javax.swing.JTable();
        btnBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Purchase Orders & Receiving");

        jPanel1.setBackground(new java.awt.Color(51, 153, 0));
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); 
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setText("PURCHASE ORDERS");

        jPanel2.setBackground(new java.awt.Color(255, 255, 204));
        jLabel2.setText("Medicine Name:");
        jLabel3.setText("Supplier:");
        jLabel4.setText("Quantity:");

        btnCreatePO.setText("Create PO");
        btnCreatePO.addActionListener(e -> createPO());

        btnReceive.setText("Receive Selected Stock");
        btnReceive.addActionListener(e -> receiveStock());

        btnBack.setText("Back to Dashboard");
        btnBack.addActionListener(e -> {
            new DashboardFrame().setVisible(true);
            this.dispose();
        });

        // Layout code...
        setLayout(new BorderLayout());
        add(jPanel1, BorderLayout.NORTH);
        jPanel1.add(jLabel1);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(jPanel2, BorderLayout.NORTH);
        
        jPanel2.setLayout(new FlowLayout());
        jPanel2.add(jLabel2); jPanel2.add(txtMedName);
        jPanel2.add(jLabel3); jPanel2.add(txtSupplier);
        jPanel2.add(jLabel4); jPanel2.add(txtQty);
        jPanel2.add(btnCreatePO);
        
        centerPanel.add(jScrollPane1, BorderLayout.CENTER);
        poTable.setFillsViewportHeight(true);
        jScrollPane1.setViewportView(poTable);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnReceive);
        bottomPanel.add(btnBack);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCreatePO;
    private javax.swing.JButton btnReceive;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable poTable;
    private javax.swing.JTextField txtMedName;
    private javax.swing.JTextField txtQty;
    private javax.swing.JTextField txtSupplier;
}
