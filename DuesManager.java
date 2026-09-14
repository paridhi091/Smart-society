import java.util.*;

/**
 * Core logic: manages flats, bills, and payments in memory,
 * and provides reporting operations.
 */
public class DuesManager {
    private Map<String, Flat> flats = new LinkedHashMap<>();
    private List<Bill> bills = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();

    // ---------- Matching helper ----------

    /**
     * Normalizes identifiers (flat numbers, months) before comparing them,
     * so "A-101" matches "a-101 ", and "2025-9" matches "2025-09".
     * This avoids "no bill found" failures caused only by typing differences
     * between the screen where a bill was generated and the screen used to
     * record a payment against it.
     */
    private static String normalize(String s) {
        if (s == null) return "";
        String t = s.trim().toUpperCase();
        // Pad a single-digit month segment, e.g. "2025-9" -> "2025-09"
        if (t.matches("\\d{4}-\\d")) {
            t = t.substring(0, 5) + "0" + t.substring(5);
        }
        return t;
    }

    // ---------- Flat operations ----------

    public boolean addFlat(Flat flat) {
        if (getFlat(flat.getFlatNo()) != null) {
            return false; // already exists
        }
        flats.put(flat.getFlatNo(), flat);
        return true;
    }

    public Collection<Flat> getAllFlats() {
        return flats.values();
    }

    public Flat getFlat(String flatNo) {
        String target = normalize(flatNo);
        for (Flat f : flats.values()) {
            if (normalize(f.getFlatNo()).equals(target)) return f;
        }
        return null;
    }

    /**
     * Removes a flat by number. Existing bills/payments for that flat are
     * left untouched (historical records are preserved).
     */
    public boolean removeFlat(String flatNo) {
        String target = normalize(flatNo);
        String matchedKey = null;
        for (String key : flats.keySet()) {
            if (normalize(key).equals(target)) {
                matchedKey = key;
                break;
            }
        }
        if (matchedKey == null) return false;
        flats.remove(matchedKey);
        return true;
    }

    /**
     * Updates a flat's owner, area, and monthly charge. The flat number itself
     * is not changed. Returns false if no matching flat exists.
     */
    public boolean updateFlat(String flatNo, String newOwner, double newArea, double newCharge) {
        String target = normalize(flatNo);
        for (Map.Entry<String, Flat> entry : flats.entrySet()) {
            if (normalize(entry.getKey()).equals(target)) {
                Flat old = entry.getValue();
                flats.put(entry.getKey(), new Flat(old.getFlatNo(), newOwner, newArea, newCharge));
                return true;
            }
        }
        return false;
    }

    // ---------- Bill operations ----------

    /**
     * Generates one bill per flat for the given month, using each flat's
     * fixed monthly charge. Skips flats that already have a bill for that month.
     */
    public int generateMonthlyBills(String month) {
        int count = 0;
        for (Flat flat : flats.values()) {
            boolean exists = bills.stream()
                    .anyMatch(b -> normalize(b.getFlatNo()).equals(normalize(flat.getFlatNo()))
                            && normalize(b.getMonth()).equals(normalize(month)));
            if (!exists) {
                bills.add(new Bill(flat.getFlatNo(), month, flat.getMonthlyCharge(), 0.0));
                count++;
            }
        }
        return count;
    }

    public List<Bill> getAllBills() {
        return bills;
    }

    private Bill findBill(String flatNo, String month) {
        String targetFlat = normalize(flatNo);
        String targetMonth = normalize(month);
        return bills.stream()
                .filter(b -> normalize(b.getFlatNo()).equals(targetFlat) && normalize(b.getMonth()).equals(targetMonth))
                .findFirst()
                .orElse(null);
    }

    // ---------- Payment operations ----------

    /**
     * Records a payment against a flat's bill for a given month.
     * Returns false if no matching bill exists.
     */
    public boolean recordPayment(String flatNo, String month, double amount, String date, String mode) {
        Bill bill = findBill(flatNo, month);
        if (bill == null) {
            return false;
        }
        bill.addPayment(amount);
        payments.add(new Payment(flatNo, month, amount, date, mode));
        return true;
    }

    public List<Payment> getPaymentsForFlat(String flatNo) {
        String target = normalize(flatNo);
        List<Payment> result = new ArrayList<>();
        for (Payment p : payments) {
            if (normalize(p.getFlatNo()).equals(target)) result.add(p);
        }
        return result;
    }

    public List<Payment> getAllPayments() {
        return payments;
    }

    // ---------- Reports ----------

    public List<Bill> getPendingBills() {
        List<Bill> pending = new ArrayList<>();
        for (Bill b : bills) {
            if (!b.isFullyPaid()) pending.add(b);
        }
        return pending;
    }

    public double getTotalCollected() {
        double total = 0;
        for (Bill b : bills) total += b.getAmountPaid();
        return total;
    }

    public double getTotalPending() {
        double total = 0;
        for (Bill b : bills) total += b.getPendingAmount();
        return total;
    }

    public List<Bill> getBillsForFlat(String flatNo) {
        String target = normalize(flatNo);
        List<Bill> result = new ArrayList<>();
        for (Bill b : bills) {
            if (normalize(b.getFlatNo()).equals(target)) result.add(b);
        }
        return result;
    }

    // ---------- Bulk loaders (used by FileHandler) ----------

    public void loadFlats(List<Flat> loaded) {
        for (Flat f : loaded) flats.put(f.getFlatNo(), f);
    }

    public void loadBills(List<Bill> loaded) {
        bills.addAll(loaded);
    }

    public void loadPayments(List<Payment> loaded) {
        payments.addAll(loaded);
    }
}
