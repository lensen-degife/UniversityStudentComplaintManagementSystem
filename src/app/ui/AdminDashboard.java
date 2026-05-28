package app.ui;

import app.dao.ComplaintDAO;
import app.dao.ComplaintResponseDAO;
import app.model.Complaint;
import app.model.ComplaintStatus;
import app.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final User admin;
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final ComplaintResponseDAO responseDAO = new ComplaintResponseDAO();

    private final JLabel totalLabel = new JLabel("Total: 0");
    private final JLabel pendingLabel = new JLabel("Pending: 0");
    private final JLabel progressLabel = new JLabel("In Progress: 0");
    private final JLabel resolvedLabel = new JLabel("Resolved: 0");
    private final JLabel rejectedLabel = new JLabel("Rejected: 0");

    private final JTextField searchField = new JTextField(20);
    private final JComboBox<String> statusFilter = new JComboBox<>(new String[]{"ALL", "PENDING", "IN_PROGRESS", "RESOLVED", "REJECTED"});
    private final JTextField fromDateField = new JTextField(10);
    private final JTextField toDateField = new JTextField(10);

    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Student", "Title", "Category", "Status", "Submitted"}, 0);
    private final JTable complaintTable = new JTable(tableModel);

    public AdminDashboard(User admin) {
        this.admin = admin;

        setTitle("Admin Dashboard - " + admin.getFullName());
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        add(createTopBar(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);

        refreshStats();
        loadComplaints();
    }

    private JPanel createTopBar() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel stats = new JPanel(new GridLayout(1, 5, 8, 8));
        stats.add(totalLabel);
        stats.add(pendingLabel);
        stats.add(progressLabel);
        stats.add(resolvedLabel);
        stats.add(rejectedLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.add(new JLabel("Admin: " + admin.getFullName()));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        right.add(logoutButton);

        wrapper.add(stats, BorderLayout.CENTER);
        wrapper.add(right, BorderLayout.EAST);
        return wrapper;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("From (YYYY-MM-DD):"));
        filterPanel.add(fromDateField);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(toDateField);

        JButton applyFilterButton = new JButton("Apply");
        applyFilterButton.addActionListener(e -> loadComplaints());
        JButton clearFilterButton = new JButton("Clear");
        clearFilterButton.addActionListener(e -> {
            searchField.setText("");
            statusFilter.setSelectedItem("ALL");
            fromDateField.setText("");
            toDateField.setText("");
            loadComplaints();
        });

        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);

        complaintTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> {
            refreshStats();
            loadComplaints();
        });

        JButton openDetailsButton = new JButton("Open Details");
        openDetailsButton.addActionListener(e -> openDetails());

        actions.add(refreshButton);
        actions.add(openDetailsButton);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(complaintTable), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshStats() {
        totalLabel.setText("Total: " + complaintDAO.countAll());
        pendingLabel.setText("Pending: " + complaintDAO.countByStatus(ComplaintStatus.PENDING));
        progressLabel.setText("In Progress: " + complaintDAO.countByStatus(ComplaintStatus.IN_PROGRESS));
        resolvedLabel.setText("Resolved: " + complaintDAO.countByStatus(ComplaintStatus.RESOLVED));
        rejectedLabel.setText("Rejected: " + complaintDAO.countByStatus(ComplaintStatus.REJECTED));
    }

    private void loadComplaints() {
        tableModel.setRowCount(0);

        ComplaintStatus status = null;
        String selectedStatus = (String) statusFilter.getSelectedItem();
        if (selectedStatus != null && !"ALL".equals(selectedStatus)) {
            status = ComplaintStatus.valueOf(selectedStatus);
        }

        LocalDate fromDate = parseDate(fromDateField.getText().trim());
        LocalDate toDate = parseDate(toDateField.getText().trim());
        if ((fromDateField.getText().isBlank() || fromDate != null) && (toDateField.getText().isBlank() || toDate != null)) {
            List<Complaint> complaints = complaintDAO.getAll(searchField.getText().trim(), status, fromDate, toDate);
            for (Complaint complaint : complaints) {
                tableModel.addRow(new Object[]{
                        complaint.getId(),
                        complaint.getStudentName(),
                        complaint.getTitle(),
                        complaint.getCategory(),
                        complaint.getStatus(),
                        complaint.getSubmittedAt()
                });
            }
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException exception) {
            JOptionPane.showMessageDialog(this, "Invalid date format: " + value + ". Use YYYY-MM-DD.", "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void openDetails() {
        int selectedRow = complaintTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a complaint.");
            return;
        }
        int complaintId = (int) tableModel.getValueAt(selectedRow, 0);
        Complaint complaint = complaintDAO.getById(complaintId);
        if (complaint == null) {
            JOptionPane.showMessageDialog(this, "Complaint not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new ComplaintDetailsFrame(admin, complaint, true, complaintDAO, responseDAO, () -> {
            refreshStats();
            loadComplaints();
        }).setVisible(true);
    }
}
