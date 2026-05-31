package app.ui;

import app.dao.ComplaintDAO;
import app.dao.ComplaintResponseDAO;
import app.model.Complaint;
import app.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final User student;
    private final LoginFrame loginFrame;
    private final ComplaintDAO complaintDAO = new ComplaintDAO();
    private final ComplaintResponseDAO responseDAO = new ComplaintResponseDAO();
    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Category", "Status", "Submitted"}, 0);
    private final JTable complaintTable = new JTable(tableModel);

    public StudentDashboard(User student, LoginFrame loginFrame) {
        this.student = student;
        this.loginFrame = loginFrame;

        setTitle("Student Dashboard - " + student.getFullName());
        setMinimumSize(new Dimension(720, 480));
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Submit Complaint", new SubmitComplaintForm(student, complaintDAO));
        tabbedPane.addTab("My Complaints", createMyComplaintsPanel());

        setLayout(new BorderLayout());
        add(createTopBar(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        loadComplaints();
    }

    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(new JLabel("Welcome, " + student.getFullName()), BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        panel.add(logoutButton, BorderLayout.EAST);
        return panel;
    }

    private JPanel createMyComplaintsPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        complaintTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(complaintTable), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadComplaints());

        JButton detailsButton = new JButton("View Details");
        detailsButton.addActionListener(e -> openDetails());

        actions.add(refreshButton);
        actions.add(detailsButton);
        panel.add(actions, BorderLayout.SOUTH);

        return panel;
    }

    private void loadComplaints() {
        tableModel.setRowCount(0);
        List<Complaint> complaints = complaintDAO.getByStudentId(student.getId());
        for (Complaint complaint : complaints) {
            tableModel.addRow(new Object[]{
                    complaint.getId(),
                    complaint.getTitle(),
                    complaint.getCategory(),
                    complaint.getStatus(),
                    complaint.getSubmittedAt()
            });
        }
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

        new ComplaintDetailsFrame(student, complaint, false, complaintDAO, responseDAO, this::loadComplaints).setVisible(true);
    }
}
