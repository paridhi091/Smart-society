import javax.swing.*;
import java.awt.*;

public class LoginGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    private DuesManager manager;

    public LoginGUI(DuesManager manager) {
        this.manager = manager;

        setTitle("Housing Society - Login");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildUI();
    }

    private void buildUI() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        JLabel title = new JLabel(
                "HOUSING SOCIETY MANAGEMENT",
                SwingConstants.CENTER
        );
        title.setFont(new Font("Arial", Font.BOLD, 20));

        mainPanel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 15));

        form.add(new JLabel("Username:"));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        form.add(passwordField);

        mainPanel.add(form, BorderLayout.CENTER);

        JButton loginButton = new JButton("Login");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> login());

        passwordField.addActionListener(e -> login());

        add(mainPanel);
    }

    private void login() {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password."
            );
            return;
        }

        User user = manager.authenticate(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid username or password.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        dispose();

        if (user.isOwner()) {

            MainGUI ownerGUI = new MainGUI(manager);
            ownerGUI.setVisible(true);

        } else if (user.isTenant()) {

            TenantDashboard tenantGUI =
                    new TenantDashboard(manager, user);

            tenantGUI.setVisible(true);
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            DuesManager manager = new DuesManager();

            FileHandler.ensureDataFolder();

            manager.loadFlats(FileHandler.loadFlats());
            manager.loadBills(FileHandler.loadBills());
            manager.loadPayments(FileHandler.loadPayments());
            manager.loadUsers(FileHandler.loadUsers());
            manager.loadComplaints(FileHandler.loadComplaints());

            manager.ensureDefaultOwner();

            new LoginGUI(manager).setVisible(true);
        });
    }
}