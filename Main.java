import java.util.*;

/**
 * Housing Society Dues Tracker - console application.
 * Menu-driven CRUD for flats, bills, and payments, with CSV persistence.
 */
public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static DuesManager manager = new DuesManager();

    public static void main(String[] args) {
        FileHandler.ensureDataFolder();
        // Load any previously saved data
        manager.loadFlats(FileHandler.loadFlats());
        manager.loadBills(FileHandler.loadBills());
        manager.loadPayments(FileHandler.loadPayments());

        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": addFlat(); break;
                case "2": generateBills(); break;
                case "3": recordPayment(); break;
                case "4": viewDuesReport(); break;
                case "5": searchFlat(); break;
                case "6": saveAndExit(); running = false; break;
                default: System.out.println("Invalid choice, try again.\n");
            }
        }
        sc.close();
    }

    private static void printMenu() {
        System.out.println("=========================================");
        System.out.println(" HOUSING SOCIETY DUES TRACKER");
        System.out.println("=========================================");
        System.out.println("1. Add Flat");
        System.out.println("2. Generate Monthly Bills");
        System.out.println("3. Record Payment");
        System.out.println("4. View Dues Report");
        System.out.println("5. Search Flat by Number");
        System.out.println("6. Save & Exit");
        System.out.print("Enter choice: ");
    }

    private static void addFlat() {
        System.out.print("Flat No: ");
        String flatNo = sc.nextLine().trim();
        System.out.print("Owner Name: ");
        String owner = sc.nextLine().trim();
        System.out.print("Area (sqft): ");
        double area = readDouble();
        System.out.print("Monthly Maintenance Amount: ");
        double charge = readDouble();

        boolean added = manager.addFlat(new Flat(flatNo, owner, area, charge));
        if (added) {
            System.out.println("Flat added successfully.\n");
        } else {
            System.out.println("A flat with this number already exists.\n");
        }
    }

    private static void generateBills() {
        System.out.print("Enter month to generate bills for (format YYYY-MM, e.g. 2025-09): ");
        String month = sc.nextLine().trim();
        int count = manager.generateMonthlyBills(month);
        System.out.println("Generated " + count + " new bill(s) for " + month + ".\n");
    }

    private static void recordPayment() {
        System.out.print("Flat No: ");
        String flatNo = sc.nextLine().trim();
        System.out.print("Month (YYYY-MM) this payment is for: ");
        String month = sc.nextLine().trim();
        System.out.print("Amount Paid: ");
        double amount = readDouble();
        System.out.print("Payment Date (YYYY-MM-DD): ");
        String date = sc.nextLine().trim();
        System.out.print("Mode (Cash/Cheque/UPI/etc.): ");
        String mode = sc.nextLine().trim();

        boolean ok = manager.recordPayment(flatNo, month, amount, date, mode);
        if (ok) {
            System.out.println("Payment recorded successfully.\n");
        } else {
            System.out.println("No bill found for that flat/month. Generate the bill first.\n");
        }
    }

    private static void viewDuesReport() {
        System.out.println("\n--- ALL BILLS ---");
        for (Bill b : manager.getAllBills()) {
            System.out.println(b);
        }

        System.out.println("\n--- PENDING DUES ---");
        List<Bill> pending = manager.getPendingBills();
        if (pending.isEmpty()) {
            System.out.println("No pending dues. Everyone is paid up!");
        } else {
            for (Bill b : pending) {
                System.out.println(b);
            }
        }

        System.out.printf("%nTotal Collected: %.2f%n", manager.getTotalCollected());
        System.out.printf("Total Pending:   %.2f%n%n", manager.getTotalPending());
    }

    private static void searchFlat() {
        System.out.print("Enter Flat No: ");
        String flatNo = sc.nextLine().trim();
        Flat flat = manager.getFlat(flatNo);
        if (flat == null) {
            System.out.println("Flat not found.\n");
            return;
        }
        System.out.println("\n" + flat);

        System.out.println("Bills:");
        List<Bill> bills = manager.getBillsForFlat(flatNo);
        if (bills.isEmpty()) System.out.println("  (no bills yet)");
        for (Bill b : bills) System.out.println("  " + b);

        System.out.println("Payments:");
        List<Payment> pays = manager.getPaymentsForFlat(flatNo);
        if (pays.isEmpty()) System.out.println("  (no payments yet)");
        for (Payment p : pays) System.out.println("  " + p);
        System.out.println();
    }

    private static void saveAndExit() {
        FileHandler.saveFlats(manager.getAllFlats());
        FileHandler.saveBills(manager.getAllBills());
        FileHandler.savePayments(manager.getAllPayments());
        System.out.println("Data saved. Goodbye!");
    }

    private static double readDouble() {
        while (true) {
            try {
                return Double.parseDouble(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number, try again: ");
            }
        }
    }
}
