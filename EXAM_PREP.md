# PharmTrack Exam Prep

This file summarizes key concepts, common exam questions, and how they apply to the Pharmacy Tracking System project.

---

## Java OOP Concepts

### 1. What is inheritance?
- Inheritance is an OOP mechanism where one class (subclass/child) derives properties and behavior from another class (superclass/parent).
- It promotes code reuse and allows shared functionality to live in one place.

**Example in this project:**
- The UI frames such as `LoginFrame`, `DashboardFrame`, and `MedicineFrame` all extend `javax.swing.JFrame`.
- They inherit methods like `setVisible()`, `setDefaultCloseOperation()`, `setSize()`, and layout behavior from `JFrame`.

### 2. What is abstraction?
- Abstraction hides implementation details and exposes only the necessary interface.
- It lets a class provide a simple public API while keeping complex details private.

**Example in this project:**
- `DatabaseHelper` abstracts JDBC details behind methods like `getConnection()`, `resultSetToTableModel()`, and `initializeDatabase()`.
- Frames do not need to know the SQL code for schema creation; they only call helper methods.

### 3. What is polymorphism?
- Polymorphism means "many forms." In Java, this typically means that a superclass reference can hold a subclass object.
- It allows code to work with objects through a common interface.

**Example in this project:**
- `JTable` uses `DefaultTableCellRenderer` and `TableModel`. Different renderers can be attached, but the table treats them through the common base type.
- Mouse listeners and action listeners are attached using interface types like `MouseAdapter` and `ActionListener`.

### 4. What is encapsulation?
- Encapsulation bundles data and methods together and restricts direct access to internal state.
- Fields are usually `private` and accessed through public methods.

**Example in this project:**
- `DatabaseHelper` keeps JDBC constants and connection details private.
- Frames keep UI components like `private JTextField m_name` and expose behavior through methods such as `loadMedicines()`.

---

## Design / Architecture Questions

### Where is inheritance used?
- `MedicineFrame`, `LoginFrame`, `SellingFrame`, and other frames inherit from `javax.swing.JFrame`.
- This is a standard Swing pattern: every window is a specialized frame.
- The shared behavior from `JFrame` includes window creation, event dispatch handling, and content pane management.

### Where is abstraction used?
- `DatabaseHelper` hides SQL details and connection setup.
- `ForecastingHelper` hides moving average calculation details and exposes only forecasting methods.
- `UIHelper` hides common UI styling details such as fonts, colors, and standard component creation.

### Where is composition used?
- Frames compose other panels and components: sidebar panel, header panel, content panel, table panel, and form panel.
- E.g., `MedicineFrame` composes `JPanel`, `JTable`, `JButton`, `JDateChooser`, and `JComboBox` into a single user interface.

---

## Swing and UI Questions

### What is Swing?
- Swing is Java’s built-in GUI toolkit.
- It provides components like `JFrame`, `JPanel`, `JButton`, `JTable`, and `JTextField`.
- In this project, Swing is used for all desktop UI screens.

### Why FlatLaf is used?
- FlatLaf gives Swing a modern look similar to IntelliJ.
- It improves styling and makes the app look consistent across platforms.
- It is initialized in the app entry point with `FlatIntelliJLaf.setup()`.

### What layouts are used?
- `BorderLayout` for main window division (`root.add(sidebar, BorderLayout.WEST)`).
- `GridBagLayout` for forms and data panels with flexible cell sizing.
- `FlowLayout` for button groups and small horizontal toolbars.
- `BoxLayout` for vertical sidebar menus.

### What is the Event Dispatch Thread (EDT)?
- EDT is the Swing thread responsible for all UI updates.
- Long tasks like database queries should not run on the EDT.
- In this project, database calls and login checks use background threads or are structured to avoid blocking the UI.

---

## Database Questions

### What is MySQL 8.3 used for?
- MySQL stores inventory, agents, sales, purchase orders, and supplier data.
- It provides a reliable relational backend with transactions and indexing.
- The project uses MySQL Connector/J 8.3 to connect Java with MySQL.

### What is JDBC?
- JDBC is Java’s standard API for database connectivity.
- It allows Java code to execute SQL queries and updates.
- This project uses JDBC directly rather than an ORM.

### Why use JDBC instead of Hibernate/JPA?
- JDBC is simpler for a small desktop project.
- It keeps SQL explicit and easier to debug.
- There is no need for mapping classes to tables or dealing with ORM configuration.

### What is a PreparedStatement?
- A `PreparedStatement` is a JDBC object that safely inserts values into SQL.
- It prevents SQL injection by separating SQL syntax from data.

Example:
```java
PreparedStatement ps = conn.prepareStatement(
    "SELECT * FROM AGENTS WHERE A_NAME = ? AND A_PASSWORD = ?");
ps.setString(1, username);
ps.setString(2, password);
```

### How are transactions used?
- Transactions ensure multiple updates succeed or fail together.
- `PurchaseOrderFrame` uses `conn.setAutoCommit(false)` and `conn.commit()`.
- If any update fails, it calls `conn.rollback()` to restore the previous state.

---

## Maven Questions

### What does Maven do?
- Maven manages the build lifecycle: compile, test, package.
- It resolves library dependencies from Maven Central.
- It stores those dependencies in the local Maven repository.

### What is `pom.xml`?
- `pom.xml` is the Maven configuration file.
- It declares project coordinates, dependencies, build plugins, and compiler settings.
- In this project, it includes FlatLaf, MySQL Connector/J, and JCalendar.

---

## JCalendar Questions

### What is JCalendar 1.4?
- JCalendar is a Swing date picker component library.
- It provides `JDateChooser`, a ready-made calendar input field.
- This project uses it for expiry date, manufacturing date, sales date, and PO date selection.

### Why use JCalendar?
- Swing does not include a built-in calendar picker.
- JCalendar is lightweight and integrates directly with Swing layouts.
- It returns `java.util.Date`, which can be converted to `java.sql.Date` for JDBC.

Example conversion:
```java
java.util.Date raw = dateChooser.getDate();
java.sql.Date sqlDate = new java.sql.Date(raw.getTime());
```

---

## Common Exam Questions and Answers

### Q: What is coupling and cohesion?
- **Coupling** is how much one class depends on another.
- **Cohesion** is how strongly related the responsibilities of a class are.
- In this project, `DatabaseHelper` has high cohesion because it only manages DB work. Frames have low coupling to DB logic because they call helper methods instead of embedding SQL.

### Q: What is a design pattern used here?
- The project uses a basic **Factory** pattern in `UIHelper` to create styled buttons, labels, and panels.
- It also uses **MVC-like separation**: frames handle view and input, while `DatabaseHelper` and `ForecastingHelper` handle model/business logic.

### Q: What is the difference between `extends` and `implements`?
- `extends` is used for class inheritance.
- `implements` is used when a class provides behavior for an interface.
- Example: `public class MedicineFrame extends JFrame` and `new MouseAdapter() { ... }` uses an adapter that implements the `MouseListener` interface.

### Q: How is exception handling used?
- `try/catch` blocks surround JDBC operations.
- Resources are closed using try-with-resources:
  ```java
  try (Connection conn = DatabaseHelper.getConnection();
       PreparedStatement ps = conn.prepareStatement(sql)) {
      ...
  }
  ```
- This ensures database resources are released even when exceptions occur.

### Q: What is the difference between `==` and `.equals()`?
- `==` compares object references.
- `.equals()` compares object values.
- In Swing, compare strings with `.equals()` when checking values like `role.equals("Admin")`.

---

## Quick Concept Reference

- **Inheritance:** `MedicineFrame extends JFrame`
- **Abstraction:** `DatabaseHelper.getConnection()` hides SQL details
- **Polymorphism:** `TableCellRenderer` and `MouseAdapter` use interface-based behavior
- **Encapsulation:** `private` fields in frames and helper classes
- **JDBC:** `Connection`, `PreparedStatement`, `ResultSet`
- **Transaction:** `setAutoCommit(false)`, `commit()`, `rollback()`
- **Maven:** dependency management via `pom.xml`
- **Swing layout:** `BorderLayout`, `GridBagLayout`, `BoxLayout`, `FlowLayout`
- **Look and Feel:** FlatLaf for modern UI style

---

## Practical exam tips for this project

- Be ready to explain why the app uses **Swing + FlatLaf** instead of JavaFX or web UI.
- Be ready to explain why the project uses **JDBC instead of Hibernate/JPA**.
- Use `DatabaseHelper` as the example of **abstraction**.
- Use `extends JFrame` as the example of **inheritance**.
- Use `PreparedStatement` as the example of **security against SQL injection**.
- Use `PurchaseOrderFrame` transaction logic as the example of **atomic DB updates**.
- Use `JDateChooser` conversion as the example of **third-party Swing component integration**.
