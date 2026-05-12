 # Pharmacy Tracking System: A to Z Documentation 💊

This document provides a comprehensive overview of the Pharmacy Tracking System, explaining its architecture, features, and how to get it running.

---

## 1. Project Overview
The **Pharmacy Tracking System** is a robust Java-based desktop application designed for modern pharmacies to manage their inventory, track sales, and optimize operations through intelligent data-driven insights. It solves the critical problems of medication stockouts, expiration management, and manual billing.

---

## 2. Technical Architecture
- **Language**: Java 17
- **UI Framework**: Java Swing (Desktop GUI)
- **Database**: Apache Derby (Supports both Network and Embedded modes)
- **Design Pattern**: Centralized Helper Architecture (Logic separated into Helper classes)

### Core Components:
- **`DatabaseHelper.java`**: Handles connection pooling, table initialization, and SQL operations.
- **`ForecastingHelper.java`**: The "brain" of the app. It contains the logic for demand prediction and inventory analytics.
- **Frames**: Each window (Medicine, Agents, Company, etc.) is a separate `JFrame` managing its own state.

---

## 3. Core Features & "A to Z" Workflow

### A. Initialization & Splash
When the app starts (`PharmacyInventoryManagement.java`), it:
1. Calls `DatabaseHelper.initializeDatabase()` to create tables if they don't exist.
2. Shows a **Splash Screen** with a progress bar.

### B. Security & Roles
The **Login System** (`LoginFrame.java`) uses role-based access control (RBAC):
- **Admin**: Full access to everything.
- **Pharmacist**: Access to medicines and sales, but limited agent management.
- **Technician**: Access to billing and inventory, but restricted from sensitive settings (e.g., Supplier/Agent management).

### C. Inventory Management (`MedicineFrame.java`)
This is the heart of the system. You can:
- Track Medicine ID, Name, Quantity, and Price.
- Monitor **Expiration Dates** and **Batch Numbers**.
- Categorize by Owner (Main/Private) and Dosage forms.
- Set **Reorder Thresholds** (default is 10 units).

### D. Intelligent Dashboard (`DashboardFrame.java`)
Unlike simple spreadsheets, this dashboard provides:
- **Total Inventory Value**: Real-time dollar value of all stock.
- **Expiration Alerts**: Automatically flags items expiring within 30 days.
- **Low Stock Alerts**: Identifies items that are below threshold or predicted to run out soon.

### E. Demand Forecasting Algorithm
Located in `ForecastingHelper.java`, the system uses a **Simple Moving Average (SMA)**:
1. It analyzes the last **30 days** of sales for a specific medicine.
2. It calculates the `Average Daily Sales`.
3. It predicts the **Next 7 Days** of demand.
4. If `Current Stock < Predicted Demand`, it triggers an alert.

### F. Sales & Smart Billing (`SellingFrame.java`)
- Allows searching for medicine and adding to a bill.
- Automatically calculates totals and taxes.
- **Crucial**: Every sale is logged in the `SALES` table, which feeds the forecasting engine.

---

## 4. Database Schema
The system uses 5 main tables:
1. **MEDICINE**: Stores all medication details.
2. **AGENTS**: Stores staff accounts and passwords.
3. **SALES**: Historical record of every transaction.
4. **COMPANY**: Supplier database (Lead times, contact info).
5. **PURCHASE_ORDERS**: Tracks restocking orders sent to suppliers.

---

## 5. How to Run the Application 🚀

### Prerequisites
- **Java JDK 17** or higher installed.
- **Apache Derby** (Optional, the app will create an embedded DB if not found).

### Method 1: Using an IDE (Recommended)
1. **NetBeans**:
   - Open NetBeans and click `File` > `Open Project`.
   - Select the `PharmacyTrackingSystem` folder.
   - Right-click the project and select **Run**.
2. **IntelliJ IDEA**:
   - Open IntelliJ and click `File` > `Open`.
   - Select the project folder.
   - It will detect it as an Ant project.
   - Right-click `PharmacyInventoryManagement.java` and select **Run**.

### Method 2: Command Line (Terminal)
Since the project uses external JARs (Derby, JCalendar), you need to include them in the classpath.

1. **Compile**:
   ```bash
   javac -d bin -cp "lib/*" src/pharmacyinventorymanagement/*.java
   ```
2. **Run**:
   ```bash
   java -cp "bin:lib/*" pharmacyinventorymanagement.PharmacyInventoryManagement
   ```

---

## 6. First Login (Important!)
Since the database is empty on first run, you need a default account. 
- **Default Admin ID**: `1`
- **Default Password**: `admin123`

---
*Created by Abdoullah Ndao | Pharmacy Inventory Management System*
