import java.io.Serializable;

/**
 * Represents a single flat/unit in the housing society.
 */
public class Flat {
    private String flatNo;
    private String ownerName;
    private double areaSqft;
    private double monthlyCharge; // fixed maintenance amount for this flat

    public Flat(String flatNo, String ownerName, double areaSqft, double monthlyCharge) {
        this.flatNo = flatNo;
        this.ownerName = ownerName;
        this.areaSqft = areaSqft;
        this.monthlyCharge = monthlyCharge;
    }

    public String getFlatNo() { return flatNo; }
    public String getOwnerName() { return ownerName; }
    public double getAreaSqft() { return areaSqft; }
    public double getMonthlyCharge() { return monthlyCharge; }

    // CSV row: flatNo,ownerName,areaSqft,monthlyCharge
    public String toCsv() {
        return flatNo + "," + ownerName + "," + areaSqft + "," + monthlyCharge;
    }

    public static Flat fromCsv(String line) {
        String[] parts = line.split(",");
        return new Flat(parts[0], parts[1], Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }

    @Override
    public String toString() {
        return String.format("Flat %-6s | Owner: %-15s | Area: %.1f sqft | Maintenance: %.2f",
                flatNo, ownerName, areaSqft, monthlyCharge);
    }
}
