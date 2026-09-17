# Housing Society Management System

## Overview

The **Housing Society Management System** is a Java-based application designed to simplify the management of residential society operations.

The system allows society owners/administrators to manage flats, generate monthly maintenance bills, record payments, track pending dues, manage tenant accounts, and handle tenant complaints.

The project provides **two interfaces**:

* **Console-based interface** for command-line execution and automated evaluation.
* **Graphical User Interface (GUI)** using Java Swing for easier interaction by owners and tenants.

The application uses CSV files for persistent data storage, allowing information to remain available between program executions.

---

## Objectives

The main objectives of this project are:

* To digitize common housing society management activities.
* To maintain flat and owner information.
* To generate and track monthly maintenance bills.
* To record and monitor payments.
* To calculate pending dues and total collections.
* To provide separate functionality for society owners and tenants.
* To allow tenants to raise and track complaints.
* To provide a simple and user-friendly interface.
* To store application data persistently using CSV files.
* To demonstrate object-oriented programming and modular Java development.

---

## Features

### 1. Flat Management

The system allows the society owner to:

* Add new flats.
* Store flat number and owner information.
* Store flat area.
* Store monthly maintenance charges.
* Search for a flat by its flat number.
* View information associated with a flat.
* Prevent duplicate flat numbers.

---

### 2. Monthly Bill Management

The application provides maintenance bill management functionality.

Features include:

* Generate monthly bills for all registered flats.
* Store the billing month.
* Store the maintenance amount.
* Prevent duplicate bills for the same flat and month.
* View all generated bills.
* View pending bills.

---

### 3. Payment Management

The system allows payments to be recorded against generated bills.

Payment information includes:

* Flat number
* Billing month
* Amount paid
* Payment date
* Payment mode

Supported payment modes can include:

* Cash
* Cheque
* UPI
* Other payment methods

The system updates the corresponding bill when a payment is recorded.

---

### 4. Dues and Collection Reports

The application provides basic financial reporting.

The owner can view:

* All bills.
* Pending bills.
* Total amount collected.
* Total pending amount.

This provides a quick overview of the society's maintenance collection status.

---

### 5. User Management

The system supports multiple user roles.

Currently supported roles are:

* **OWNER**
* **TENANT**

The owner can:

* View registered users.
* Create tenant accounts.
* Assign a tenant account to an existing flat.
* Prevent duplicate usernames.

Tenant accounts contain:

* Username
* Password
* Tenant name
* Flat number
* Role

---

### 6. Authentication

The GUI provides a login system for accessing the application.

Users log in using:

* Username
* Password

After successful authentication, the system identifies the user's role.

The application provides:

**Owner → Owner Dashboard**

**Tenant → Tenant Dashboard**

Invalid credentials are rejected with an appropriate error message.

### Default Owner Account

A default owner account is automatically created if no owner account exists.

```text
Username: owner
Password: owner123
Role: OWNER
```

### Tenant Accounts

There is **no fixed default tenant account**.

Tenant accounts are created by the owner through the User Management section.

For example, a tenant account can be created with:

```text
Username: tenant1
Password: tenant123
Tenant Name: Paridhi
Flat No: 01
Role: TENANT
```

This is an example test account and is not automatically created by the application.

---

## 7. Owner Dashboard

After logging in as an owner, the owner can access the main management dashboard.

The dashboard contains sections for:

* Flats
* Generate Bills
* Record Payment
* Dues Report
* Charts
* Search Flat
* Complaints
* Users

The owner can manage society data from a single interface.

---

## 8. Tenant Dashboard

Tenants have a separate dashboard after logging in.

The tenant dashboard provides access to information related to their assigned flat.

Sections include:

* My Dashboard
* My Bills
* My Payments
* My Complaints

Tenants can view their bills and payment history and can raise complaints.

---

## 9. Complaint Management

The complaint management module allows tenants to communicate issues to the society owner.

Tenants can submit:

* Subject
* Description
* Flat number
* Tenant information
* Complaint date

Each complaint receives a unique complaint ID.

### Complaint Lifecycle

A complaint follows a simple workflow:

```text
Tenant raises complaint
        ↓
Complaint created
        ↓
Status = OPEN
        ↓
Owner views complaint
        ↓
Owner responds
        ↓
Owner marks complaint as DONE
```

The owner can:

* View all complaints.
* View open complaints.
* View complaint details.
* Respond to complaints.
* Mark complaints as completed.

Tenants can view complaints associated with their flat.

---

## 10. Persistent Data Storage

The application uses CSV files for persistent storage.

The following data files are used:

```text
data/
├── flats.csv
├── bills.csv
├── payments.csv
├── users.csv
└── complaints.csv
```

Data is loaded when the application starts and saved during application operations/exit depending on the interface.

This allows data to persist between different executions of the program.

---

## Technologies and Tools Used

### Programming Language

* **Java**

### GUI

* **Java Swing**
* **AWT**

### Data Storage

* **CSV files**
* Java File I/O
* `java.io`
* `java.nio.file`

### Data Structures

The project uses Java collections such as:

* `ArrayList`
* `LinkedHashMap`
* `List`
* `Map`
* `Collection`

### Development Tools

* Visual Studio Code
* Java Development Kit (JDK)
* Git
* GitHub

---

## Project Structure

```text
Housing-Society-Management/
│
├── Main.java
├── LoginGUI.java
├── MainGUI.java
├── TenantDashboard.java
│
├── DuesManager.java
├── FileHandler.java
│
├── Flat.java
├── Bill.java
├── Payment.java
├── User.java
└── Complaint.java
│
├── data/
│   ├── flats.csv
│   ├── bills.csv
│   ├── payments.csv
│   ├── users.csv
│   └── complaints.csv
│
└── README.md
```

---

## System Architecture

The application follows a simple layered structure.

```text
                    USER
                     |
          +----------+----------+
          |                     |
     Console UI              GUI
     Main.java             LoginGUI.java
                                |
                    +-----------+-----------+
                    |                       |
                 OWNER                    TENANT
                    |                       |
                MainGUI              TenantDashboard
                    \                       /
                     \                     /
                      +-------------------+
                              |
                        DuesManager
                              |
                        FileHandler
                              |
                       CSV Data Files
```


# Installation and Setup

## Requirements

Before running the project, make sure Java is installed.

Recommended:

```text
JDK 17 or later
```

Check your Java version using:

```bash
java -version
```

Check the Java compiler using:

```bash
javac -version
```

Git is recommended for obtaining the project from GitHub.

---

## Clone the Repository

Clone the repository using:

```bash
git clone https://github.com/paridhi091/smart-society.git
```

Move into the project directory:

```bash
cd smart-society
```

---

# Running the Project

---

## Option 1: Run the Console Application

Compile all Java files:

```bash
javac *.java
```

Run the console application:

```bash
java Main
```

The console menu will appear:

```text
=========================================
      HOUSING SOCIETY MANAGEMENT
=========================================
1. Add Flat
2. Generate Monthly Bills
3. Record Payment
4. View Dues Report
5. Search Flat by Number
6. User Management
7. Complaint Management
8. Save & Exit
=========================================
Enter choice:
```

---

## Option 2: Run the Graphical Application

Compile the project:

```bash
javac *.java
```

Run the GUI:

```bash
java LoginGUI
```

The login screen will appear.

Use the default owner credentials:

```text
Username: owner
Password: owner123
```

After successful login, the Owner Dashboard will open.

---

# How to Use the Application

## Owner Workflow

A typical owner workflow is:

```text
Login
  ↓
Owner Dashboard
  ↓
Add Flats
  ↓
Generate Monthly Bills
  ↓
Record Payments
  ↓
View Dues Report
  ↓
Manage Tenant Accounts
  ↓
Manage Complaints
```

---

## Creating a Tenant Account

1. Log in as the owner.
2. Open the **Users** section.
3. Enter a username.
4. Enter a password.
5. Enter the tenant's name.
6. Enter an existing flat number.
7. Click **Create Tenant**.
8. The tenant account is stored in `users.csv`.

The tenant can then use those credentials to log in.

---

## Tenant Workflow

A typical tenant workflow is:

```text
Tenant Login
     ↓
Tenant Dashboard
     ↓
View Bills
     ↓
View Payments
     ↓
Raise Complaint
     ↓
Track Complaint
```





# Screenshots

C:\Users\parid\Project\screenshots\Screenshot 2026-09-17 213705.png
---


