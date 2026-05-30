package app.ui;

import app.dao.ComplaintDAO;
import app.dao.ComplaintResponseDAO;
import app.model.Complaint;
import app.model.ComplaintStatus;
import app.model.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Student", "Title", "Category", "Status", "Submitted"}, 0);
    private final JTable complaintTable = new JTable(tableModel);

    private final LoginFrame loginFrame;

    public AdminDashboard(User admin, LoginFrame loginFrame) {
        this.admin = admin;
        this.loginFrame = loginFrame;

        setTitle("Admin Dashboard - " + admin.getFullName());
        setSize(1150, 700);                    // Slightly larger
        setLocationRelativeTo(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 252));

        add(createTopBar(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);

        refreshStats();
        loadComplaints();
    }

    private JPanel createTopBar() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        wrapper.setBackground(Color.WHITE);

        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 12, 8));
        statsPanel.setBackground(Color.WHITE);

        styleStatLabel(totalLabel, new Color(0, 51, 102));
        styleStatLabel(pendingLabel, new Color(255, 140, 0));
        styleStatLabel(progressLabel, new Color(0, 123, 255));
        styleStatLabel(resolvedLabel, new Color(40, 167, 69));
        styleStatLabel(rejectedLabel, new Color(220, 53, 69));

        statsPanel.add(totalLabel);
        statsPanel.add(pendingLabel);
        statsPanel.add(progressLabel);
        statsPanel.add(resolvedLabel);
        statsPanel.add(rejectedLabel);

        // Right side
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(Color.WHITE);

        JLabel adminLabel = new JLabel("Admin: " + admin.getFullName());
        adminLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        styleLogoutButton(logoutButton);

        rightPanel.add(adminLabel);
        rightPanel.add(logoutButton);

        wrapper.add(statsPanel, BorderLayout.CENTER);
        wrapper.add(rightPanel, BorderLayout.EAST);
        return wrapper;
    }

    private void styleStatLabel(JLabel label, Color color) {
        label.setFont(new Font("Arial", Font.BOLD, 18));     // Increased font
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(12, 10, 12, 10)));
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        panel.setBackground(new Color(245, 247, 252));

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("From:"));
        filterPanel.add(fromDateField);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(toDateField);

        JButton applyFilterButton = new JButton("Apply Filter");
        JButton clearFilterButton = new JButton("Clear");

        styleButton(applyFilterButton, new Color(0, 123, 255));
        styleButton(clearFilterButton, new Color(108, 117, 125));

        applyFilterButton.addActionListener(e -> loadComplaints());
        clearFilterButton.addActionListener(e -> clearFilters());

        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);

        // Table Styling
        complaintTable.setRowHeight(38);                    // Bigger rows
        complaintTable.setFont(new Font("Arial", Font.PLAIN, 15));
        complaintTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = complaintTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        // Actions Panel
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        actions.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh");
        JButton openDetailsButton = new JButton("Open Details");

        styleButton(refreshButton, new Color(40, 167, 69));
        styleButton(openDetailsButton, new Color(0, 102, 204));

        refreshButton.addActionListener(e -> {
            refreshStats();
            loadComplaints();
        });
        openDetailsButton.addActionListener(e -> openDetails());

        actions.add(refreshButton);
        actions.add(openDetailsButton);

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(complaintTable), BorderLayout.CENTER);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private void styleButton(JButton button, Color bgColor) {
        button.setPreferredSize(new Dimension(130, 42));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleLogoutButton(JButton button) {
        button.setBackground(new Color(220, 53, 69));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 42));
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            setVisible(false);
            loginFrame.clearFields();
            loginFrame.setVisible(true);
        }
    }

    private void clearFilters() {
        searchField.setText("");
        statusFilter.setSelectedItem("ALL");
        fromDateField.setText("");
        toDateField.setText("");
        loadComplaints();
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
        String selected = (String) statusFilter.getSelectedItem();
        if (selected != null && !"ALL".equals(selected)) {
            status = ComplaintStatus.valueOf(selected);
        }

        LocalDate from = parseDate(fromDateField.getText().trim());
        LocalDate to = parseDate(toDateField.getText().trim());

        List<Complaint> complaints = complaintDAO.getAll(
                searchField.getText().trim(), status, from, to);

        for (Complaint c : complaints) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getStudentName(),
                    c.getTitle(),
                    c.getCategory(),
                    c.getStatus(),
                    c.getSubmittedAt()
            });
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private void openDetails() {
        int row = complaintTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a complaint first.");
            return;
        }

        int id = (int) tableModel.getValueAt(row, 0);
        Complaint complaint = complaintDAO.getById(id);

        if (complaint != null) {
            new ComplaintDetailsFrame(admin, complaint, true, complaintDAO, responseDAO, () -> {
                refreshStats();
                loadComplaints();
            }).setVisible(true);
        }
    }
}