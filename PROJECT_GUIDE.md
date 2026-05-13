# Pharmacy Tracking System: Technical Architecture & Guide 💊

This document provides an in-depth technical overview of the Pharmacy Tracking System, explaining its modernized architecture, core features, and data-driven insights.

---

## 1. Architecture Overview
The system is built on a modern **Maven** and **MySQL** architecture, ensuring a clean, portable, and professional codebase with standardized dependency management and production-grade data storage.

---

## 2. Technical Stack
- **Language**: Java 17 LTS
- **UI Framework**: Java Swing with **FlatLaf (Light theme)**
- **Database**: **MySQL 8.x** (High-performance relational storage)
- **Build System**: **Maven** (Standardized dependency and lifecycle management)
- **Architecture**: Three-Tiered Separation (UI, Business Logic, Data Access)

### Core Modules:
- **`DatabaseHelper.java`**: Centralized JDBC management using MySQL. Handles schema initialization and data conversion.
- **`ForecastingHelper.java`**: The analytical engine. Implements demand prediction and inventory valuation.
- **`PharmacyInventoryManagement.java`**: Startup entry point that initializes the Look-and-Feel and database connection.

---

## 3. Core Features & "A to Z" Workflow

### A. Initialization & Visuals
When the app starts, it:
1. Initializes **FlatLightLaf** for a premium, modern aesthetic.
2. Calls `DatabaseHelper.initializeDatabase()` to verify the MySQL schema.
3. Shows a **Splash Screen** with a progress bar during initialization.

### B. Role-Based Access Control (RBAC)
The system enforces strict security boundaries at the UI level:
- **Admin**: Complete system access.
- **Pharmacist**: Focused on daily operations (Inventory, Sales, Suppliers, POs).
- **Technician**: Restricted to basic dispensing and stock viewing.

### C. Inventory Intelligence (`MedicineFrame.java`)
Beyond basic CRUD, the inventory module provides:
- **Visual Alerting**: Color-coded rows based on stock levels and expiration dates.
- **Batch Tracking**: Unique batch identifiers for medication traceability.
- **Reorder Logic**: Configurable thresholds to prevent shortages.

### D. Demand Forecasting Engine
The system uses a **Simple Moving Average (SMA)** algorithm:
1. Aggregates the last **30 days** of sales for every medicine.
2. Calculates daily consumption rates.
3. Projects demand for the **Next 7 Days**.
4. If `Predicted Demand > Current Stock`, a "Low Stock" alert is triggered on the dashboard before the item actually runs out.

### E. Procurement & Transactions (`PurchaseOrderFrame.java`)
Stock replenishment is handled via formal Purchase Orders. The "Receive" operation is **atomic**:
- Updates the PO status to `Received`.
- Increments the medicine quantity in the `MEDICINE` table.
- Both updates are wrapped in a single database transaction to guarantee data integrity.

---

## 4. Database Schema
Managed by `DatabaseHelper.java`, the system utilizes five primary tables:
1. **MEDICINE**: Core inventory data.
2. **AGENTS**: Secure staff credentials and roles.
3. **SALES**: Append-only log of all billing transactions.
4. **COMPANY**: Supplier directory with lead-time tracking.
5. **PURCHASE_ORDERS**: Procurement tracking and status management.

---

## 5. How to Run 🚀

### Prerequisites
- **Java JDK 17**
- **MySQL Server**

### Setup
1. Configure your MySQL credentials in `DatabaseHelper.java`.
2. Run `mvn clean compile` to prepare the project.
3. Launch the application via your IDE or `mvn exec:java`.

### First-Time Access
A default admin account is provided for initial setup:
- **Username**: `Admin`
- **Password**: `admin123`

---
*Developed by Abdoullah Ndao | Modern Pharmacy Inventory Management*
