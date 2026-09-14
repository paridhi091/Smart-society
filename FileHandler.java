import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Handles reading/writing flats, bills, and payments to CSV files
 * so data persists between runs of the program.
 */
public class FileHandler {
    private static final String FLATS_FILE = "data/flats.csv";
    private static final String BILLS_FILE = "data/bills.csv";
    private static final String PAYMENTS_FILE = "data/payments.csv";

    public static void ensureDataFolder() {
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            System.out.println("Warning: could not create data folder: " + e.getMessage());
        }
    }

    // ---------- Loading ----------

    public static List<Flat> loadFlats() {
        List<Flat> list = new ArrayList<>();
        readLines(FLATS_FILE).forEach(line -> {
            if (!line.isBlank()) list.add(Flat.fromCsv(line));
        });
        return list;
    }

    public static List<Bill> loadBills() {
        List<Bill> list = new ArrayList<>();
        readLines(BILLS_FILE).forEach(line -> {
            if (!line.isBlank()) list.add(Bill.fromCsv(line));
        });
        return list;
    }

    public static List<Payment> loadPayments() {
        List<Payment> list = new ArrayList<>();
        readLines(PAYMENTS_FILE).forEach(line -> {
            if (!line.isBlank()) list.add(Payment.fromCsv(line));
        });
        return list;
    }

    private static List<String> readLines(String path) {
        File file = new File(path);
        if (!file.exists()) return new ArrayList<>();
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.out.println("Warning: could not read " + path + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ---------- Saving ----------

    public static void saveFlats(Collection<Flat> flats) {
        writeLines(FLATS_FILE, flats.stream().map(Flat::toCsv).toList());
    }

    public static void saveBills(Collection<Bill> bills) {
        writeLines(BILLS_FILE, bills.stream().map(Bill::toCsv).toList());
    }

    public static void savePayments(Collection<Payment> payments) {
        writeLines(PAYMENTS_FILE, payments.stream().map(Payment::toCsv).toList());
    }

    private static void writeLines(String path, List<String> lines) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (String line : lines) {
                pw.println(line);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not write " + path + ": " + e.getMessage());
        }
    }
}
