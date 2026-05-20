# PharmTrack

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=flat-square&logo=mysql&logoColor=white)
![FlatLaf](https://img.shields.io/badge/UI-FlatLaf%20IntelliJ-10B981?style=flat-square)
![Build](https://img.shields.io/badge/Build-Maven-C71A36?style=flat-square&logo=apache-maven&logoColor=white)

A desktop pharmacy inventory management system built with Java 17, MySQL, and FlatLaf.

---

## Run

```bash
./start.sh
```

The script finds Maven automatically, checks MySQL is running, compiles, and launches the app.

**Default login**
- Username: `Admin`
- Password: `admin123`

> To change the MySQL credentials, edit `USER` and `PASS` in `DatabaseHelper.java`.

---

## Features

- **Inventory** — add, update, delete medicines with batch tracking, categories, and reorder thresholds. Rows highlight red (low stock) or yellow (near expiry).
- **Forecasting** — 30-day Simple Moving Average predicts next-week demand per medicine and triggers low-stock alerts on the dashboard.
- **Billing** — POS screen deducts stock and logs every sale to the `SALES` table for forecasting.
- **Purchase Orders** — create POs and receive stock atomically via a two-step JDBC transaction.
- **Role-Based Access** — Admin, Pharmacist, and Technician roles with distinct permissions enforced across all screens.
- **Suppliers** — manage supplier directory with lead times and preferred-supplier flags.

---

## Tech Stack

| | |
|---|---|
| Language | Java 17 LTS |
| UI | Java Swing + FlatLaf IntelliJ |
| Database | MySQL 8.x |
| Build | Maven |
| Date Picker | JCalendar 1.4 |
| Persistence | JDBC (MySQL Connector/J 8.3) |

---

## Project Structure

```
src/main/java/pharmacyinventorymanagement/
├── PharmacyInventoryManagement.java  — entry point, FlatLaf setup
├── SplashFrame.java                  — loading screen
├── LoginFrame.java                   — authentication
├── DashboardFrame.java               — alerts, RBAC, navigation
├── MedicineFrame.java                — inventory management
├── SellingFrame.java                 — billing & sales
├── AgentsFrame.java                  — staff management
├── CompanyFrame.java                 — supplier management
├── PurchaseOrderFrame.java           — procurement workflow
├── DatabaseHelper.java               — MySQL connection & schema
└── ForecastingHelper.java            — SMA forecasting & alerts
```

---

## Database

All 5 tables are created automatically on first launch — no manual SQL needed.

| Table | Purpose |
|---|---|
| `MEDICINE` | Stock records — quantity, price, expiry, batch, thresholds |
| `AGENTS` | Staff accounts and roles |
| `SALES` | Transaction log used by the forecasting engine |
| `COMPANY` | Supplier directory |
| `PURCHASE_ORDERS` | PO lifecycle — Pending → Received |

---

**Abdoullah Ndao** · Junior II · DAUST · [github.com/utachicodes](https://github.com/utachicodes)
