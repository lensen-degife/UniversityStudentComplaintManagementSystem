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

        setTitle("Student Portal - Workspace");
        setMinimumSize(new Dimension(850, 600));
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        getContentPane().setBackground(UiTheme.APP_BACKGROUND);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Submit New Complaint", new SubmitComplaintForm(student, complaintDAO));
        tabbedPane.addTab("View History Log", createMyComplaintsPanel());

        setLayout(new BorderLayout(0, 12));
        add(createTopBar(), BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        loadComplaints();
    }

    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UiTheme.PRIMARY);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel welcomeLabel = new JLabel("Active Session: " + student.getFullName());
        welcomeLabel.setFont(UiTheme.HEADER_FONT);
        welcomeLabel.setForeground(Color.WHITE);
        panel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Sign Out");
        UiTheme.styleButton(logoutButton, new Color(239, 68, 68), new Color(220, 38, 38));
        logoutButton.setPreferredSize(new Dimension(110, 36));
        logoutButton.addActionListener(e -> logout());
        panel.add(logoutButton, BorderLayout.EAST);
        return panel;
    }

    private static void styleButton(JButton btn, Color base, Color hvr) {
        btn.setFont(UiTheme.BUTTON_FONT);
        btn.setForeground(Color.WHITE);
        btn.setBackground(base);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(hvr); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(base); }
        });
    }

    private JPanel createMyComplaintsPanel() {
        JPanel outerPanel = new JPanel(new BorderLayout(0, 16));
        outerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        outerPanel.setBackground(UiTheme.APP_BACKGROUND);

        JPanel container = new JPanel(new BorderLayout(0, 16));
        container.setBackground(UiTheme.CARD_BACKGROUND);
        container.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(12, UiTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        UiTheme.styleTable(complaintTable);
        complaintTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(complaintTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UiTheme.BORDER_COLOR));
        container.add(scrollPane, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        actions.setBackground(UiTheme.CARD_BACKGROUND);

        JButton refreshButton = new JButton("Refresh Status");
        JButton detailsButton = new JButton("Inspect Record");

        UiTheme.styleMutedButton(refreshButton);
        UiTheme.stylePrimaryButton(detailsButton);

        refreshButton.addActionListener(e -> loadComplaints());
        detailsButton.addActionListener(e -> openDetails());

        actions.add(refreshButton);
        actions.add(detailsButton);
        container.add(actions, BorderLayout.SOUTH);

        outerPanel.add(container, BorderLayout.CENTER);
        return outerPanel;
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
                "Are you sure you want to log out?", "Confirm Session Termination",
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
            JOptionPane.showMessageDialog(this, "Please choose an entry row from the table map first.", "Selection Needed", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int complaintId = (int) tableModel.getValueAt(selectedRow, 0);
        Complaint complaint = complaintDAO.getById(complaintId);
        if (complaint == null) {
            JOptionPane.showMessageDialog(this, "The requested tracking item could not be pulled.", "Data Fetch Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new ComplaintDetailsFrame(student, complaint, false, complaintDAO, responseDAO, this::loadComplaints).setVisible(true);
    }
}