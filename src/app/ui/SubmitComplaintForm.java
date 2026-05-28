package app.ui;

import app.dao.ComplaintDAO;
import app.model.Complaint;
import app.model.ComplaintCategory;
import app.model.User;

import javax.swing.*;
import java.awt.*;

public class SubmitComplaintForm extends JPanel {

    private final User student;
    private final ComplaintDAO complaintDAO;

    private final JTextField titleField = new JTextField();
    private final JComboBox<ComplaintCategory> categoryCombo = new JComboBox<>(ComplaintCategory.values());
    private final JTextArea descriptionArea = new JTextArea(6, 40);
    private final JTextField attachmentField = new JTextField();

    public SubmitComplaintForm(User student, ComplaintDAO complaintDAO) {
        this.student = student;
        this.complaintDAO = complaintDAO;

        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        formPanel.add(descriptionScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(new JLabel("Attachment Path (optional):"), gbc);
        gbc.gridx = 1;
        formPanel.add(attachmentField, gbc);

        JButton submitButton = new JButton("Submit Complaint");
        submitButton.addActionListener(e -> submit());

        add(formPanel, BorderLayout.CENTER);
        add(submitButton, BorderLayout.SOUTH);
    }

    private void submit() {
        String title = titleField.getText().trim();
        ComplaintCategory category = (ComplaintCategory) categoryCombo.getSelectedItem();
        String description = descriptionArea.getText().trim();
        String attachmentPath = attachmentField.getText().trim();

        if (title.isBlank() || description.isBlank() || category == null) {
            JOptionPane.showMessageDialog(this, "Title, category and description are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Complaint complaint = new Complaint(student.getId(), title, category, description, attachmentPath.isBlank() ? null : attachmentPath);
        boolean success = complaintDAO.addComplaint(complaint);

        if (success) {
            JOptionPane.showMessageDialog(this, "Complaint submitted successfully.");
            titleField.setText("");
            descriptionArea.setText("");
            attachmentField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to submit complaint.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
