# PharmTrack — Pharmacy Inventory Management System

![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=flat-square&logo=mysql&logoColor=white)
![FlatLaf](https://img.shields.io/badge/UI-FlatLaf%20IntelliJ-10B981?style=flat-square)
![Build](https://img.shields.io/badge/Build-Maven-C71A36?style=flat-square&logo=apache-maven&logoColor=white)

A desktop pharmacy management system built with Java 17, MySQL, and Java Swing. It handles medicine inventory, billing, purchase orders, supplier management, staff accounts, and demand forecasting — all from a single application with role-based access control.

---

## Table of Contents

1. [Quick Start](#1-quick-start)
2. [What the App Does (Feature Map)](#2-what-the-app-does-feature-map)
3. [Technology Choices and Why](#3-technology-choices-and-why)
4. [Project Structure](#4-project-structure)
5. [Every Screen Explained](#5-every-screen-explained)
6. [Role-Based Access Control (RBAC)](#6-role-based-access-control-rbac)
7. [Database Design](#7-database-design)
8. [Forecasting Engine](#8-forecasting-engine)
9. [How the UI is Built](#9-how-the-ui-is-built)
10. [How Data Flows Through the App](#10-how-data-flows-through-the-app)
11. [How to Extend the App](#11-how-to-extend-the-app)

---

## 1. Quick Start

### Prerequisites

- Java 17+
- MySQL 8.x running locally (default port 3306, root user, empty password)
- Maven (or use the bundled Maven via `start.sh`)

### Run in one command

```bash
./start.sh
```

The script automatically finds Maven, checks that MySQL is running (and starts it if not), compiles the project, and launches the app.

### Default login

| Username | Password | Role |
|----------|----------|------|
| `Admin` | `admin123` | Admin |

On first launch, the database and all 5 tables are created automatically. No manual SQL setup is needed.

### Change the MySQL credentials

Open `src/main/java/pharmacyinventorymanagement/DatabaseHelper.java` and edit:

```java
private static final String USER = "root";
private static final String PASS = "";
```

---

## 2. What the App Does (Feature Map)

| Module | What it does |
|--------|-------------|
| **Login** | Authenticates staff by username + password against the database and routes them to the dashboard with their assigned role |
| **Dashboard** | Live inventory alerts, low-stock warnings, expiry warnings, forecasting summary, and navigation hub |
| **Medicines** | Full CRUD for medicine stock — add batches, set reorder thresholds, view low-stock and near-expiry highlights |
| **Billing** | POS screen — scan/search medicines, add to bill, print invoice, deduct stock, log sale |
| **Purchase Orders** | Create POs for low-stock medicines, receive them atomically (stock is updated in the same DB transaction) |
| **Suppliers** | Manage the supplier directory — contact info, lead times, preferred flag |
| **Agents** | Admin-only staff account management — create, update, delete accounts and assign roles |
| **Sales History** | Browse and filter all past transactions by date range or medicine name |
| **Reports** | Top-selling medicines, daily revenue (14-day window), expiry overview, inventory stats |

---

## 3. Technology Choices and Why

### Java 17

Java was chosen because it compiles to a single `.class`/JAR that runs anywhere a JVM is installed, without a separate runtime environment like Node or Python needing to be set up on the machine. Java 17 is the current LTS (Long-Term Support) release, meaning it receives security updates until 2029. Swing, the UI framework used here, is part of the JDK itself — no extra download needed.

### Java Swing (not JavaFX, not a web framework)

Swing is the classic Java desktop UI toolkit. It is:

- **Built into the JDK** — no dependency to download, always available with any Java install
- **Mature** — every layout manager, border, component, and renderer has been stable since Java 2 (1998) and is extensively documented
- **Fully programmatic** — every pixel is controlled in code, making it easy to read and modify without a visual designer
- **Thread-safe with the Event Dispatch Thread (EDT)** — Swing enforces a single-thread model: all UI updates must happen on the EDT. Long operations (like DB queries) are run in background threads and results sent back via `SwingUtilities.invokeLater()`. This is exactly the pattern used in `LoginFrame` for the sign-in query.

JavaFX was not chosen because it requires a separate download on Java 11+. Web frameworks were not chosen because this is a local desktop tool — no network, no browser dependency, and no server to deploy.

### FlatLaf (Look and Feel)

Out of the box, Java Swing uses the OS's native look-and-feel (Metal on Linux/Windows, Aqua on macOS). Neither looks modern. FlatLaf replaces the entire paint system with a clean, flat design (similar to IntelliJ IDEA). It is activated in one line at startup:

```java
FlatIntelliJLaf.setup();
```

All colors, borders, fonts, and sizes then build on top of FlatLaf's base theme using the custom `UIHelper` constants.

### MySQL 8.x

MySQL is the most widely deployed open-source relational database. It was chosen over alternatives for these reasons:

| Alternative | Why MySQL was preferred |
|-------------|------------------------|
| **SQLite** | File-based, no server needed — but no concurrent access, limited data types, no stored procedures |
| **H2 (in-memory)** | Great for testing, but loses all data when the app exits unless configured to persist — adds setup complexity |
| **PostgreSQL** | Excellent, but heavier to install and configure on Windows/macOS for students |
| **MySQL** | Free, installable via Homebrew/XAMPP/installer, widely taught, `createDatabaseIfNotExist=true` in the JDBC URL means zero manual setup |

The JDBC connection URL is:

```
jdbc:mysql://localhost:3306/PharmaDb?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
```

- `createDatabaseIfNotExist=true` — creates the `PharmaDb` database automatically if it does not exist
- `useSSL=false` — disables SSL for local dev (no certificate needed)
- `allowPublicKeyRetrieval=true` — required for MySQL 8's new authentication plugin (`caching_sha2_password`) when not using SSL

### JDBC (not Hibernate/JPA)

JDBC (Java Database Connectivity) is the low-level API for SQL queries from Java. It was chosen instead of an ORM (like Hibernate) because:

- **No magic** — every query is a SQL string you write yourself, so you see exactly what hits the database
- **No annotation learning curve** — ORMs require understanding `@Entity`, `@OneToMany`, lazy loading, etc.
- **Appropriate for the scale** — this app has 5 tables and straightforward queries; an ORM would add overhead with no benefit
- **PreparedStatements** are used throughout, which prevent SQL injection by parameterizing inputs:

```java
PreparedStatement ps = conn.prepareStatement(
    "SELECT A_ROLE FROM AGENTS WHERE A_NAME = ? AND A_PASSWORD = ?");
ps.setString(1, user);
ps.setString(2, pwd);
```

The `?` placeholders are filled in by JDBC with proper escaping, so a user entering `' OR '1'='1` as a password cannot break the query.

### Maven

Maven manages dependencies and the build lifecycle. All external libraries (FlatLaf, MySQL Connector, JCalendar) are declared in `pom.xml` and downloaded automatically on first build. This means the project has no JAR files checked into the repository — only source code.

---

## 4. Project Structure

```
PharmacyTrackingSystem/
├── pom.xml                          Maven config and dependency list
├── start.sh                         One-command build + launch script
├── README.md                        This file
└── src/main/java/pharmacyinventorymanagement/
    │
    ├── PharmacyInventoryManagement.java   Entry point — FlatLaf setup, DB init, splash
    ├── SplashFrame.java                   Loading screen shown on startup
    │
    ├── DatabaseHelper.java                MySQL connection, schema creation, seed data
    ├── ForecastingHelper.java             SMA demand forecast, stock alerts, expiry alerts
    │
    ├── PharmIcons.java                    Vector icon library (Java2D, no image files)
    ├── UIHelper.java                      Shared colors, fonts, sizes, component factories
    │
    ├── LoginFrame.java                    Authentication screen
    ├── DashboardFrame.java                Main hub — alerts, forecasts, navigation
    ├── MedicineFrame.java                 Medicine inventory CRUD
    ├── SellingFrame.java                  Billing / point-of-sale
    ├── PurchaseOrderFrame.java            Purchase order creation and receiving
    ├── CompanyFrame.java                  Supplier management
    ├── AgentsFrame.java                   Staff account management (Admin only)
    ├── SalesHistoryFrame.java             Transaction history with filters
    └── ReportsFrame.java                  Analytics — top sellers, revenue, expiry
```

### Naming convention

All screen files are named `<Module>Frame.java` and extend `javax.swing.JFrame`. Each frame is a self-contained window: it builds its own sidebar, header, and content area; navigating to another module creates that module's `JFrame`, makes it visible, and disposes the current one.

---

## 5. Every Screen Explained

### 5.1 Splash Screen (`SplashFrame.java`)

Shown for ~2 seconds while the database initializes. It is a simple branded panel with the app name and a progress bar. `DatabaseHelper.initializeDatabase()` runs in the background during this time — by the time the splash closes, all 5 tables exist and the default admin account is seeded.

### 5.2 Login Screen (`LoginFrame.java`)

The login screen is an undecorated (no OS window chrome) frameless window with a green accent border. It has two panels side by side:

**Left panel** — branding and role reference card:
- App logo and name
- Three role cards showing what each role can access (Admin, Pharmacist, Technician) with PharmIcons vector icons

**Right panel** — the login form:
- Username field
- Password field with Show/Hide toggle
- Sign In button (runs the query on a background thread so the UI never freezes)
- DB connection status badge (green "Connected" / red "Database offline") checked on startup

**Authentication logic:**

```java
SELECT A_ROLE FROM AGENTS WHERE A_NAME = ? AND A_PASSWORD = ?
```

If a row is returned, the role string (e.g., `"Admin"`) is read and passed to `new DashboardFrame(role)`. The login frame is then disposed. If no row is returned, both fields shake with a brief animation and an error message appears inline (not a popup).

**Security note:** Passwords are stored as plain text in this version (appropriate for a student project). In a production system, you would hash passwords with BCrypt before storage and compare hashes on login.

### 5.3 Dashboard (`DashboardFrame.java`)

The dashboard is the home screen for all roles. It serves two purposes: **navigation hub** and **live alerts**.

**Sidebar:** Lists all modules the current user's role can access. Modules that are restricted for the current role are visually dimmed and unclickable — `setEnabled(false)` is called and the foreground is set to a grey color. PharmIcons reads the foreground color at paint time, so the icons dim automatically without any extra code.

**Alerts panel:** Calls `ForecastingHelper.getLowStockAlerts()` and `ForecastingHelper.getExpirationAlerts()` on load. Each alert is displayed in a list with a colored left border:
- **Red border** — LOW STOCK: stock is below the reorder threshold or below the 7-day SMA forecast
- **Amber border** — EXPIRY: medicine expires within 30 days
- **Grey border** — summary/separator rows

**Double-click shortcut:** Double-clicking a LOW STOCK alert opens `PurchaseOrderFrame` pre-filled with that medicine name. This is a workflow shortcut so pharmacists don't have to navigate to POs and manually type the name.

**Inventory summary bar:** Always shows total medicine count and total inventory value (`SUM(M_QUANTITY * M_UNIT_COST)`) at the top of the alert list.

**Role badge:** The current user's role is shown in the header as a green pill badge.

### 5.4 Medicine Inventory (`MedicineFrame.java`)

The medicines screen is a split layout: a form card at the top for data entry, and a table below showing the full inventory.

**Form fields:**

| Field | Column in DB | Notes |
|-------|-------------|-------|
| ID | `M_ID` | Must be unique integer — primary key |
| Name | `M_NAME` | Free text |
| Quantity | `M_QUANTITY` | Current stock level |
| Price (sell) | `M_PRICE` | Retail price shown to patient |
| Unit Cost | `M_UNIT_COST` | Wholesale cost — used for inventory value |
| Expiry Date | `M_EXPDATE` | Date picker (JCalendar) |
| Manufacture Date | `M_MFTDATE` | Date picker |
| Supplier | `M_COMPANY` | Dropdown populated from COMPANY table |
| Category | `M_CATEGORY` | Dropdown — Tablet, Syrup, Injection, etc. |
| Strength | `M_STRENGTH` | e.g., "500mg" |
| Dosage | `M_DOSAGE` | e.g., "2 tablets twice daily" |
| Threshold | `M_THRESHOLD` | Reorder point — default 10 units |
| Batch | `M_BATCH` | Batch/lot number for traceability |
| Owner | `M_OWNER` | Branch/owner label — default "Main" |

**Table row highlighting:**
- Red background → quantity ≤ threshold (low stock)
- Yellow background → expiry date within 30 days
- This is implemented via a custom `DefaultTableCellRenderer` that reads those two columns on every row paint

**Actions:**
- **ADD** — inserts a new row; catches `SQLIntegrityConstraintViolationException` if the ID already exists
- **UPDATE** — updates all fields for the selected ID
- **DELETE** — removes the row after confirmation
- **CLEAR** — resets the form

**Clicking a table row** fills the form with that medicine's data so it can be edited.

**Live search** filters the table client-side using `RowFilter.regexFilter()` — no DB round-trip needed.

### 5.5 Billing / POS (`SellingFrame.java`)

The billing screen is a split layout: medicine search and item addition on the left, running invoice on the right.

**Workflow:**

1. Type a medicine name in the search field — the table below shows matching medicines with current stock and price
2. Select a medicine from the table — its name and price fill the form
3. Enter the quantity and optionally a prescription reference
4. Click **Add to Bill** — the item is added to the invoice textarea and the running total updates
5. Click **Print / Confirm** — the sale is finalized:
   - Stock is deducted from `MEDICINE` (`UPDATE MEDICINE SET M_QUANTITY = M_QUANTITY - ? WHERE M_ID = ?`)
   - The sale is logged in `SALES` (`INSERT INTO SALES ...`)
   - The invoice textarea resets for the next customer

**Why the SALES table matters:** Every row in `SALES` is an input to the forecasting engine. If a medicine is never billed, it has no sales history and the SMA returns 0 — only the threshold-based alert fires. The more billing data, the more accurate the forecast.

**Technician access:** Technicians can access billing fully. It is one of the two modules available to them.

### 5.6 Purchase Orders (`PurchaseOrderFrame.java`)

Purchase orders handle the procurement loop: when stock is low, you create a PO for a supplier, and when goods arrive, you receive the PO.

**Two-panel layout:**
- Top: form to create a new PO (medicine name from dropdown, supplier from dropdown, quantity, date)
- Bottom: table of all POs with status (Pending / Received)

**Creating a PO:**

```sql
INSERT INTO PURCHASE_ORDERS (PO_MED_NAME, PO_SUPPLIER, PO_QTY, PO_STATUS, PO_DATE)
VALUES (?, ?, ?, 'Pending', ?)
```

**Receiving a PO (atomic stock update):**

When you select a Pending PO and click Receive, two things happen inside a single JDBC transaction:

```sql
-- 1. Update PO status
UPDATE PURCHASE_ORDERS SET PO_STATUS = 'Received' WHERE PO_ID = ?

-- 2. Add received quantity to medicine stock
UPDATE MEDICINE SET M_QUANTITY = M_QUANTITY + ? WHERE M_NAME = ?
```

Both updates use `conn.setAutoCommit(false)` / `conn.commit()` / `conn.rollback()`. If either query fails, the whole operation is rolled back — you cannot receive a PO and have the stock update fail silently.

**Pre-fill from dashboard:** When the dashboard's double-click shortcut is used, `PurchaseOrderFrame` is constructed with `new PurchaseOrderFrame(userRole, medName)`. The two-argument constructor pre-populates the medicine name field.

### 5.7 Supplier Management (`CompanyFrame.java`)

A straightforward CRUD screen for the supplier directory.

**Fields per supplier:**

| Field | Purpose |
|-------|---------|
| ID | Primary key |
| Name | Company name |
| Address | Physical address |
| Phone | Contact number |
| Email | Contact email |
| Experience (years) | How long they've been a supplier |
| Lead Time (days) | How many days between order and delivery |
| Preferred | Yes/No — marks this as the default supplier |

The `C_LEADTIME` field feeds into future planning (e.g., if lead time is 7 days and you are out of stock, you need to order now). The `C_PREFERRED` flag is a simple Yes/No that could be used to auto-select a supplier on new POs.

The supplier name dropdown in `MedicineFrame` and `PurchaseOrderFrame` is populated by querying this table:

```sql
SELECT C_NAME FROM COMPANY ORDER BY C_NAME
```

### 5.8 Agent / Staff Management (`AgentsFrame.java`)

This screen is only accessible to Admins. It manages the staff accounts that log into the system.

**Fields per agent:**

| Field | DB Column | Notes |
|-------|----------|-------|
| ID | `A_ID` | Unique integer — primary key |
| Name | `A_NAME` | Used as the username at login |
| Age | `A_AGE` | Informational |
| Password | `A_PASSWORD` | Plain text (see security note in §5.2) |
| Phone | `A_PHONE` | Contact |
| Gender | `A_GENDER` | Male / Female / Other |
| Email | `A_EMAIL` | Contact |
| Role | `A_ROLE` | Admin / Pharmacist / Technician — controls access |

**Password security:** The password column is hidden in the table (column width set to 0) so passwords are never visible on screen. When you click a row to edit it, the password field is left blank — if you submit an update with the password field empty, the existing password is preserved. Only if you type a new password is it updated.

**Role colour coding in the table:** The Role column has a custom cell renderer:
- Admin → red background
- Pharmacist → blue background
- Technician → green background

This makes it easy to scan who has what level of access.

**How account creation works end-to-end:**
1. Admin fills in all fields including the Role dropdown
2. Clicks ADD → `INSERT INTO AGENTS VALUES(?,?,?,?,?,?,?,?)` runs with a PreparedStatement
3. The new account immediately appears in the table
4. The new staff member can now log in with the name and password just created
5. Their role determines what they see after login

### 5.9 Sales History (`SalesHistoryFrame.java`)

A read-only browsing screen for all past transactions. It supports filtering by:

- **Date range** — From / To date pickers (JCalendar). The SQL WHERE clause adds `AND S_DATE >= ? AND S_DATE <= ?` when dates are selected
- **Medicine name** — client-side regex filter on the table (same RowFilter approach as MedicineFrame)

The total revenue for the currently displayed rows is shown in the top-right as a running sum. This is recalculated every time the filter changes.

Columns: Sale ID, Medicine Name, Date, Quantity, Total, Prescription Reference.

### 5.10 Reports (`ReportsFrame.java`)

The reports screen has four stat cards at the top and three data tables below.

**Stat cards:**

| Card | Query |
|------|-------|
| Medicines | `SELECT COUNT(*) FROM MEDICINE` |
| Total Revenue | `SELECT SUM(S_TOTAL) FROM SALES` |
| Pending POs | `SELECT COUNT(*) FROM PURCHASE_ORDERS WHERE PO_STATUS = 'Pending'` |
| Expiring Soon | Count of medicines with expiry within 30 days |

**Three report tables:**

1. **Top Sellers** — `SELECT S_MED_NAME, SUM(S_QTY), SUM(S_TOTAL) FROM SALES GROUP BY S_MED_NAME ORDER BY SUM(S_QTY) DESC LIMIT 10`
2. **Revenue by Day (14 days)** — `SELECT S_DATE, SUM(S_TOTAL) FROM SALES WHERE S_DATE >= CURDATE() - INTERVAL 14 DAY GROUP BY S_DATE ORDER BY S_DATE DESC`
3. **Expiry Overview** — `SELECT M_NAME, M_EXPDATE, M_QUANTITY FROM MEDICINE WHERE M_EXPDATE <= CURDATE() + INTERVAL 30 DAY ORDER BY M_EXPDATE`

All three tables are loaded in parallel — each runs its query independently, so the screen loads as fast as the slowest query, not the sum of all three.

---

## 6. Role-Based Access Control (RBAC)

### The three roles

| Role | Access |
|------|--------|
| **Admin** | Everything — all 7 modules including Agents (staff management) |
| **Pharmacist** | All modules except Agents |
| **Technician** | Medicines and Billing only |

### How roles are stored

Each staff account has an `A_ROLE` column in the `AGENTS` table. The value is one of the three strings: `"Admin"`, `"Pharmacist"`, `"Technician"`.

### How roles are enforced — three layers

**Layer 1: Authentication query**

The login query reads the role:

```java
SELECT A_ROLE FROM AGENTS WHERE A_NAME = ? AND A_PASSWORD = ?
```

The role string is passed to `DashboardFrame(role)` and then forwarded to every subsequent frame as it is opened. No role check is done in the app's memory alone — the source of truth is always the database.

**Layer 2: Dashboard restrictions (`applyRolePermissions()`)**

Called immediately after the sidebar is built. It disables the nav labels that the role cannot use:

```java
private void applyRolePermissions() {
    if ("Technician".equalsIgnoreCase(userRole)) {
        btnAgents.setEnabled(false);    // dimmed — can't click
        btnCompany.setEnabled(false);
        btnPO.setEnabled(false);
        btnReports.setEnabled(false);
    } else if ("Pharmacist".equalsIgnoreCase(userRole)) {
        btnAgents.setEnabled(false);
    }
    // Admin: no restrictions
}
```

The mouse listener on each button checks `isEnabled()` before navigating:

```java
nav.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
        if (nav.isEnabled()) {
            // open the target frame
        }
    }
});
```

**Layer 3: Per-frame sidebar restrictions**

Every frame independently re-applies role restrictions in its own sidebar. This prevents a scenario where someone navigates directly to a restricted module by calling its constructor. The pattern in each frame is:

```java
boolean restricted =
    (isTech  && (key.equals("agents") || key.equals("po") || ...)) ||
    (isPharm && key.equals("agents"));
JLabel nav = navLabel(item[0], restricted);
```

If `restricted` is true, the label gets a dimmed foreground color and no mouse listener is attached.

### Access matrix

| Module | Admin | Pharmacist | Technician |
|--------|-------|-----------|-----------|
| Dashboard | Full | Full | Full |
| Medicines | Full | Full | Full |
| Billing | Full | Full | Full |
| Sales History | Full | Full | Full |
| Suppliers | Full | Full | Blocked |
| Purchase Orders | Full | Full | Blocked |
| Reports | Full | Full | Blocked |
| Agents | Full | Blocked | Blocked |

### Adding a new role

To add a fourth role (e.g., "Manager"):

1. Add `"Manager"` as a choice in the Role dropdown in `AgentsFrame`:
   ```java
   a_role = new JComboBox<>(new String[]{"Admin", "Pharmacist", "Technician", "Manager"});
   ```
2. Define what a Manager can access in `DashboardFrame.applyRolePermissions()`:
   ```java
   } else if ("Manager".equalsIgnoreCase(userRole)) {
       btnAgents.setEnabled(false);  // e.g., can't manage staff
   }
   ```
3. Mirror the restriction in each frame's sidebar `restricted` check.

---

## 7. Database Design

### Schema overview

All tables are created by `DatabaseHelper.initializeDatabase()` on first launch using `CREATE TABLE IF NOT EXISTS`, so re-running the app on an existing database is safe.

```
MEDICINE          AGENTS            SALES             PURCHASE_ORDERS    COMPANY
─────────────     ──────────        ──────────        ───────────────    ───────
M_ID (PK)         A_ID (PK)         S_ID (PK, AUTO)   PO_ID (PK, AUTO)   C_ID (PK)
M_NAME            A_NAME            S_MED_NAME        PO_MED_NAME        C_NAME
M_QUANTITY        A_AGE             S_DATE            PO_SUPPLIER        C_ADDRESS
M_PRICE           A_PASSWORD        S_QTY             PO_QTY             C_EXP
M_EXPDATE         A_PHONE           S_TOTAL           PO_STATUS          C_PHONE
M_MFTDATE         A_GENDER          S_PRESCRIPTION    PO_DATE            C_EMAIL
M_COMPANY         A_EMAIL                                                C_LEADTIME
M_OWNER           A_ROLE                                                 C_PREFERRED
M_CATEGORY
M_STRENGTH
M_DOSAGE
M_UNIT_COST
M_THRESHOLD
M_BATCH
```

### Key design decisions

**No foreign keys.** Medicine names are stored as plain strings in `SALES` and `PURCHASE_ORDERS` rather than as a foreign key to `MEDICINE.M_ID`. This is intentional for a first version: it means historical sales records are not broken if a medicine is deleted or renamed, and it simplifies queries for the forecasting engine. The tradeoff is that a typo in a medicine name creates orphaned records.

**AUTO_INCREMENT for SALES and PURCHASE_ORDERS.** These tables generate their own IDs because each transaction or order is a new event, not something the user assigns. MEDICINE, AGENTS, and COMPANY use user-assigned IDs, giving the admin control over ID numbering.

**M_THRESHOLD per medicine.** Each medicine has its own reorder point. A fast-moving analgesic might have a threshold of 50 while a niche medicine might have 5. This per-row threshold makes the alerts much more precise than a global constant.

**S_PRESCRIPTION in SALES.** Added to support prescription tracking. If blank, the sale was over-the-counter. The `ALTER TABLE ... ADD COLUMN IF NOT EXISTS` pattern is used on startup so existing databases without this column get it added automatically.

### Connection management

Every public method that hits the database opens a fresh connection, runs its query, and closes everything in a `try-with-resources` block:

```java
try (Connection conn = DatabaseHelper.getConnection();
     PreparedStatement ps = conn.prepareStatement("...")) {
    // use ps
} // conn and ps are closed automatically
```

This is a connection-per-operation model. For an app with a small number of concurrent users (a desktop app is single-user), this is fine. For high-concurrency apps you would use a connection pool (HikariCP, etc.).

---

## 8. Forecasting Engine

The forecasting engine lives in `ForecastingHelper.java` and uses **Simple Moving Average (SMA)** to predict demand.

### How SMA works here

1. Look at all sales of a medicine in the last 30 days
2. Sum the total quantity sold
3. Divide by 30 to get the average daily sales rate
4. Multiply by 7 to forecast the next 7 days of demand

```java
double avgDaily = (double) totalQty / (double) SALES_WINDOW_DAYS; // 30
int forecast   = (int) Math.ceil(avgDaily * FORECAST_HORIZON_DAYS); // 7
```

If a medicine sold 60 units in the last 30 days, that is 2 units/day on average. The 7-day forecast is 14 units. If current stock is 10 and the threshold is 12, a LOW STOCK alert fires.

### The dual-trigger alert condition

```java
if (currentQty < predicted || currentQty < threshold)
```

An alert fires if **either** condition is true:
- `currentQty < predicted` — based on actual sales velocity (SMA)
- `currentQty < threshold` — based on the admin-set reorder point

This means new medicines with no sales history still get alerted if stock drops below threshold.

### Expiry alerts

```java
LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
if (expiry.isBefore(thirtyDaysFromNow)) { alert; }
```

Any medicine expiring within the next 30 days (including already-expired medicines, since their expiry date is before "now + 30 days") is listed. This is intentionally inclusive — expired medicines are the most urgent case.

### Where forecasting is called

- `DashboardFrame.loadAlerts()` — on every dashboard load and on Refresh click
- `ForecastingHelper.getInventoryValue()` and `getMedicineCount()` — for the dashboard summary bar

---

## 9. How the UI is Built

### The frame pattern

Every screen follows this structure:

```java
public class XFrame extends JFrame {

    public XFrame(String role) {
        this.userRole = role;
        initComponents();  // builds all UI
        loadData();        // first DB fetch
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildContent(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildSidebar() { ... }
    private JPanel buildContent() { ... }
}
```

### The sidebar pattern

Every frame has an identical sidebar built from the same template:
1. Logo area (dark background, "⚕ PHARMA" label)
2. Separator line
3. Active page indicator (green highlight, shows current module with icon)
4. Separator line
5. Nav items for all OTHER modules (clickable, restricted by role)
6. Push remaining space down with `Box.createVerticalGlue()`
7. Separator line
8. Logout at the bottom

Navigation works by creating a new frame and disposing the current one:

```java
nav.addMouseListener(new MouseAdapter() {
    public void mouseClicked(MouseEvent e) {
        java.awt.Rectangle b = getBounds();  // remember window position/size
        MedicineFrame f = new MedicineFrame(userRole);
        f.setBounds(b);   // open at same position/size
        f.setVisible(true);
        dispose();        // close this frame
    }
});
```

This is a single-window pattern: only one `JFrame` exists at a time.

### UIHelper and PharmIcons

`UIHelper.java` is a static constants class. It defines all colors, font sizes, and standard component factories used across every frame. Instead of typing `new Color(16, 185, 129)` in twenty places, you write `UIHelper.ACCENT`. This means changing the accent color is a one-line edit.

`PharmIcons.java` draws scalable vector icons using Java2D — no image files. Every icon is a `paintIcon()` method that draws shapes using `Graphics2D`:

```java
private static Icon home(int s) {
    return make(s, (g, x, y, sz, c) -> {
        // draw a house using Path2D, Rectangle2D, etc.
        // 'c' is the component's foreground color at paint time
        g.setColor(c);
        ...
    });
}
```

Because the icon reads the label's foreground color at paint time (not when the icon is created), a single icon instance automatically adapts: green when the label is active, grey when it is disabled.

### GridBagLayout for forms

All data entry forms use `GridBagLayout`, the most powerful (and most verbose) Swing layout. It places components in a grid where each cell can span columns and have independent sizing:

```
[Label: ID    ] [TextField ID  ] [Label: Phone ] [TextField Phone]
[Label: Name  ] [TextField Name] [Label: Email ] [TextField Email]
[Label: Age   ] [TextField Age ] [Label: Gender] [ComboBox Gender]
```

The pattern in every form:
```java
GridBagConstraints g = new GridBagConstraints();
g.fill = GridBagConstraints.HORIZONTAL;
g.insets = new Insets(5, 6, 5, 6);  // padding around each cell

g.gridy = 0; g.gridx = 0; g.weightx = 0;  panel.add(label1, g);
             g.gridx = 1; g.weightx = 1;  panel.add(field1, g);
             g.gridx = 2; g.weightx = 0;  panel.add(label2, g);
             g.gridx = 3; g.weightx = 1;  panel.add(field2, g);
```

`weightx = 0` means the label takes only as much width as its text needs. `weightx = 1` means the field expands to fill available space.

### JTable and table models

All data tables use `DefaultTableModel` populated from a `ResultSet`. The `DatabaseHelper.resultSetToTableModel()` utility method converts any `ResultSet` into a table model by reading column metadata:

```java
ResultSetMetaData meta = rs.getMetaData();
for (int col = 1; col <= meta.getColumnCount(); col++)
    columnNames.add(meta.getColumnLabel(col));
while (rs.next()) {
    Vector<Object> row = new Vector<>();
    for (int col = 1; col <= meta.getColumnCount(); col++)
        row.add(rs.getObject(col));
    rows.add(row);
}
return new DefaultTableModel(rows, columnNames);
```

Selecting a row fills the form by reading values directly from the model:

```java
int i = table.getSelectedRow();
nameField.setText(model.getValueAt(i, 1).toString());
```

---

## 10. How Data Flows Through the App

### Login flow

```
User types credentials
    → background thread runs SELECT A_ROLE FROM AGENTS WHERE ...
    → if match found: new DashboardFrame(role) on EDT
    → LoginFrame disposed
    → DashboardFrame.loadAlerts() runs
    → DashboardFrame.applyRolePermissions() dims restricted nav items
```

### Billing flow

```
Pharmacist searches medicine
    → stockSearch listener → SELECT ... FROM MEDICINE WHERE M_NAME LIKE ?
    → table updates
Selects medicine, enters quantity, clicks Add to Bill
    → item appended to invoice textarea
    → running total updated in-memory
Clicks Print / Confirm
    → UPDATE MEDICINE SET M_QUANTITY = M_QUANTITY - qty WHERE M_ID = id
    → INSERT INTO SALES (S_MED_NAME, S_DATE, S_QTY, S_TOTAL, S_PRESCRIPTION)
    → invoice resets
```

### Low-stock reorder flow

```
Dashboard shows LOW STOCK alert for "Paracetamol"
User double-clicks the alert row
    → DashboardFrame.openPrefilledPO("Paracetamol")
    → new PurchaseOrderFrame(userRole, "Paracetamol") — medicine field pre-filled
User fills quantity, selects supplier, clicks Create PO
    → INSERT INTO PURCHASE_ORDERS (PO_MED_NAME, PO_SUPPLIER, PO_QTY, PO_STATUS='Pending', PO_DATE)
Goods arrive — user selects PO, clicks Receive
    → BEGIN TRANSACTION
    → UPDATE PURCHASE_ORDERS SET PO_STATUS = 'Received' WHERE PO_ID = ?
    → UPDATE MEDICINE SET M_QUANTITY = M_QUANTITY + qty WHERE M_NAME = medName
    → COMMIT
```

---

## 11. How to Extend the App

### Add a new module (e.g., Patient Records)

1. Create `PatientsFrame.java` following the same frame pattern (extend JFrame, take `String role`, build sidebar + content)
2. Add a new nav item in every existing frame's sidebar items array:
   ```java
   {"Patients", "patients"}
   ```
3. Add the icon key to `PharmIcons.byKey()` and draw the icon
4. Add the navigation case to each frame's switch block:
   ```java
   case "patients": next = new PatientsFrame(userRole); break;
   ```
5. Add role restrictions in the `restricted` boolean expression in each sidebar
6. Add the button to `DashboardFrame` fields and `applyRolePermissions()`
7. Create the `PATIENTS` table in `DatabaseHelper.initializeDatabase()`

### Add a new field to Medicine

1. Add the column to the `CREATE TABLE` statement in `DatabaseHelper.java`:
   ```java
   "M_NEWFIELD VARCHAR(50)"
   ```
2. For existing databases, add an `ALTER TABLE` migration (like the `S_PRESCRIPTION` example):
   ```java
   try { stmt.execute("ALTER TABLE MEDICINE ADD COLUMN M_NEWFIELD VARCHAR(50)"); }
   catch (SQLException ignored) {}
   ```
3. Add the input field to `MedicineFrame`'s form
4. Add the column to the INSERT and UPDATE PreparedStatements
5. Read the value from the table model in the row-click handler

### Add password hashing

1. Add BCrypt to `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.mindrot</groupId>
       <artifactId>jbcrypt</artifactId>
       <version>0.4</version>
   </dependency>
   ```
2. In `AgentsFrame.btnAddMouseClicked()`:
   ```java
   String hashed = BCrypt.hashpw(pwd, BCrypt.gensalt());
   add.setString(4, hashed);  // store hash, not plain text
   ```
3. In `LoginFrame.btnLoginMouseClicked()`:
   ```java
   // Query retrieves the stored hash for the given username
   SELECT A_PASSWORD, A_ROLE FROM AGENTS WHERE A_NAME = ?
   // Then verify:
   if (BCrypt.checkpw(enteredPassword, storedHash)) { ... }
   ```

---

## Tech Stack Summary

| Component | Technology | Version |
|-----------|-----------|---------|
| Language | Java | 17 LTS |
| UI Framework | Java Swing | Built into JDK |
| Look and Feel | FlatLaf IntelliJ | 3.4.1 |
| Database | MySQL | 8.x |
| DB Driver | MySQL Connector/J | 8.3.0 |
| DB API | JDBC | Built into JDK |
| Date Picker | JCalendar (toedter) | 1.4 |
| Build Tool | Apache Maven | 3.x |
| Icon System | Java2D (custom, PharmIcons) | — |

---

**Abdoullah Ndao** · Junior II · DAUST · [github.com/utachicodes](https://github.com/utachicodes)
