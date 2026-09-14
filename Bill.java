/**
 * Represents a single month's maintenance bill for a flat.
 */
public class Bill {
    private String flatNo;
    private String month;      // format: YYYY-MM, e.g. 2025-09
    private double amount;     // total amount due
    private double amountPaid; // running total paid against this bill

    public Bill(String flatNo, String month, double amount, double amountPaid) {
        this.flatNo = flatNo;
        this.month = month;
        this.amount = amount;
        this.amountPaid = amountPaid;
    }

    public String getFlatNo() { return flatNo; }
    public String getMonth() { return month; }
    public double getAmount() { return amount; }
    public double getAmountPaid() { return amountPaid; }

    public double getPendingAmount() {
        return Math.max(0, amount - amountPaid);
    }

    public boolean isFullyPaid() {
        return amountPaid >= amount;
    }

    public void addPayment(double amt) {
        this.amountPaid += amt;
    }

    // CSV row: flatNo,month,amount,amountPaid
    public String toCsv() {
        return flatNo + "," + month + "," + amount + "," + amountPaid;
    }

    public static Bill fromCsv(String line) {
        String[] parts = line.split(",");
        return new Bill(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }

    @Override
    public String toString() {
        String status = isFullyPaid() ? "PAID" : "PENDING";
        return String.format("Flat %-6s | Month: %-7s | Due: %8.2f | Paid: %8.2f | Pending: %8.2f | %s",
                flatNo, month, amount, amountPaid, getPendingAmount(), status);
    }
}
