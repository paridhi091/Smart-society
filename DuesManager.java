// import java.util.*;

// /**
//  * Core logic: manages flats, bills, and payments in memory,
//  * and provides reporting operations.
//  */
// public class DuesManager {
//     private Map<String, Flat> flats = new LinkedHashMap<>();
//     private List<Bill> bills = new ArrayList<>();
//     private List<Payment> payments = new ArrayList<>();

//     // ---------- Matching helper ----------

//     /**
//      * Normalizes identifiers (flat numbers, months) before comparing them,
//      * so "A-101" matches "a-101 ", and "2025-9" matches "2025-09".
//      * This avoids "no bill found" failures caused only by typing differences
//      * between the screen where a bill was generated and the screen used to
//      * record a payment against it.
//      */
//     private static String normalize(String s) {
//         if (s == null) return "";
//         String t = s.trim().toUpperCase();
//         // Pad a single-digit month segment, e.g. "2025-9" -> "2025-09"
//         if (t.matches("\\d{4}-\\d")) {
//             t = t.substring(0, 5) + "0" + t.substring(5);
//         }
//         return t;
//     }

//     // ---------- Flat operations ----------

//     public boolean addFlat(Flat flat) {
//         if (getFlat(flat.getFlatNo()) != null) {
//             return false; // already exists
//         }
//         flats.put(flat.getFlatNo(), flat);
//         return true;
//     }

//     public Collection<Flat> getAllFlats() {
//         return flats.values();
//     }

//     public Flat getFlat(String flatNo) {
//         String target = normalize(flatNo);
//         for (Flat f : flats.values()) {
//             if (normalize(f.getFlatNo()).equals(target)) return f;
//         }
//         return null;
//     }

//     /**
//      * Removes a flat by number. Existing bills/payments for that flat are
//      * left untouched (historical records are preserved).
//      */
//     public boolean removeFlat(String flatNo) {
//         String target = normalize(flatNo);
//         String matchedKey = null;
//         for (String key : flats.keySet()) {
//             if (normalize(key).equals(target)) {
//                 matchedKey = key;
//                 break;
//             }
//         }
//         if (matchedKey == null) return false;
//         flats.remove(matchedKey);
//         return true;
//     }

//     /**
//      * Updates a flat's owner, area, and monthly charge. The flat number itself
//      * is not changed. Returns false if no matching flat exists.
//      */
//     public boolean updateFlat(String flatNo, String newOwner, double newArea, double newCharge) {
//         String target = normalize(flatNo);
//         for (Map.Entry<String, Flat> entry : flats.entrySet()) {
//             if (normalize(entry.getKey()).equals(target)) {
//                 Flat old = entry.getValue();
//                 flats.put(entry.getKey(), new Flat(old.getFlatNo(), newOwner, newArea, newCharge));
//                 return true;
//             }
//         }
//         return false;
//     }

//     // ---------- Bill operations ----------

//     /**
//      * Generates one bill per flat for the given month, using each flat's
//      * fixed monthly charge. Skips flats that already have a bill for that month.
//      */
//     public int generateMonthlyBills(String month) {
//         int count = 0;
//         for (Flat flat : flats.values()) {
//             boolean exists = bills.stream()
//                     .anyMatch(b -> normalize(b.getFlatNo()).equals(normalize(flat.getFlatNo()))
//                             && normalize(b.getMonth()).equals(normalize(month)));
//             if (!exists) {
//                 bills.add(new Bill(flat.getFlatNo(), month, flat.getMonthlyCharge(), 0.0));
//                 count++;
//             }
//         }
//         return count;
//     }

//     public List<Bill> getAllBills() {
//         return bills;
//     }

//     private Bill findBill(String flatNo, String month) {
//         String targetFlat = normalize(flatNo);
//         String targetMonth = normalize(month);
//         return bills.stream()
//                 .filter(b -> normalize(b.getFlatNo()).equals(targetFlat) && normalize(b.getMonth()).equals(targetMonth))
//                 .findFirst()
//                 .orElse(null);
//     }

//     // ---------- Payment operations ----------

//     /**
//      * Records a payment against a flat's bill for a given month.
//      * Returns false if no matching bill exists.
//      */
//     public boolean recordPayment(String flatNo, String month, double amount, String date, String mode) {
//         Bill bill = findBill(flatNo, month);
//         if (bill == null) {
//             return false;
//         }
//         bill.addPayment(amount);
//         payments.add(new Payment(flatNo, month, amount, date, mode));
//         return true;
//     }

//     public List<Payment> getPaymentsForFlat(String flatNo) {
//         String target = normalize(flatNo);
//         List<Payment> result = new ArrayList<>();
//         for (Payment p : payments) {
//             if (normalize(p.getFlatNo()).equals(target)) result.add(p);
//         }
//         return result;
//     }

//     public List<Payment> getAllPayments() {
//         return payments;
//     }

//     // ---------- Reports ----------

//     public List<Bill> getPendingBills() {
//         List<Bill> pending = new ArrayList<>();
//         for (Bill b : bills) {
//             if (!b.isFullyPaid()) pending.add(b);
//         }
//         return pending;
//     }

//     public double getTotalCollected() {
//         double total = 0;
//         for (Bill b : bills) total += b.getAmountPaid();
//         return total;
//     }

//     public double getTotalPending() {
//         double total = 0;
//         for (Bill b : bills) total += b.getPendingAmount();
//         return total;
//     }

//     public List<Bill> getBillsForFlat(String flatNo) {
//         String target = normalize(flatNo);
//         List<Bill> result = new ArrayList<>();
//         for (Bill b : bills) {
//             if (normalize(b.getFlatNo()).equals(target)) result.add(b);
//         }
//         return result;
//     }

//     // ---------- Bulk loaders (used by FileHandler) ----------

//     public void loadFlats(List<Flat> loaded) {
//         for (Flat f : loaded) flats.put(f.getFlatNo(), f);
//     }

//     public void loadBills(List<Bill> loaded) {
//         bills.addAll(loaded);
//     }

//     public void loadPayments(List<Payment> loaded) {
//         payments.addAll(loaded);
//     }
// }
import java.util.*;

/**
 * Core logic: manages flats, bills, and payments in memory,
 * and provides reporting operations.
 */
public class DuesManager {
    private Map<String, Flat> flats = new LinkedHashMap<>();
    private List<Bill> bills = new ArrayList<>();
    private List<Payment> payments = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private List<Complaint> complaints = new ArrayList<>();
    private int nextComplaintId = 1;

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

    public void loadUsers(List<User> loaded) {
        users.addAll(loaded);
    }

    public void loadComplaints(List<Complaint> loaded) {
        complaints.addAll(loaded);
        int maxId = 0;
        for (Complaint c : complaints) maxId = Math.max(maxId, c.getId());
        nextComplaintId = maxId + 1;
    }

    // ---------- User accounts ----------

    /**
     * Creates the default owner login (username "owner", password
     * "owner123") the first time the app runs with no accounts at all,
     * so there is always a way to log in.
     */
    public void ensureDefaultOwner() {
        boolean hasOwner = users.stream().anyMatch(User::isOwner);
        if (!hasOwner) {
            users.add(new User("owner", "owner123", "OWNER", "", "Society Owner"));
        }
    }

    public User getUser(String username) {
        String target = normalize(username);
        for (User u : users) {
            if (normalize(u.getUsername()).equals(target)) return u;
        }
        return null;
    }

    public User authenticate(String username, String password) {
        User u = getUser(username);
        if (u == null) return null;
        return u.getPassword().equals(password) ? u : null;
    }

    /**
     * Creates a tenant login account linked to a flat. Returns false if the
     * username is already taken or the flat number doesn't exist.
     */
    public boolean addTenantUser(String username, String password, String tenantName, String flatNo) {
        if (getUser(username) != null) return false;
        if (getFlat(flatNo) == null) return false;
        users.add(new User(username, password, "TENANT", flatNo, tenantName));
        return true;
    }

    public List<User> getAllUsers() {
        return users;
    }

    public List<User> getTenantUsers() {
        List<User> result = new ArrayList<>();
        for (User u : users) {
            if (u.isTenant()) result.add(u);
        }
        return result;
    }

    // ---------- Complaints ----------

    public Complaint raiseComplaint(String flatNo, String tenantName, String subject, String description, String createdDate) {
        Complaint c = new Complaint(nextComplaintId++, flatNo, tenantName, subject, description, "OPEN", "", createdDate);
        complaints.add(c);
        return c;
    }

    public List<Complaint> getComplaintsForFlat(String flatNo) {
        String target = normalize(flatNo);
        List<Complaint> result = new ArrayList<>();
        for (Complaint c : complaints) {
            if (normalize(c.getFlatNo()).equals(target)) result.add(c);
        }
        return result;
    }

    public List<Complaint> getAllComplaints() {
        return complaints;
    }

    public List<Complaint> getOpenComplaints() {
        List<Complaint> result = new ArrayList<>();
        for (Complaint c : complaints) {
            if (!c.isDone()) result.add(c);
        }
        return result;
    }

    public Complaint getComplaint(int id) {
        for (Complaint c : complaints) {
            if (c.getId() == id) return c;
        }
        return null;
    }

    public boolean respondToComplaint(int id, String response) {
        Complaint c = getComplaint(id);
        if (c == null) return false;
        c.setResponse(response);
        return true;
    }

    public boolean markComplaintDone(int id) {
        Complaint c = getComplaint(id);
        if (c == null) return false;
        c.setStatus("DONE");
        return true;
    }
}