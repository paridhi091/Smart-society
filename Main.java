import java.util.*;

/**
 * Housing Society Management System - Console Application.
 *
 * Provides menu-driven management of:
 * - Flats
 * - Monthly bills
 * - Payments
 * - Users
 * - Complaints
 *
 * Data is persisted using CSV files.
 */
public class Main {

    private static Scanner sc = new Scanner(System.in);
    private static DuesManager manager = new DuesManager();

    public static void main(String[] args) {

        FileHandler.ensureDataFolder();

        // Load existing data
        manager.loadFlats(FileHandler.loadFlats());
        manager.loadBills(FileHandler.loadBills());
        manager.loadPayments(FileHandler.loadPayments());
        manager.loadUsers(FileHandler.loadUsers());
        manager.loadComplaints(FileHandler.loadComplaints());

        // Create default owner if no owner exists
        manager.ensureDefaultOwner();

        boolean running = true;

        while (running) {

            printMenu();

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    addFlat();
                    break;

                case "2":
                    generateBills();
                    break;

                case "3":
                    recordPayment();
                    break;

                case "4":
                    viewDuesReport();
                    break;

                case "5":
                    searchFlat();
                    break;

                case "6":
                    userManagement();
                    break;

                case "7":
                    complaintManagement();
                    break;

                case "8":
                    saveAndExit();
                    running = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice, try again.\n"
                    );
            }
        }

        sc.close();
    }

    // =========================
    // MAIN MENU
    // =========================

    private static void printMenu() {

        System.out.println("\n=========================================");
        System.out.println("      HOUSING SOCIETY MANAGEMENT");
        System.out.println("=========================================");
        System.out.println("1. Add Flat");
        System.out.println("2. Generate Monthly Bills");
        System.out.println("3. Record Payment");
        System.out.println("4. View Dues Report");
        System.out.println("5. Search Flat by Number");
        System.out.println("6. User Management");
        System.out.println("7. Complaint Management");
        System.out.println("8. Save & Exit");
        System.out.println("=========================================");
        System.out.print("Enter choice: ");
    }

    // =========================
    // FLAT MANAGEMENT
    // =========================

    private static void addFlat() {

        System.out.println("\n--- ADD FLAT ---");

        System.out.print("Flat No: ");
        String flatNo = sc.nextLine().trim();

        if (flatNo.isEmpty()) {
            System.out.println("Flat number cannot be empty.\n");
            return;
        }

        if (manager.getFlat(flatNo) != null) {
            System.out.println(
                    "A flat with this number already exists.\n"
            );
            return;
        }

        System.out.print("Owner Name: ");
        String owner = sc.nextLine().trim();

        if (owner.isEmpty()) {
            System.out.println("Owner name cannot be empty.\n");
            return;
        }

        System.out.print("Area (sqft): ");
        double area = readDouble();

        if (area <= 0) {
            System.out.println("Area must be greater than zero.\n");
            return;
        }

        System.out.print("Monthly Maintenance Amount: ");
        double charge = readDouble();

        if (charge < 0) {
            System.out.println(
                    "Maintenance amount cannot be negative.\n"
            );
            return;
        }

        boolean added = manager.addFlat(
                new Flat(flatNo, owner, area, charge)
        );

        if (added) {
            System.out.println(
                    "Flat added successfully.\n"
            );
        } else {
            System.out.println(
                    "Could not add flat.\n"
            );
        }
    }

    // =========================
    // BILL MANAGEMENT
    // =========================

    private static void generateBills() {

        System.out.println("\n--- GENERATE MONTHLY BILLS ---");

        System.out.print(
                "Enter month (YYYY-MM, e.g. 2026-09): "
        );

        String month = sc.nextLine().trim();

        if (month.isEmpty()) {
            System.out.println("Month cannot be empty.\n");
            return;
        }

        int count = manager.generateMonthlyBills(month);

        System.out.println(
                "Generated " + count +
                " new bill(s) for " + month + ".\n"
        );
    }

    // =========================
    // PAYMENT MANAGEMENT
    // =========================

    private static void recordPayment() {

        System.out.println("\n--- RECORD PAYMENT ---");

        System.out.print("Flat No: ");
        String flatNo = sc.nextLine().trim();

        if (manager.getFlat(flatNo) == null) {
            System.out.println("Flat not found.\n");
            return;
        }

        System.out.print(
                "Month (YYYY-MM) this payment is for: "
        );

        String month = sc.nextLine().trim();

        System.out.print("Amount Paid: ");
        double amount = readDouble();

        if (amount <= 0) {
            System.out.println(
                    "Payment amount must be greater than zero.\n"
            );
            return;
        }

        System.out.print(
                "Payment Date (YYYY-MM-DD): "
        );

        String date = sc.nextLine().trim();

        System.out.print(
                "Mode (Cash/Cheque/UPI/etc.): "
        );

        String mode = sc.nextLine().trim();

        if (date.isEmpty() || mode.isEmpty()) {
            System.out.println(
                    "Payment date and mode are required.\n"
            );
            return;
        }

        boolean success = manager.recordPayment(
                flatNo,
                month,
                amount,
                date,
                mode
        );

        if (success) {
            System.out.println(
                    "Payment recorded successfully.\n"
            );
        } else {
            System.out.println(
                    "No bill found for that flat/month. " +
                    "Generate the bill first.\n"
            );
        }
    }

    // =========================
    // DUES REPORT
    // =========================

    private static void viewDuesReport() {

        System.out.println("\n--- ALL BILLS ---");

        List<Bill> bills = manager.getAllBills();

        if (bills.isEmpty()) {
            System.out.println("No bills found.");
        } else {
            for (Bill b : bills) {
                System.out.println(b);
            }
        }

        System.out.println("\n--- PENDING DUES ---");

        List<Bill> pending = manager.getPendingBills();

        if (pending.isEmpty()) {

            System.out.println(
                    "No pending dues. Everyone is paid up!"
            );

        } else {

            for (Bill b : pending) {
                System.out.println(b);
            }
        }

        System.out.printf(
                "%nTotal Collected: %.2f%n",
                manager.getTotalCollected()
        );

        System.out.printf(
                "Total Pending:   %.2f%n%n",
                manager.getTotalPending()
        );
    }

    // =========================
    // SEARCH FLAT
    // =========================

    private static void searchFlat() {

        System.out.println("\n--- SEARCH FLAT ---");

        System.out.print("Enter Flat No: ");

        String flatNo = sc.nextLine().trim();

        Flat flat = manager.getFlat(flatNo);

        if (flat == null) {

            System.out.println(
                    "Flat not found.\n"
            );

            return;
        }

        System.out.println("\n" + flat);

        // Bills
        System.out.println("\nBills:");

        List<Bill> bills =
                manager.getBillsForFlat(flatNo);

        if (bills.isEmpty()) {

            System.out.println(
                    "  (no bills yet)"
            );

        } else {

            for (Bill b : bills) {
                System.out.println("  " + b);
            }
        }

        // Payments
        System.out.println("\nPayments:");

        List<Payment> payments =
                manager.getPaymentsForFlat(flatNo);

        if (payments.isEmpty()) {

            System.out.println(
                    "  (no payments yet)"
            );

        } else {

            for (Payment p : payments) {
                System.out.println("  " + p);
            }
        }

        System.out.println();
    }

    // =========================
    // USER MANAGEMENT
    // =========================

    private static void userManagement() {

        boolean back = false;

        while (!back) {

            System.out.println("\n=================================");
            System.out.println(" USER MANAGEMENT");
            System.out.println("=================================");
            System.out.println("1. View Users");
            System.out.println("2. Create Tenant Account");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    viewUsers();
                    break;

                case "2":
                    createTenantAccount();
                    break;

                case "3":
                    back = true;
                    break;

                default:
                    System.out.println(
                            "Invalid choice.\n"
                    );
            }
        }
    }

    private static void viewUsers() {

        System.out.println("\n--- USERS ---");

        List<User> users =
                manager.getAllUsers();

        if (users.isEmpty()) {

            System.out.println(
                    "No users found.\n"
            );

            return;
        }

        for (User user : users) {

            System.out.println(
                    "Username: " + user.getUsername()
            );

            System.out.println(
                    "Name: " + user.getName()
            );

            System.out.println(
                    "Flat No: " +
                    (user.getFlatNo().isEmpty()
                            ? "-"
                            : user.getFlatNo())
            );

            System.out.println(
                    "Role: " + user.getRole()
            );

            System.out.println(
                    "---------------------------------"
            );
        }
    }

    private static void createTenantAccount() {

        System.out.println("\n--- CREATE TENANT ACCOUNT ---");

        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine();

        System.out.print("Tenant Name: ");
        String tenantName = sc.nextLine().trim();

        System.out.print("Flat No: ");
        String flatNo = sc.nextLine().trim();

        if (username.isEmpty() ||
                password.isEmpty() ||
                tenantName.isEmpty() ||
                flatNo.isEmpty()) {

            System.out.println(
                    "Please fill all fields.\n"
            );

            return;
        }

        if (manager.getUser(username) != null) {

            System.out.println(
                    "Username already exists.\n"
            );

            return;
        }

        if (manager.getFlat(flatNo) == null) {

            System.out.println(
                    "Flat does not exist.\n"
            );

            return;
        }

        boolean success =
                manager.addTenantUser(
                        username,
                        password,
                        tenantName,
                        flatNo
                );

        if (success) {

            System.out.println(
                    "Tenant account created successfully.\n"
            );

        } else {

            System.out.println(
                    "Could not create tenant account.\n"
            );
        }
    }

    // =========================
    // COMPLAINT MANAGEMENT
    // =========================

    private static void complaintManagement() {

        boolean back = false;

        while (!back) {

            System.out.println("\n=================================");
            System.out.println(" COMPLAINT MANAGEMENT");
            System.out.println("=================================");
            System.out.println("1. View All Complaints");
            System.out.println("2. View Open Complaints");
            System.out.println("3. Raise Complaint");
            System.out.println("4. Respond to Complaint");
            System.out.println("5. Mark Complaint as Done");
            System.out.println("6. Back");
            System.out.print("Enter choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    viewComplaints();
                    break;

                case "2":
                    viewOpenComplaints();
                    break;

                case "3":
                    raiseComplaint();
                    break;

                case "4":
                    respondToComplaint();
                    break;

                case "5":
                    markComplaintDone();
                    break;

                case "6":
                    back = true;
                    break;

                default:
                    System.out.println(
                            "Invalid choice.\n"
                    );
            }
        }
    }

    private static void viewComplaints() {

        System.out.println("\n--- ALL COMPLAINTS ---");

        List<Complaint> complaints =
                manager.getAllComplaints();

        if (complaints.isEmpty()) {

            System.out.println(
                    "No complaints found.\n"
            );

            return;
        }

        for (Complaint c : complaints) {
            printComplaint(c);
        }
    }

    private static void viewOpenComplaints() {

        System.out.println("\n--- OPEN COMPLAINTS ---");

        List<Complaint> complaints =
                manager.getOpenComplaints();

        if (complaints.isEmpty()) {

            System.out.println(
                    "No open complaints.\n"
            );

            return;
        }

        for (Complaint c : complaints) {
            printComplaint(c);
        }
    }

    private static void raiseComplaint() {

        System.out.println("\n--- RAISE COMPLAINT ---");

        System.out.print("Flat No: ");
        String flatNo = sc.nextLine().trim();

        Flat flat = manager.getFlat(flatNo);

        if (flat == null) {

            System.out.println(
                    "Flat not found.\n"
            );

            return;
        }

        System.out.print("Tenant Name: ");
        String tenantName = sc.nextLine().trim();

        System.out.print("Subject: ");
        String subject = sc.nextLine().trim();

        System.out.print("Description: ");
        String description = sc.nextLine().trim();

        System.out.print(
                "Created Date (YYYY-MM-DD): "
        );

        String date = sc.nextLine().trim();

        if (tenantName.isEmpty() ||
                subject.isEmpty() ||
                description.isEmpty() ||
                date.isEmpty()) {

            System.out.println(
                    "All fields are required.\n"
            );

            return;
        }

        Complaint complaint =
                manager.raiseComplaint(
                        flatNo,
                        tenantName,
                        subject,
                        description,
                        date
                );

        System.out.println(
                "\nComplaint raised successfully!"
        );

        System.out.println(
                "Complaint ID: " +
                complaint.getId() + "\n"
        );
    }

    private static void respondToComplaint() {

        System.out.println("\n--- RESPOND TO COMPLAINT ---");

        System.out.print("Complaint ID: ");

        int id = readInt();

        Complaint complaint =
                manager.getComplaint(id);

        if (complaint == null) {

            System.out.println(
                    "Complaint not found.\n"
            );

            return;
        }

        System.out.println(
                "Subject: " +
                complaint.getSubject()
        );

        System.out.println(
                "Current Status: " +
                complaint.getStatus()
        );

        System.out.print("Enter Response: ");

        String response =
                sc.nextLine().trim();

        if (response.isEmpty()) {

            System.out.println(
                    "Response cannot be empty.\n"
            );

            return;
        }

        boolean success =
                manager.respondToComplaint(
                        id,
                        response
                );

        if (success) {

            System.out.println(
                    "Response added successfully.\n"
            );

        } else {

            System.out.println(
                    "Could not respond to complaint.\n"
            );
        }
    }

    private static void markComplaintDone() {

        System.out.println("\n--- MARK COMPLAINT AS DONE ---");

        System.out.print("Complaint ID: ");

        int id = readInt();

        Complaint complaint =
                manager.getComplaint(id);

        if (complaint == null) {

            System.out.println(
                    "Complaint not found.\n"
            );

            return;
        }

        boolean success =
                manager.markComplaintDone(id);

        if (success) {

            System.out.println(
                    "Complaint marked as DONE.\n"
            );

        } else {

            System.out.println(
                    "Could not update complaint.\n"
            );
        }
    }

    private static void printComplaint(
            Complaint c) {

        System.out.println(
                "Complaint ID: " +
                c.getId()
        );

        System.out.println(
                "Flat No: " +
                c.getFlatNo()
        );

        System.out.println(
                "Tenant: " +
                c.getTenantName()
        );

        System.out.println(
                "Subject: " +
                c.getSubject()
        );

        System.out.println(
                "Description: " +
                c.getDescription()
        );

        System.out.println(
                "Status: " +
                c.getStatus()
        );

        System.out.println(
                "Response: " +
                (c.getResponse().isEmpty()
                        ? "-"
                        : c.getResponse())
        );

        System.out.println(
                "Created Date: " +
                c.getCreatedDate()
        );

        System.out.println(
                "---------------------------------"
        );
    }

    // =========================
    // SAVE & EXIT
    // =========================

    private static void saveAndExit() {

        FileHandler.saveFlats(
                manager.getAllFlats()
        );

        FileHandler.saveBills(
                manager.getAllBills()
        );

        FileHandler.savePayments(
                manager.getAllPayments()
        );

        FileHandler.saveUsers(
                manager.getAllUsers()
        );

        FileHandler.saveComplaints(
                manager.getAllComplaints()
        );

        System.out.println(
                "\nData saved successfully."
        );

        System.out.println(
                "Goodbye!"
        );
    }

    // =========================
    // INPUT HELPERS
    // =========================

    private static double readDouble() {

        while (true) {

            try {

                return Double.parseDouble(
                        sc.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.print(
                        "Invalid number, try again: "
                );
            }
        }
    }

    private static int readInt() {

        while (true) {

            try {

                return Integer.parseInt(
                        sc.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.print(
                        "Invalid number, try again: "
                );
            }
        }
    }
}