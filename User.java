public class User {
    private String username;
    private String password;
    private String role;
    private String flatNo;
    private String name;

    public User(String username, String password, String role, String flatNo, String name) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.flatNo = flatNo;
        this.name = name;
    }

    // ---------- Getters ----------

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getFlatNo() {
        return flatNo;
    }

    public String getName() {
        return name;
    }

    // ---------- Role checks ----------

    public boolean isOwner() {
        return "OWNER".equalsIgnoreCase(role);
    }

    public boolean isTenant() {
        return "TENANT".equalsIgnoreCase(role);
    }

    // ---------- Setters ----------

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFlatNo(String flatNo) {
        this.flatNo = flatNo;
    }

    // ---------- CSV ----------

    public String toCsv() {
        return escape(username) + "," +
               escape(password) + "," +
               escape(role) + "," +
               escape(flatNo) + "," +
               escape(name);
    }

    public static User fromCsv(String line) {
        String[] parts = line.split(",", -1);

        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid user CSV: " + line);
        }

        return new User(
            unescape(parts[0]),
            unescape(parts[1]),
            unescape(parts[2]),
            unescape(parts[3]),
            unescape(parts[4])
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
        return name + " (" + username + ")";
    }
}