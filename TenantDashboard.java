import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TenantDashboard extends JFrame {

    private DuesManager manager;
    private User user;

    private DefaultTableModel billsModel;
    private DefaultTableModel paymentsModel;
    private DefaultTableModel complaintsModel;

    public TenantDashboard(DuesManager manager, User user) {

        this.manager = manager;
        this.user = user;

        setTitle("Tenant Dashboard - " + user.getName());
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        buildUI();
    }

    private void buildUI() {

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("My Dashboard", buildDashboardPanel());
        tabs.addTab("My Bills", buildBillsPanel());
        tabs.addTab("My Payments", buildPaymentsPanel());
        tabs.addTab("My Complaints", buildComplaintsPanel());

        add(tabs);
    }

    // --------------------------------------------------
    // DASHBOARD
    // --------------------------------------------------

    private JPanel buildDashboardPanel() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));

        panel.setBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JLabel welcome = new JLabel(
                "Welcome, " + user.getName()
        );

        welcome.setFont(new Font("Arial", Font.BOLD, 24));

        panel.add(welcome, BorderLayout.NORTH);

        JPanel info = new JPanel(new GridLayout(2, 2, 15, 15));

        info.add(createCard("Flat Number", user.getFlatNo()));

        double pending = 0;

        for (Bill bill :
                manager.getBillsForFlat(user.getFlatNo())) {

            pending += bill.getPendingAmount();
        }

        info.add(createCard(
                "Pending Amount",
                "₹" + String.format("%.2f", pending)
        ));

        info.add(createCard(
                "Total Bills",
                String.valueOf(
                        manager.getBillsForFlat(user.getFlatNo()).size()
                )
        ));

        info.add(createCard(
                "Complaints",
                String.valueOf(
                        manager.getComplaintsForFlat(user.getFlatNo()).size()
                )
        ));

        panel.add(info, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCard(String title, String value) {

        JPanel panel = new JPanel(new GridLayout(2, 1));

        panel.setBorder(
                BorderFactory.createTitledBorder(title)
        );

        JLabel valueLabel =
                new JLabel(value, SwingConstants.CENTER);

        valueLabel.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        panel.add(valueLabel);

        return panel;
    }

    // --------------------------------------------------
    // BILLS
    // --------------------------------------------------

    private JPanel buildBillsPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        billsModel = new DefaultTableModel(
                new Object[]{
                        "Flat No",
                        "Month",
                        "Amount",
                        "Paid",
                        "Pending",
                        "Status"
                }, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;
            }
        };

        JTable table = new JTable(billsModel);

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        refreshBills();

        return panel;
    }

    private void refreshBills() {

        billsModel.setRowCount(0);

        for (Bill bill :
                manager.getBillsForFlat(user.getFlatNo())) {

            String status =
                    bill.isFullyPaid()
                            ? "PAID"
                            : "PENDING";

            billsModel.addRow(new Object[]{
                    bill.getFlatNo(),
                    bill.getMonth(),
                    bill.getAmount(),
                    bill.getAmountPaid(),
                    bill.getPendingAmount(),
                    status
            });
        }
    }

    // --------------------------------------------------
    // PAYMENTS
    // --------------------------------------------------

    private JPanel buildPaymentsPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        paymentsModel = new DefaultTableModel(
                new Object[]{
                        "Flat No",
                        "Month",
                        "Amount",
                        "Date",
                        "Mode"
                }, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;
            }
        };

        JTable table = new JTable(paymentsModel);

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        refreshPayments();

        return panel;
    }

    private void refreshPayments() {

        paymentsModel.setRowCount(0);

        for (Payment payment :
                manager.getPaymentsForFlat(user.getFlatNo())) {

            paymentsModel.addRow(new Object[]{
                    payment.getFlatNo(),
                    payment.getMonth(),
                    payment.getAmount(),
                    payment.getDate(),
                    payment.getMode()
            });
        }
    }

    // --------------------------------------------------
    // COMPLAINTS
    // --------------------------------------------------

    private JPanel buildComplaintsPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        complaintsModel = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Subject",
                        "Description",
                        "Status",
                        "Response",
                        "Date"
                }, 0) {

            @Override
            public boolean isCellEditable(
                    int row,
                    int column) {

                return false;
            }
        };

        JTable table = new JTable(complaintsModel);

        panel.add(
                new JScrollPane(table),
                BorderLayout.CENTER
        );

        JPanel form =
                new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField subjectField =
                new JTextField();

        JTextArea descriptionArea =
                new JTextArea(4, 20);

        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JButton raiseButton =
                new JButton("Raise Complaint");

        form.add(new JLabel("Subject:"));
        form.add(subjectField);

        form.add(new JLabel("Description:"));
        form.add(new JScrollPane(descriptionArea));

        form.add(new JLabel(""));
        form.add(raiseButton);

        panel.add(form, BorderLayout.SOUTH);

        raiseButton.addActionListener(e -> {

            String subject =
                    subjectField.getText().trim();

            String description =
                    descriptionArea.getText().trim();

            if (subject.isEmpty() ||
                    description.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all details."
                );

                return;
            }

            manager.raiseComplaint(
                    user.getFlatNo(),
                    user.getName(),
                    subject,
                    description,
                    java.time.LocalDate.now().toString()
            );

            FileHandler.saveComplaints(
                    manager.getAllComplaints()
            );

            subjectField.setText("");
            descriptionArea.setText("");

            refreshComplaints();

            JOptionPane.showMessageDialog(
                    this,
                    "Complaint raised successfully."
            );
        });

        refreshComplaints();

        return panel;
    }

    private void refreshComplaints() {

        complaintsModel.setRowCount(0);

        for (Complaint complaint :
                manager.getComplaintsForFlat(
                        user.getFlatNo())) {

            complaintsModel.addRow(new Object[]{
                    complaint.getId(),
                    complaint.getSubject(),
                    complaint.getDescription(),
                    complaint.getStatus(),
                    complaint.getResponse().isEmpty()
                            ? "-"
                            : complaint.getResponse(),
                    complaint.getCreatedDate()
            });
        }
    }
}
