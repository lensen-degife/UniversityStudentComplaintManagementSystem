package app.ui;

import app.dao.ComplaintDAO;
import app.dao.ComplaintResponseDAO;
import app.model.Complaint;
import app.model.ComplaintResponse;
import app.model.ComplaintStatus;
import app.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ComplaintDetailsFrame extends JFrame {

    private final User currentUser;
    private Complaint complaint;
    private final boolean adminView;
    private final ComplaintDAO complaintDAO;
    private final ComplaintResponseDAO responseDAO;
    private final Runnable onUpdated;

    private final JLabel statusLabel = new JLabel();
    private final JLabel categoryLabel = new JLabel();
    private final JLabel studentLabel = new JLabel();
    private final JTextArea descriptionArea = new JTextArea();

    private final DefaultTableModel responseModel = new DefaultTableModel(new Object[]{"Responder", "Message", "Time"}, 0);
    private final JTable responseTable = new JTable(responseModel);

    public ComplaintDetailsFrame(User currentUser, Complaint complaint, boolean adminView,
                                 ComplaintDAO complaintDAO, ComplaintResponseDAO responseDAO, Runnable onUpdated) {
        this.currentUser = currentUser;
        this.complaint = complaint;
        this.adminView = adminView;
        this.complaintDAO = complaintDAO;
        this.responseDAO = responseDAO;
        this.onUpdated = onUpdated;

        setTitle("Complaint Details #" + complaint.getId());
        setSize(850, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        setLayout(new BorderLayout(8, 8));
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);

        loadComplaintDetails();
        loadResponses();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(studentLabel);
        panel.add(categoryLabel);
        panel.add(new JLabel("Title: " + complaint.getTitle()));
        panel.add(statusLabel);
        return panel;
    }

    private JPanel createBodyPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(new JLabel("Description"), BorderLayout.NORTH);
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        JPanel responsePanel = new JPanel(new BorderLayout());
        responsePanel.add(new JLabel("Responses"), BorderLayout.NORTH);
        responsePanel.add(new JScrollPane(responseTable), BorderLayout.CENTER);

        panel.add(descriptionPanel);
        panel.add(responsePanel);
        return panel;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JTextField responseField = new JTextField(30);
        panel.add(responseField);

        JButton addResponseButton = new JButton("Add Response");
        addResponseButton.addActionListener(e -> {
            String message = responseField.getText().trim();
            if (message.isBlank()) {
                JOptionPane.showMessageDialog(this, "Response message is required.");
                return;
            }

            ComplaintResponse response = new ComplaintResponse(complaint.getId(), currentUser.getId(), message);
            boolean added = responseDAO.addResponse(response);
            if (added) {
                responseField.setText("");
                loadResponses();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add response.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(addResponseButton);

        if (adminView) {
            JComboBox<ComplaintStatus> statusCombo = new JComboBox<>(ComplaintStatus.values());
            statusCombo.setSelectedItem(complaint.getStatus());
            panel.add(statusCombo);

            JButton updateStatusButton = new JButton("Update Status");
            updateStatusButton.addActionListener(e -> {
                ComplaintStatus status = (ComplaintStatus) statusCombo.getSelectedItem();
                if (status == null) {
                    return;
                }
                boolean updated = complaintDAO.updateStatus(complaint.getId(), status);
                if (updated) {
                    complaint = complaintDAO.getById(complaint.getId());
                    loadComplaintDetails();
                    onUpdated.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update status.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            panel.add(updateStatusButton);
        }

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        panel.add(closeButton);

        return panel;
    }

    private void loadComplaintDetails() {
        studentLabel.setText("Student: " + complaint.getStudentName());
        categoryLabel.setText("Category: " + complaint.getCategory());
        statusLabel.setText("Status: " + complaint.getStatus());
        descriptionArea.setText(complaint.getDescription() + (complaint.getAttachmentPath() == null ? "" : "\n\nAttachment: " + complaint.getAttachmentPath()));
    }

    private void loadResponses() {
        responseModel.setRowCount(0);
        List<ComplaintResponse> responses = responseDAO.getByComplaintId(complaint.getId());
        for (ComplaintResponse response : responses) {
            responseModel.addRow(new Object[]{response.getResponderName(), response.getMessage(), response.getCreatedAt()});
        }
    }
}
