# Housing Society Management System

## Problem Statement

Housing societies need to manage monthly maintenance charges, payments, pending dues, tenant information, and resident complaints. In smaller societies, these activities may be managed manually using paper records or spreadsheets.

Manual management can result in:

* Difficulty tracking which flats have paid their maintenance bills.
* Errors while calculating pending dues.
* Difficulty maintaining payment history.
* Difficulty managing tenant accounts.
* Lack of a dedicated system for handling tenant complaints.
* Difficulty accessing organized records of society-related information.

The **Housing Society Management System** provides a centralized Java-based application to manage flats, monthly bills, payments, dues, users, and tenant complaints. The system provides both a command-line interface and a graphical interface to make these operations easier to manage.

## Scope of the Project

The project covers the basic management requirements of a residential housing society.

The system includes:

* Managing flat records.
* Generating monthly maintenance bills.
* Recording full and partial payments.
* Tracking pending dues and total collections.
* Searching flat and payment history.
* Creating and managing tenant accounts.
* User authentication.
* Separate owner and tenant dashboards.
* Allowing tenants to raise complaints.
* Allowing owners to respond to and resolve complaints.
* Storing application data using local CSV files.
* Providing both console-based and Java Swing-based interfaces.

The project is designed as a local desktop application and does not currently include a centralized database, online payment system, cloud deployment, or mobile application.

## Target Users

### Society Owners / Administrators

Owners or administrators can use the system to:

* Manage flats.
* Generate monthly bills.
* Record payments.
* View pending dues.
* Manage tenant accounts.
* View and manage complaints.

### Tenants

Tenants can use the system to:

* Log in using their account.
* View bills related to their flat.
* View payment history.
* Raise complaints.
* Track complaint status and responses.

### Small Residential Housing Societies

The system is intended for small residential societies that require a simple local application for managing maintenance dues and basic resident-related activities.

## High-Level Features

1. **Flat Management** - Add, manage, and search flat records containing flat number, owner name, area, and monthly maintenance charge.

2. **Bill Generation** - Generate monthly maintenance bills for registered flats and prevent duplicate bills for the same flat and month.

3. **Payment Recording** - Record full or partial payments along with payment date and payment mode.

4. **Dues Reporting** - View pending bills and calculate total collected and pending amounts.

5. **Flat Search and History** - Search for a flat and view its associated bills and payment records.

6. **User Management** - Create and view tenant accounts and associate tenants with existing flats.

7. **Authentication** - Provide username/password authentication with separate access for owners and tenants.

8. **Owner Dashboard** - Provide owners with access to flat, bill, payment, dues, user, and complaint management.

9. **Tenant Dashboard** - Allow tenants to view their bills, payments, and complaints.

10. **Complaint Management** - Allow tenants to raise complaints and owners to view, respond to, and mark complaints as completed.

11. **Data Persistence** - Store flats, bills, payments, users, and complaints locally using CSV files.

