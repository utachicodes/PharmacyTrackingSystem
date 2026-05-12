# Pharmacy Tracking System

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java&logoColor=white)
![Apache Derby](https://img.shields.io/badge/Database-Apache%20Derby-d9534f?style=flat-square)
![Swing](https://img.shields.io/badge/UI-Java%20Swing-4caf50?style=flat-square)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)
![Build](https://img.shields.io/badge/Build-Apache%20Ant-a6192e?style=flat-square&logo=apache&logoColor=white)
![Status](https://img.shields.io/badge/Status-Active-brightgreen?style=flat-square)

A desktop pharmacy inventory management system built with Java 17, Java Swing, and Apache Derby. Designed for small pharmacies that need real-time stock tracking, demand forecasting, and role-based access without requiring a server or internet connection.

---

## Features

**Inventory Management**
Full CRUD on medicine records across 14 fields including batch number, therapeutic category, dosage form, unit cost, and configurable reorder threshold. Rows are automatically colour-coded: red for low stock, yellow for medicines expiring within 30 days.

**Demand Forecasting**
A Simple Moving Average algorithm analyses the past 30 days of sales per medicine and projects seven-day demand. Low-stock alerts fire when current quantity falls below the forecast or below the minimum threshold of 10 units.

**Billing and Sales**
Point-of-sale screen enforces stock constraints and blocks overselling. Every transaction is logged to the SALES table, which feeds the forecasting engine. Inventory value is recalculated on each dashboard load.

**Purchase Orders**
Create purchase orders with medicine, supplier, and quantity. Receiving a PO uses an explicit JDBC transaction: both the status update and the stock increment commit together, or both roll back. Partial database state cannot persist.

**Role-Based Access Control**
Three roles enforced at the UI level:
- Admin: full access to all modules
- Pharmacist: access to medicines, billing, suppliers, and purchase orders
- Technician: access to medicines and billing only

**Supplier Management**
Supplier directory with lead-time tracking and a preferred-supplier flag. The medicine entry form populates its company dropdown live from this table.

---

## Technical Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 17 LTS |
| UI Framework | Java Swing (Nimbus look and feel) |
| Database | Apache Derby 10.16 (embedded + network modes) |
| Date Picker | JCalendar (Toedter) |
| Build | Apache Ant |
| Version Control | Git |

The application bootstraps its own database schema on first launch. `DatabaseHelper.getConnection()` tries the Derby network server on port 1527 first, then falls back to embedded mode automatically. No manual database setup is required.

---

## Getting Started

**Prerequisites**
- Java JDK 17 or higher
- NetBeans (recommended) or IntelliJ IDEA

**Run with NetBeans**
1. Clone the repository
2. Open NetBeans and select File > Open Project
3. Select the `PharmacyTrackingSystem` folder
4. Right-click the project and select Run

**Run from the command line**
```bash
# Compile
javac -d bin -cp "lib/*" src/pharmacyinventorymanagement/*.java

# Run
java -cp "bin:lib/*" pharmacyinventorymanagement.PharmacyInventoryManagement
```

**Default credentials**
On first launch, a default admin account is created automatically:

| Field | Value |
|-------|-------|
| Name | Admin |
| Password | admin123 |

---

## Project Structure

```
src/pharmacyinventorymanagement/
    PharmacyInventoryManagement.java   Entry point
    SplashFrame.java                   Loading screen
    LoginFrame.java                    Authentication
    DashboardFrame.java                Navigation hub and alerts
    MedicineFrame.java                 Inventory CRUD
    SellingFrame.java                  Point-of-sale billing
    AgentsFrame.java                   Staff management
    CompanyFrame.java                  Supplier management
    PurchaseOrderFrame.java            Procurement workflow
    DatabaseHelper.java                JDBC layer and schema bootstrap
    ForecastingHelper.java             SMA forecasting and alert logic
```

---

## Database Schema

Five tables managed entirely by `DatabaseHelper.initializeDatabase()`:

| Table | Purpose |
|-------|---------|
| MEDICINE | Medicine records with 14 fields including batch and threshold |
| AGENTS | Staff accounts with role assignment |
| SALES | Append-only transaction log, auto-incremented ID |
| PURCHASE_ORDERS | Procurement records with Pending/Received status |
| COMPANY | Supplier directory with lead time and preference flag |

---

## Author

**Abdoullah Ndao**
Junior II, Dakar American University of Science and Technology
