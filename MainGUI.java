import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Collection;

/**
 * Swing desktop GUI for the Housing Society Dues Tracker.
 * Reuses Flat, Bill, Payment, DuesManager, and FileHandler as-is -
 * this class only adds a visual layer on top of the existing logic.
 */
public class MainGUI extends JFrame {

    private final DuesManager manager = new DuesManager();

    // Flats tab
    private DefaultTableModel flatsModel;
    private JTable flatsTable;

    // Bills tab
    private DefaultTableModel billsModel;
    private JTable billsTable;

    // Report tab
    private DefaultTableModel pendingModel;
    private JTable pendingTable;
    private JLabel collectedLabel;
    private JLabel pendingLabel;

    // Search tab
    private JTextArea searchResultArea;

    public MainGUI() {
        super("Housing Society Dues Tracker");

        FileHandler.ensureDataFolder();
        manager.loadFlats(FileHandler.loadFlats());
        manager.loadBills(FileHandler.loadBills());
        manager.loadPayments(FileHandler.loadPayments());

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAndExit();
            }
        });

        setSize(950, 620);
        setLocationRelativeTo(null);

        setJMenuBar(buildMenuBar());

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Flats", buildFlatsPanel());
        tabs.addTab("Generate Bills", buildBillsPanel());
        tabs.addTab("Record Payment", buildPaymentPanel());
        tabs.addTab("Dues Report", buildReportPanel());
        tabs.addTab("Search Flat", buildSearchPanel());

        add(tabs);
        refreshAll();
    }

    // ---------- Menu bar ----------

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem saveItem = new JMenuItem("Save Now");
        saveItem.addActionListener(e -> {
            persist();
            JOptionPane.showMessageDialog(this, "Data saved to:\n" + new java.io.File("data").getAbsolutePath());
        });

        JMenuItem locationItem = new JMenuItem("Show Data Folder Location");
        locationItem.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Reading/writing data from:\n" + new java.io.File("data").getAbsolutePath()));

        JMenuItem exitItem = new JMenuItem("Save & Exit");
        exitItem.addActionListener(e -> saveAndExit());

        fileMenu.add(saveItem);
        fileMenu.add(locationItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        bar.add(fileMenu);
        return bar;
    }

    // ---------- Flats tab ----------

    private JPanel buildFlatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        flatsModel = new DefaultTableModel(
                new Object[]{"Flat No", "Owner", "Area (sqft)", "Monthly Charge"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        flatsTable = new JTable(flatsModel);
        flatsTable.setRowSorter(new TableRowSorter<>(flatsModel));
        panel.add(new JScrollPane(flatsTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(1, 10, 5, 5));
        JTextField flatNoField = new JTextField();
        JTextField ownerField = new JTextField();
        JTextField areaField = new JTextField();
        JTextField chargeField = new JTextField();
        JCheckBox perSqftCheck = new JCheckBox("Amount above is a rate per sqft, not a fixed total");
        JButton addBtn = new JButton("Add Flat");

        form.add(new JLabel("Flat No:")); form.add(flatNoField);
        form.add(new JLabel("Owner:")); form.add(ownerField);
        form.add(new JLabel("Area (sqft):")); form.add(areaField);
        form.add(new JLabel("Monthly Charge:")); form.add(chargeField);
        form.add(addBtn);
        form.add(perSqftCheck);

        addBtn.addActionListener(e -> {
            String flatNo = flatNoField.getText().trim();
            String owner = ownerField.getText().trim();
            if (flatNo.isEmpty() || owner.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Flat No and Owner are required.", "Missing info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Double area = parseDoubleOrWarn(areaField.getText(), "Area");
            Double enteredAmount = parseDoubleOrWarn(chargeField.getText(), perSqftCheck.isSelected() ? "Rate per Sqft" : "Monthly Charge");
            if (area == null || enteredAmount == null) return;

            double finalCharge = perSqftCheck.isSelected() ? (area * enteredAmount) : enteredAmount;

            boolean added = manager.addFlat(new Flat(flatNo, owner, area, finalCharge));
            if (!added) {
                JOptionPane.showMessageDialog(this, "A flat with this number already exists.", "Duplicate flat", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (perSqftCheck.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        String.format("Monthly charge computed as %.2f sqft x %.2f/sqft = %.2f", area, enteredAmount, finalCharge));
            }
            flatNoField.setText(""); ownerField.setText(""); areaField.setText(""); chargeField.setText("");
            perSqftCheck.setSelected(false);
            refreshAll();
            persist();
        });

        JPanel southContainer = new JPanel(new BorderLayout(5, 5));
        southContainer.add(form, BorderLayout.NORTH);

        JPanel editDeleteRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JButton editBtn = new JButton("Edit Selected Flat");
        JButton deleteBtn = new JButton("Delete Selected Flat");
        editDeleteRow.add(editBtn);
        editDeleteRow.add(deleteBtn);
        southContainer.add(editDeleteRow, BorderLayout.SOUTH);

        editBtn.addActionListener(e -> {
            int row = flatsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a flat in the table first.", "No selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = flatsTable.convertRowIndexToModel(row);
            String flatNo = flatsModel.getValueAt(modelRow, 0).toString();
            String currentOwner = flatsModel.getValueAt(modelRow, 1).toString();
            String currentArea = flatsModel.getValueAt(modelRow, 2).toString();
            String currentCharge = flatsModel.getValueAt(modelRow, 3).toString();

            JTextField ownerEdit = new JTextField(currentOwner);
            JTextField areaEdit = new JTextField(currentArea);
            JTextField chargeEdit = new JTextField(currentCharge);
            JPanel editPanel = new JPanel(new GridLayout(3, 2, 5, 5));
            editPanel.add(new JLabel("Owner:")); editPanel.add(ownerEdit);
            editPanel.add(new JLabel("Area (sqft):")); editPanel.add(areaEdit);
            editPanel.add(new JLabel("Monthly Charge:")); editPanel.add(chargeEdit);

            int result = JOptionPane.showConfirmDialog(this, editPanel, "Edit Flat " + flatNo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result != JOptionPane.OK_OPTION) return;

            Double newArea = parseDoubleOrWarn(areaEdit.getText(), "Area");
            Double newCharge = parseDoubleOrWarn(chargeEdit.getText(), "Monthly Charge");
            if (newArea == null || newCharge == null) return;

            manager.updateFlat(flatNo, ownerEdit.getText().trim(), newArea, newCharge);
            refreshAll();
            persist();
        });

        deleteBtn.addActionListener(e -> {
            int row = flatsTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a flat in the table first.", "No selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int modelRow = flatsTable.convertRowIndexToModel(row);
            String flatNo = flatsModel.getValueAt(modelRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete flat " + flatNo + "? Existing bills and payments for it are kept, but it will\n" +
                            "no longer appear when generating future bills.",
                    "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            manager.removeFlat(flatNo);
            refreshAll();
            persist();
        });

        panel.add(southContainer, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- Generate Bills tab ----------

    private JPanel buildBillsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        billsModel = new DefaultTableModel(
                new Object[]{"Flat No", "Month", "Amount Due", "Amount Paid", "Pending", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        billsTable = new JTable(billsModel);
        billsTable.setRowSorter(new TableRowSorter<>(billsModel));
        panel.add(new JScrollPane(billsTable), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JTextField monthField = new JTextField(10);
        JButton generateBtn = new JButton("Generate Bills for Month");

        form.add(new JLabel("Month (YYYY-MM):"));
        form.add(monthField);
        form.add(generateBtn);

        generateBtn.addActionListener(e -> {
            String month = monthField.getText().trim();
            if (!isValidMonth(month)) {
                JOptionPane.showMessageDialog(this, "Month must be in YYYY-MM format, e.g. 2025-09 (include the year).", "Invalid format", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int flatCount = manager.getAllFlats().size();
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Generate bills for " + flatCount + " flat(s) for " + month + "?\n" +
                            "(Flats that already have a bill for this month are skipped.)",
                    "Confirm bill generation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            int count = manager.generateMonthlyBills(month);
            JOptionPane.showMessageDialog(this, "Generated " + count + " new bill(s) for " + month + ".");
            refreshAll();
            persist();
        });

        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- Record Payment tab ----------

    private JPanel buildPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel paymentsModel = new DefaultTableModel(
                new Object[]{"Flat No", "Month", "Amount", "Date", "Mode"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable paymentsTable = new JTable(paymentsModel);
        paymentsTable.setRowSorter(new TableRowSorter<>(paymentsModel));
        panel.add(new JScrollPane(paymentsTable), BorderLayout.CENTER);
        // Store reference on the table itself via client property so refreshAll can update it
        paymentsTable.putClientProperty("role", "paymentsTable");
        this.paymentsTableRef = paymentsTable;
        this.paymentsModelRef = paymentsModel;

        JPanel form = new JPanel(new GridLayout(1, 11, 5, 5));
        JTextField flatNoField = new JTextField();
        JTextField monthField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField modeField = new JTextField();
        JButton recordBtn = new JButton("Record Payment");

        form.add(new JLabel("Flat No:")); form.add(flatNoField);
        form.add(new JLabel("Month (YYYY-MM):")); form.add(monthField);
        form.add(new JLabel("Amount:")); form.add(amountField);
        form.add(new JLabel("Date (YYYY-MM-DD):")); form.add(dateField);
        form.add(new JLabel("Mode:")); form.add(modeField);
        form.add(recordBtn);

        recordBtn.addActionListener(e -> {
            String flatNo = flatNoField.getText().trim();
            String month = monthField.getText().trim();
            String date = dateField.getText().trim();
            String mode = modeField.getText().trim();
            if (flatNo.isEmpty() || month.isEmpty() || date.isEmpty() || mode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required.", "Missing info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!isValidMonth(month)) {
                JOptionPane.showMessageDialog(this, "Month must be in YYYY-MM format, e.g. 2025-09 (include the year).", "Invalid format", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!isValidDate(date)) {
                JOptionPane.showMessageDialog(this, "Date must be in YYYY-MM-DD format, e.g. 2025-09-14 (include the year).", "Invalid format", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Double amount = parseDoubleOrWarn(amountField.getText(), "Amount");
            if (amount == null) return;

            int confirm = JOptionPane.showConfirmDialog(this,
                    String.format("Record a payment of %.2f for flat %s, month %s?", amount, flatNo, month),
                    "Confirm payment", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = manager.recordPayment(flatNo, month, amount, date, mode);
            if (!ok) {
                JOptionPane.showMessageDialog(this, buildNoBillDiagnostic(flatNo, month), "Not found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            flatNoField.setText(""); monthField.setText(""); amountField.setText("");
            dateField.setText(""); modeField.setText("");
            refreshAll();
            persist();
        });

        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    // held so refreshAll() can update the payments table built above
    private JTable paymentsTableRef;
    private DefaultTableModel paymentsModelRef;

    // ---------- Dues Report tab ----------

    private JPanel buildReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pendingModel = new DefaultTableModel(
                new Object[]{"Flat No", "Month", "Amount Due", "Amount Paid", "Pending"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        pendingTable = new JTable(pendingModel);
        pendingTable.setRowSorter(new TableRowSorter<>(pendingModel));
        panel.add(new JScrollPane(pendingTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        collectedLabel = new JLabel("Total Collected: 0.00");
        pendingLabel = new JLabel("Total Pending: 0.00");
        JButton refreshBtn = new JButton("Refresh");
        JButton exportBtn = new JButton("Export to CSV");
        refreshBtn.addActionListener(e -> refreshAll());
        exportBtn.addActionListener(e -> exportPendingReport());

        bottom.add(collectedLabel);
        bottom.add(pendingLabel);
        bottom.add(refreshBtn);
        bottom.add(exportBtn);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private void exportPendingReport() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("dues_report.csv"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("Flat No,Month,Amount Due,Amount Paid,Pending");
            for (Bill b : manager.getPendingBills()) {
                pw.println(b.getFlatNo() + "," + b.getMonth() + "," + b.getAmount() + "," + b.getAmountPaid() + "," + b.getPendingAmount());
            }
            pw.println();
            pw.println("Total Collected," + manager.getTotalCollected());
            pw.println("Total Pending," + manager.getTotalPending());
            JOptionPane.showMessageDialog(this, "Report exported to:\n" + file.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not write file: " + ex.getMessage(), "Export failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Search tab ----------

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        JTextField flatNoField = new JTextField(12);
        JButton searchBtn = new JButton("Search");
        top.add(new JLabel("Flat No:"));
        top.add(flatNoField);
        top.add(searchBtn);

        searchResultArea = new JTextArea();
        searchResultArea.setEditable(false);
        searchResultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        searchBtn.addActionListener(e -> {
            String flatNo = flatNoField.getText().trim();
            Flat flat = manager.getFlat(flatNo);
            StringBuilder sb = new StringBuilder();
            if (flat == null) {
                sb.append("Flat not found.\n");
            } else {
                sb.append(flat).append("\n\n");
                sb.append("Bills:\n");
                List<Bill> bills = manager.getBillsForFlat(flatNo);
                if (bills.isEmpty()) sb.append("  (no bills yet)\n");
                for (Bill b : bills) sb.append("  ").append(b).append("\n");

                sb.append("\nPayments:\n");
                List<Payment> pays = manager.getPaymentsForFlat(flatNo);
                if (pays.isEmpty()) sb.append("  (no payments yet)\n");
                for (Payment p : pays) sb.append("  ").append(p).append("\n");
            }
            searchResultArea.setText(sb.toString());
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(searchResultArea), BorderLayout.CENTER);
        return panel;
    }

    // ---------- Refresh logic ----------

    private void refreshAll() {
        refreshFlatsTable();
        refreshBillsTable();
        refreshPaymentsTable();
        refreshReportTable();
    }

    private void refreshFlatsTable() {
        flatsModel.setRowCount(0);
        Collection<Flat> flats = manager.getAllFlats();
        for (Flat f : flats) {
            flatsModel.addRow(new Object[]{f.getFlatNo(), f.getOwnerName(), f.getAreaSqft(), f.getMonthlyCharge()});
        }
    }

    private void refreshBillsTable() {
        billsModel.setRowCount(0);
        for (Bill b : manager.getAllBills()) {
            String status = b.isFullyPaid() ? "PAID" : "PENDING";
            billsModel.addRow(new Object[]{
                    b.getFlatNo(), b.getMonth(), b.getAmount(), b.getAmountPaid(), b.getPendingAmount(), status
            });
        }
    }

    private void refreshPaymentsTable() {
        if (paymentsModelRef == null) return;
        paymentsModelRef.setRowCount(0);
        for (Payment p : manager.getAllPayments()) {
            paymentsModelRef.addRow(new Object[]{
                    p.getFlatNo(), p.getMonth(), p.getAmount(), p.getPaymentDate(), p.getMode()
            });
        }
    }

    private void refreshReportTable() {
        pendingModel.setRowCount(0);
        for (Bill b : manager.getPendingBills()) {
            pendingModel.addRow(new Object[]{
                    b.getFlatNo(), b.getMonth(), b.getAmount(), b.getAmountPaid(), b.getPendingAmount()
            });
        }
        collectedLabel.setText(String.format("Total Collected: %.2f", manager.getTotalCollected()));
        pendingLabel.setText(String.format("Total Pending: %.2f", manager.getTotalPending()));
    }

    private String buildNoBillDiagnostic(String typedFlatNo, String typedMonth) {
        StringBuilder sb = new StringBuilder("No matching bill found.\n\n");
        sb.append("You typed:\n");
        sb.append("  Flat No: [").append(typedFlatNo).append("]  (length ").append(typedFlatNo.length()).append(")\n");
        sb.append("  Month:   [").append(typedMonth).append("]  (length ").append(typedMonth.length()).append(")\n\n");

        List<Bill> billsForFlat = manager.getBillsForFlat(typedFlatNo);
        if (billsForFlat.isEmpty()) {
            sb.append("No bills exist for this flat number at all.\n");
            sb.append("Existing flat numbers in the system:\n");
            for (Flat f : manager.getAllFlats()) {
                sb.append("  [").append(f.getFlatNo()).append("]\n");
            }
        } else {
            sb.append("Bills DO exist for this flat, for these months:\n");
            for (Bill b : billsForFlat) {
                sb.append("  [").append(b.getMonth()).append("]  (length ").append(b.getMonth().length()).append(")\n");
            }
            sb.append("\nCompare the bracketed text above to what you typed - look for\n");
            sb.append("extra spaces or different characters that look the same.");
        }
        return sb.toString();
    }

    // ---------- Helpers ----------

    private boolean isValidMonth(String month) {
        return month.matches("\\d{4}-\\d{2}");
    }

    private boolean isValidDate(String date) {
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private Double parseDoubleOrWarn(String text, String fieldName) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, fieldName + " must be a valid number.", "Invalid input", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void persist() {
        FileHandler.saveFlats(manager.getAllFlats());
        FileHandler.saveBills(manager.getAllBills());
        FileHandler.savePayments(manager.getAllPayments());
    }

    private void saveAndExit() {
        persist();
        dispose();
        System.exit(0);
    }

    // ---------- Entry point ----------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
