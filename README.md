# Pharmacy Tracking System

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/Database-MySQL%208.0-4479A1?style=flat-square&logo=mysql&logoColor=white)
![FlatLaf](https://img.shields.io/badge/UI-FlatLaf%20Light-10B981?style=flat-square)
![Build](https://img.shields.io/badge/Build-Maven-C71A36?style=flat-square&logo=apache-maven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)
![Status](https://img.shields.io/badge/Status-Active-brightgreen?style=flat-square)

A desktop pharmacy inventory management system built with **Java 17**, **MySQL**, and **FlatLaf**. Designed for pharmacies that require real-time stock tracking, intelligent demand forecasting, and professional role-based access control.

---

## Features

**Inventory Management**
Full CRUD on medicine records across 14 fields including batch tracking, therapeutic category, and per-medicine reorder thresholds. High-contrast row highlighting flags low stock (Red) and near-expiry items (Yellow).

**Demand Forecasting**
Integrated 30-day Simple Moving Average (SMA) algorithm that predicts next-week demand per medicine, allowing for proactive restocking before stockouts occur.

**Sales & Smart Billing**
Professional billing engine that enforces stock constraints and records every transaction to the `SALES` table, automatically updating inventory and feeding the forecasting dashboard.

**Procurement Workflow**
Full purchase order (PO) management with transactional stock receiving. Uses atomic database transactions to ensure inventory counts and PO statuses are always perfectly synchronized.

**Role-Based Access Control (RBAC)**
Three distinct permission levels:
- **Admin:** Full system oversight including user management.
- **Pharmacist:** Inventory, Sales, Suppliers, and Procurement.
- **Technician:** Inventory and Sales only.

---

## Technical Stack

| Component | Technology |
|-----------|------------|
| Language | Java 17 LTS |
| UI Framework | Java Swing + FlatLaf Light |
| Database | MySQL 8.x |
| Build System | Maven |
| Date Picker | JCalendar |
| Persistence | JDBC (MySQL Connector/J, Apache Commons DBUtils) |

---

## Database

The application connects to a local MySQL instance and uses a database called **`PharmaDb`**. The database and all required tables are **created automatically** on first launch — no manual SQL setup is needed.

### Schema (5 Tables)

| Table | Purpose |
|-------|---------|
| `MEDICINE` | Core inventory — name, quantity, price, expiry, batch, category, reorder threshold |
| `AGENTS` | Staff accounts — credentials, roles (Admin / Pharmacist / Technician) |
| `SALES` | Append-only transaction log for all billing activity |
| `COMPANY` | Supplier directory with lead-time and preferred-supplier tracking |
| `PURCHASE_ORDERS` | Procurement lifecycle — order creation, status, and receiving |

### Connection

The default connection points to `localhost:3306` with user `root` and an empty password. To change this, edit the constants at the top of `DatabaseHelper.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/PharmaDb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true";
private static final String USER = "root";
private static final String PASS = ""; // Update with your MySQL password
```

---

## Getting Started

### Prerequisites
- **Java JDK 17** or higher
- **MySQL Server** running locally on port `3306`
- **Maven** (or an IDE with Maven support like IntelliJ IDEA)

### Run the Application

**Via Maven (Command Line):**
```bash
# Compile and build
mvn clean compile

# Run the application
mvn exec:java -Dexec.mainClass="pharmacyinventorymanagement.PharmacyInventoryManagement"
```

**Via IntelliJ IDEA:**
1. Open the project folder (IntelliJ will detect the `pom.xml`).
2. Wait for dependencies to download.
3. Run `PharmacyInventoryManagement.java`.

### Default Login
A default admin account is seeded automatically on first launch:
- **Username:** `Admin`
- **Password:** `admin123`

---

## Project Structure

```
src/pharmacyinventorymanagement/
    PharmacyInventoryManagement.java   – Application Entry Point
    SplashFrame.java                   – Loading & DB Initialization
    LoginFrame.java                    – Role-Based Authentication
    DashboardFrame.java                – Navigation & Live Alerts
    MedicineFrame.java                 – Inventory Management
    SellingFrame.java                  – POS & Sales Log
    AgentsFrame.java                   – User Management (Admin Only)
    CompanyFrame.java                  – Supplier Management
    PurchaseOrderFrame.java            – Procurement Workflow
    DatabaseHelper.java                – MySQL Connection & Schema Init
    ForecastingHelper.java             – SMA Algorithm & Analytics
```

---

## Author

**Abdoullah Ndao**
Junior II, Dakar American University of Science and Technology
[GitHub Portfolio](https://github.com/utachicodes)

---
*This project is part of the Software Engineering curriculum at DAUST.*
