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
import java.util.List;

public class AdminDashboard extends JFrame {

    private final User admin;
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final ComplaintResponseDAO responseDAO = new ComplaintResponseDAO();

    private final JLabel totalLabel = new JLabel("0");
    private final JLabel pendingLabel = new JLabel("0");
    private final JLabel progressLabel = new JLabel("0");
    private final JLabel resolvedLabel = new JLabel("0");
    private final JLabel rejectedLabel = new JLabel("0");

    private final JTextField searchField = new JTextField(15);
    private final JComboBox<String> statusFilter = new JComboBox<>(createStatusFilterOptions());
    private final JTextField fromDateField = new JTextField(8);
    private final JTextField toDateField = new JTextField(8);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Student Requester", "Title Topic", "Category Group", "Status Tag", "Date Tracked"}, 0);
    private final JTable complaintTable = new JTable(tableModel);
    private final LoginFrame loginFrame;

    public AdminDashboard(User admin, LoginFrame loginFrame) {
        this.admin = admin;
        this.loginFrame = loginFrame;

        setTitle("Administration Hub Control Center");
        setMinimumSize(new Dimension(1100, 680));
        setSize(1250, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        setLayout(new BorderLayout(0, 16));
        getContentPane().setBackground(UiTheme.APP_BACKGROUND);

        add(createTopBar(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);

        refreshStats();
        loadComplaints();
    }

    private JPanel createTopBar() {
        JPanel wrapper = new JPanel(new BorderLayout(24, 0));
        wrapper.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        wrapper.setBackground(UiTheme.CARD_BACKGROUND);
        wrapper.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UiTheme.BORDER_COLOR));

        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 14, 0));
        statsPanel.setBackground(UiTheme.CARD_BACKGROUND);

        statsPanel.add(createKpiCard("Total Tickets", totalLabel, UiTheme.PRIMARY));
        statsPanel.add(createKpiCard("Pending Attention", pendingLabel, new Color(245, 158, 11))); // Amber
        statsPanel.add(createKpiCard("In-Progress", progressLabel, new Color(59, 130, 246)));      // Blue
        statsPanel.add(createKpiCard("Resolved Done", resolvedLabel, new Color(16, 185, 129)));    // Emerald
        statsPanel.add(createKpiCard("Rejected Case", rejectedLabel, new Color(239, 68, 68)));     // Red

        JPanel profileArea = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 8));
        profileArea.setBackground(UiTheme.CARD_BACKGROUND);

        JLabel identityLabel = new JLabel(admin.getFullName());
        identityLabel.setFont(UiTheme.HEADER_FONT);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        logoutButton.setPreferredSize(new Dimension(95, 36));
        styleRedButton(logoutButton);

        profileArea.add(identityLabel);
        profileArea.add(logoutButton);

        wrapper.add(statsPanel, BorderLayout.CENTER);
        wrapper.add(profileArea, BorderLayout.EAST);
        return wrapper;
    }

    private JPanel createKpiCard(String title, JLabel metricLbl, Color indicatorColor) {
        JPanel card = new JPanel(new BorderLayout(0, 4));
        card.setBackground(UiTheme.APP_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(8, UiTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(UiTheme.MUTED);

        metricLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        metricLbl.setForeground(indicatorColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(metricLbl, BorderLayout.CENTER);
        return card;
    }

    private JPanel createCenterPanel() {
        JPanel centerWrapper = new JPanel(new BorderLayout(0, 16));
        centerWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        centerWrapper.setBackground(UiTheme.APP_BACKGROUND);

        JPanel tableContainerCard = new JPanel(new BorderLayout(0, 16));
        tableContainerCard.setBackground(UiTheme.CARD_BACKGROUND);
        tableContainerCard.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(12, UiTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Filter Controls Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        filterPanel.setBackground(UiTheme.CARD_BACKGROUND);

        filterPanel.add(new JLabel("Search Query:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Status State:"));
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("Date From:"));
        filterPanel.add(fromDateField);
        filterPanel.add(new JLabel("To:"));
        filterPanel.add(toDateField);

        setupInputFieldStyle(searchField);
        setupInputFieldStyle(statusFilter);
        setupInputFieldStyle(fromDateField);
        setupInputFieldStyle(toDateField);

        JButton applyFilterButton = new JButton("Filter Map");
        JButton clearFilterButton = new JButton("Reset");
        UiTheme.stylePrimaryButton(applyFilterButton);
        UiTheme.styleMutedButton(clearFilterButton);
        applyFilterButton.setPreferredSize(new Dimension(110, 36));
        clearFilterButton.setPreferredSize(new Dimension(90, 36));

        applyFilterButton.addActionListener(e -> loadComplaints());
        clearFilterButton.addActionListener(e -> clearFilters());

        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);

        // Data Table Configuration
        UiTheme.styleTable(complaintTable);
        complaintTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(complaintTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER_COLOR));

        // Bottom Action Controls
        JPanel executionTray = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        executionTray.setBackground(UiTheme.CARD_BACKGROUND);

        JButton refreshButton = new JButton("Sync Records");
        JButton openDetailsButton = new JButton("Manage Incident");
        UiTheme.styleSecondaryButton(refreshButton);
        UiTheme.stylePrimaryButton(openDetailsButton);
        refreshButton.setPreferredSize(new Dimension(140, 40));
        openDetailsButton.setPreferredSize(new Dimension(160, 40));

        refreshButton.addActionListener(e -> { refreshStats(); loadComplaints(); });
        openDetailsButton.addActionListener(e -> openDetails());

        executionTray.add(refreshButton);
        executionTray.add(openDetailsButton);

        tableContainerCard.add(filterPanel, BorderLayout.NORTH);
        tableContainerCard.add(scrollPane, BorderLayout.CENTER);
        tableContainerCard.add(executionTray, BorderLayout.SOUTH);

        centerWrapper.add(tableContainerCard, BorderLayout.CENTER);
        return centerWrapper;
    }

    private void setupInputFieldStyle(JComponent field) {
        field.setFont(UiTheme.FIELD_FONT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(6, UiTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private void styleRedButton(JButton btn) {
        btn.setFont(UiTheme.BUTTON_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(239, 68, 68));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(220, 38, 38)); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(new Color(239, 68, 68)); }
        });
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Terminate active administrator dashboard session?", "Confirm Signout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            setVisible(false);
            loginFrame.clearFields();
            loginFrame.setVisible(true);
        }
    }

    private static String[] createStatusFilterOptions() {
        ComplaintStatus[] statuses = ComplaintStatus.values();
        String[] options = new String[statuses.length + 1];
        options[0] = "ALL";
        for (int i = 0; i < statuses.length; i++) {
            options[i + 1] = statuses[i].name();
        }
        return options;
    }

    private void clearFilters() {
        searchField.setText("");
        statusFilter.setSelectedIndex(0);
        fromDateField.setText("");
        toDateField.setText("");
        loadComplaints();
    }

    private void refreshStats() {
        totalLabel.setText(String.valueOf(complaintDAO.countAll()));
        pendingLabel.setText(String.valueOf(complaintDAO.countByStatus(ComplaintStatus.PENDING)));
        progressLabel.setText(String.valueOf(complaintDAO.countByStatus(ComplaintStatus.IN_PROGRESS)));
        resolvedLabel.setText(String.valueOf(complaintDAO.countByStatus(ComplaintStatus.RESOLVED)));
        rejectedLabel.setText(String.valueOf(complaintDAO.countByStatus(ComplaintStatus.REJECTED)));
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

        List<Complaint> complaints = complaintDAO.getAll(searchField.getText().trim(), status, from, to);
        for (Complaint c : complaints) {
            tableModel.addRow(new Object[]{c.getId(), c.getStudentName(), c.getTitle(), c.getCategory(), c.getStatus(), c.getSubmittedAt()});
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try { return LocalDate.parse(value); } catch (Exception e) { return null; }
    }

    private void openDetails() {
        int row = complaintTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please mark a complaint item row to open map views."); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        Complaint complaint = complaintDAO.getById(id);
        if (complaint != null) {
            new ComplaintDetailsFrame(admin, complaint, true, complaintDAO, responseDAO, () -> { refreshStats(); loadComplaints(); }).setVisible(true);
        }
    }
}