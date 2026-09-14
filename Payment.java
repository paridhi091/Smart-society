/**
 * Represents a single payment transaction made against a bill.
 */
public class Payment {
    private String flatNo;
    private String month;
    private double amount;
    private String paymentDate; // e.g. 2025-09-14
    private String mode;        // Cash, Cheque, UPI, etc.

    public Payment(String flatNo, String month, double amount, String paymentDate, String mode) {
        this.flatNo = flatNo;
        this.month = month;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.mode = mode;
    }

    public String getFlatNo() { return flatNo; }
    public String getMonth() { return month; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
    public String getMode() { return mode; }

    // CSV row: flatNo,month,amount,paymentDate,mode
    public String toCsv() {
        return flatNo + "," + month + "," + amount + "," + paymentDate + "," + mode;
    }

    public static Payment fromCsv(String line) {
        String[] parts = line.split(",");
        return new Payment(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3], parts[4]);
    }

    @Override
    public String toString() {
        return String.format("Flat %-6s | Month: %-7s | Amount: %8.2f | Date: %-10s | Mode: %s",
                flatNo, month, amount, paymentDate, mode);
    }
}
