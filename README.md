# The Housing Society Management System

## Overview

The Housing Society Management System is a Java desktop application designed to help housing societies manage flats, maintenance bills, payments, users, and tenant complaints.

The system replaces manual registers and spreadsheets with a centralized application where society owners/administrators can manage financial records and tenants can view their own information and raise complaints.

The project provides two interfaces built on the same underlying business logic:

- **Console version (`Main.java`)** - menu-driven terminal application
- **GUI version (`LoginGUI.java`)** - Swing-based application with role-based access

Both interfaces use the same `DuesManager` business logic and CSV-based persistent storage through `FileHandler`.

---

## Features

### 1. Flat Management

- Add new flats
- Store flat number, owner name, area, and monthly maintenance charge
- Edit flat details through the GUI
- Delete flats through the GUI
- Search flats by flat number
- View associated bills and payment history

### 2. Monthly Bill Management

- Generate monthly maintenance bills for all registered flats
- Prevent duplicate bills for the same flat and month
- Store amount due and amount paid
- Automatically calculate pending amounts
- Track `PENDING` and `PAID` bills

### 3. Payment Management

- Record payments against generated bills
- Support full and partial payments
- Store payment date and payment mode
- Automatically update bill payment status
- View payment history for individual flats

### 4. Dues & Reports

- View all generated bills
- View pending dues
- Calculate total amount collected
- Calculate total amount pending
- Search individual flats and view their financial history

### 5. User Management

- Owner/administrator account
- Create tenant accounts
- Associate tenants with flats
- Store username, password, name, role, and flat number
- Role-based access to different dashboards

### 6. Authentication

- Username and password authentication
- Separate access for:
  - **Owner**
  - **Tenant**
- Invalid login attempts are rejected
- Users are redirected to the appropriate dashboard based on their role

### 7. Tenant Dashboard

Tenants can access:

- Personal dashboard
- Their bills
- Their payment history
- Their complaints

Tenants only interact with information associated with their registered flat.

### 8. Complaint Management

Tenants can raise complaints related to their flat.

The owner/administrator can:

- View all complaints
- View open complaints
- Respond to complaints
- Mark complaints as completed
- Track complaint status

Complaint information includes:

- Complaint ID
- Flat number
- Tenant name
- Subject
- Description
- Status
- Response
- Creation date

### 9. Persistent Storage

All major application data is stored in CSV files:

- `flats.csv`
- `bills.csv`
- `payments.csv`
- `users.csv`
- `complaints.csv`

Data is loaded when the application starts and saved when changes are made or the application exits.

### 10. Data Validation & Error Handling

- Empty input validation
- Numeric input validation
- Flat existence validation
- Duplicate username prevention
- Duplicate monthly bill prevention
- Invalid login handling
- Invalid complaint ID handling
- Invalid payment handling

---

## Technologies / Tools Used

- **Language:** Java
- **JDK:** Java 17+
- **GUI:** Java Swing (`javax.swing`)
- **Storage:** CSV files
- **File Handling:** `java.io`, `java.nio.file`
- **Collections:** Java Collections Framework
- **Version Control:** Git / GitHub
- **Build:** Standard `javac` / `java` commands
- **Database:** No external database required

---

## Project Structure

```text
smart-society/
│
├── Flat.java
├── Bill.java
├── Payment.java
├── User.java
├── Complaint.java
│
├── DuesManager.java
├── FileHandler.java
│
├── Main.java
├── MainGUI.java
├── LoginGUI.java
├── TenantDashboard.java
│
├── statement.md
├── README.md
│
└── data/
    ├── flats.csv
    ├── bills.csv
    ├── payments.csv
    ├── users.csv
    └── complaints.csv

Main classes 
| Class                  | Responsibility                            |
| ---------------------- | ----------------------------------------- |
| `Flat.java`            | Represents a housing society flat         |
| `Bill.java`            | Represents a monthly maintenance bill     |
| `Payment.java`         | Represents a payment transaction          |
| `User.java`            | Represents owner and tenant accounts      |
| `Complaint.java`       | Represents tenant complaints              |
| `DuesManager.java`     | Core business logic and data management   |
| `FileHandler.java`     | CSV persistence                           |
| `Main.java`            | Command-line interface                    |
| `MainGUI.java`         | Owner/administrator GUI                   |
| `LoginGUI.java`        | Authentication and role-based entry point |
| `TenantDashboard.java` | Tenant-specific GUI                       |

Architecture

The application follows a simple layered structure:
                User
                 |
        +--------+--------+
        |                 |
   Main.java          LoginGUI.java
  Console UI             |
                    +----+----+
                    |         |
                 Owner      Tenant
                    |         |
                MainGUI   TenantDashboard
                    \         /
                     \       /
                    DuesManager
                         |
                    FileHandler
                         |
                    CSV Storage

The user interfaces communicate with DuesManager, which contains the core application logic.

FileHandler handles reading and writing persistent data to CSV files.
Steps to Install & Run
Prerequisites

Install Java JDK 17 or later.

Check the installation:

java -version
javac -version
Clone the Repository
git clone https://github.com/paridhi091/smart-society.git
cd smart-society
Compile the Project

Compile all Java files:
javac *.java
Run the Console Version

The console version can be executed directly from the terminal:

java Main   
Run the GUI Version

For the graphical application, start:

java LoginGUI

The login screen provides role-based access.

Default Owner Account
Username: owner
Password: owner123

After logging in as the owner, tenant accounts can be created through User Management.                 
