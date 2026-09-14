# The Housing Society Dues Tracker


## Overview

The Housing Society Dues Tracker is a Java desktop application that helps a
housing society's treasurer or secretary manage flat maintenance dues. It
replaces manual registers/spreadsheets with a simple tool to register flats,
generate monthly maintenance bills, record payments against those bills, and
see at a glance who has paid and who still owes money.

The project includes two interfaces built on the same underlying logic:
- A **console version** (`Main.java`) - a menu-driven terminal application
- A **GUI version** (`MainGUI.java`) - a Swing desktop application with a
  tabbed interface

Both share the same business logic (`DuesManager`) and the same data files
(CSV, via `FileHandler`), so data created in one interface is visible in the
other.

## Features

- **Flat management** - add, edit, and delete flats (flat number, owner
  name, area in sqft, fixed monthly maintenance charge)
- **Bill generation** - generate maintenance bills for a given month for
  every registered flat in one action; already-billed flats for that month
  are skipped automatically
- **Payment recording** - record full or partial payments against a flat's
  bill, with date and payment mode (cash, UPI, cheque, etc.)
- **Dues report** - see all pending bills, plus running totals for amount
  collected and amount still pending
- **Flat search** - look up any flat to see its full bill and payment
  history in one place
- **CSV export** - export the current pending-dues report to a `.csv` file
  for printing or sharing
- **Data validation** - month and date fields are validated to a consistent
  `YYYY-MM` / `YYYY-MM-DD` format; flat number/month matching is
  case-insensitive and whitespace-tolerant to avoid false "not found" errors
- **Confirmation dialogs** - bill generation and payment recording ask for
  confirmation before making changes, to avoid accidental bulk actions
- **Sortable tables** - every table in the GUI can be sorted by clicking a
  column header
- **Persistent storage** - all data is saved to local CSV files and reloaded
  automatically the next time the app is run; changes are auto-saved after
  every action so no explicit "save" step is required

## Technologies / Tools Used

- **Language:** Java (JDK 17+ recommended; uses `List.toList()`, a JDK 16+
  feature)
- **GUI:** Java Swing (`javax.swing`) - no external UI framework
- **Storage:** Plain CSV files, read/written with `java.nio.file` and
  `java.io` - no external database
- **Build/run:** Standard `javac`/`java` command-line tools - no build tool
  (Maven/Gradle) required
- **Version control:** Git / GitHub

## Project Structure

```
Flat.java          - flat/unit model (flat no., owner, area, charge)
Bill.java           - monthly bill model (amount due, amount paid)
Payment.java        - payment transaction model
DuesManager.java    - core in-memory business logic and reporting
FileHandler.java    - CSV read/write for persistence
Main.java           - console (terminal) user interface
MainGUI.java        - Swing GUI user interface
statement.md        - problem statement, scope, target users
README.md           - this file
data/               - created automatically; stores flats.csv, bills.csv,
                      payments.csv (not committed to version control)
```

## Steps to Install & Run

### Prerequisites
- Java JDK 17 or later installed (`java -version` and `javac -version` to
  check). JDK 16+ is required for the `.toList()` syntax used in
  `FileHandler.java`; ask if you need it adapted for an older JDK.

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/paridhi091/smart-society.git
   cd smart-society
   ```
2. Compile all source files:
   ```bash
   javac *.java
   ```
3. Run the application - either interface works independently:
   ```bash
   java Main       # console (terminal) version
   ```
   ```bash
   java MainGUI    # Swing GUI version
   ```
4. On first run, a `data/` folder is created automatically in the current
   directory to store `flats.csv`, `bills.csv`, and `payments.csv`. Run the
   app from the same folder each time so it finds the same data.

## Instructions for Testing

No external testing framework is used; the application is tested manually
through its console/GUI menus. Suggested test flow:

1. **Add a flat** - go to the Flats tab (GUI) or option 1 (console), and add
   a flat with a flat number, owner name, area, and monthly charge.
   Verify it appears in the flats table/list.
2. **Generate bills** - go to Generate Bills, enter a month in `YYYY-MM`
   format (e.g. `2025-09`), and generate. Verify a bill appears for the
   flat you added, with the correct amount due and a `PENDING` status.
3. **Record a payment** - go to Record Payment, enter the same flat number
   and month, a payment amount, and a date in `YYYY-MM-DD` format. Verify
   the bill's paid/pending amounts update correctly, and that a partial
   payment leaves the bill `PENDING` while a full payment marks it `PAID`.
4. **Check the dues report** - verify the pending-dues table and the
   Total Collected / Total Pending figures match what you'd expect from
   the bills and payments entered so far.
5. **Search a flat** - search by the flat number and confirm its full bill
   and payment history is listed correctly.
6. **Edit/delete a flat** (GUI only) - select a flat row, edit its owner or
   charge, and verify the change is reflected; then delete a flat and
   confirm it disappears from the Flats tab while its historical bills and
   payments remain intact.
7. **Persistence check** - close the application (GUI: the window's X
   button, or File → Save & Exit; console: option 6) and reopen it. Verify
   all previously entered flats, bills, and payments are still present.
8. **Validation check** - try entering a month or date without a year
   (e.g. `09` instead of `2025-09`) and confirm the app rejects it with a
   clear format warning instead of silently accepting bad data.

## Screenshots

