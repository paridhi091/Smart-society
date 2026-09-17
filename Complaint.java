public class Complaint {
    private int id;
    private String flatNo;
    private String tenantName;
    private String subject;
    private String description;
    private String status;
    private String response;
    private String createdDate;

    public Complaint(
            int id,
            String flatNo,
            String tenantName,
            String subject,
            String description,
            String status,
            String response,
            String createdDate) {

        this.id = id;
        this.flatNo = flatNo;
        this.tenantName = tenantName;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.response = response;
        this.createdDate = createdDate;
    }

    // ---------- Getters ----------

    public int getId() {
        return id;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getResponse() {
        return response;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    // ---------- Setters ----------

    public void setResponse(String response) {
        this.response = response;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ---------- Status ----------

    public boolean isDone() {
        return "DONE".equalsIgnoreCase(status);
    }

    // ---------- CSV ----------

    public String toCsv() {
        return id + "," +
               escape(flatNo) + "," +
               escape(tenantName) + "," +
               escape(subject) + "," +
               escape(description) + "," +
               escape(status) + "," +
               escape(response) + "," +
               escape(createdDate);
    }

    public static Complaint fromCsv(String line) {
        String[] parts = line.split(",", -1);

        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid complaint CSV: " + line);
        }

        return new Complaint(
            Integer.parseInt(parts[0]),
            unescape(parts[1]),
            unescape(parts[2]),
            unescape(parts[3]),
            unescape(parts[4]),
            unescape(parts[5]),
            unescape(parts[6]),
            unescape(parts[7])
        );
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                    .replace(",", "\\,")
                    .replace("\n", "\\n");
    }

    private static String unescape(String value) {
        if (value == null) return "";

        StringBuilder result = new StringBuilder();
        boolean escaped = false;

        for (char c : value.toCharArray()) {
            if (escaped) {
                if (c == 'n') {
                    result.append('\n');
                } else {
                    result.append(c);
                }
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else {
                result.append(c);
            }
        }

        if (escaped) {
            result.append('\\');
        }

        return result.toString();
    }

    @Override
    public String toString() {
        return "#" + id + " - " + subject + " (" + status + ")";
    }
}