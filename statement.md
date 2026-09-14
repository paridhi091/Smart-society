# Project Statement - Housing Society Dues Tracker

## Problem Statement

Housing societies typically collect a monthly maintenance charge from every
flat/unit to cover shared expenses (security, cleaning, repairs, common
utilities, etc.). In many smaller societies, this collection process is
still managed manually - through paper registers or ad-hoc spreadsheets
maintained by a volunteer treasurer or secretary. This manual approach leads
to several recurring problems:

- No single, reliable view of which flats have paid and which are pending
  for a given month
- Difficulty tracking partial payments against a bill
- No easy way to total collections vs. outstanding dues at a glance
- Errors from manually recalculating amounts owed each month
- No searchable history of a flat's payment record when disputes arise

The Housing Society Dues Tracker addresses this by providing a lightweight,
dedicated application to record flats, generate monthly bills, log
payments, and report on dues - removing the need for manual bookkeeping.

## Scope of the Project

This project covers the core financial-tracking workflow for a housing
society's maintenance dues:

**In scope:**
- Registering and maintaining flat/unit records
- Generating monthly maintenance bills per flat based on a fixed charge
- Recording full or partial payments against a bill, with date and mode
- Reporting on pending dues and total collections
- Searching a flat's bill and payment history
- Exporting the pending-dues report for printing/sharing
- Local, file-based data persistence (no server setup required)

**Out of scope (possible future work, not implemented here):**
- Per-square-foot or slab-based automatic charge calculation
- Late payment interest/penalty calculation
- Multi-user login, authentication, or role-based access (admin vs. member)
- Email/SMS payment reminders
- PDF receipt generation
- A central database or web-hosted backend
- Handling of non-maintenance charges (e.g. one-time festival funds,
  repair funds) as a separate category from regular maintenance

## Target Users

- **Housing society treasurers/secretaries** - the primary users, who add
  flats, generate monthly bills, and record payments as they come in
- **Housing society committee members** - who may use the dues report to
  review collection status during committee meetings
- **Small residential societies** without the budget or technical need for
  a full web-based society management platform

The tool is designed for a single person operating it locally on their own
computer (desktop application, local file storage) rather than multiple
people accessing it simultaneously over a network.

## High-Level Features

1. **Flat Management** - add, edit, and delete flat records (flat number,
   owner, area, monthly charge)
2. **Bill Generation** - generate a maintenance bill for every registered
   flat for a chosen month, in a single action
3. **Payment Recording** - log payments (full or partial) against a flat's
   bill, with date and payment mode
4. **Dues Reporting** - view all pending bills and running totals for
   amounts collected vs. pending
5. **Flat Search/History** - look up a flat's complete bill and payment
   history
6. **Data Export** - export the current pending-dues report to CSV
7. **Data Persistence** - all data is automatically saved locally and
   reloaded on the next run, so no data is lost between sessions
