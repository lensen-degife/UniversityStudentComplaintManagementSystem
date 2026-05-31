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
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(UiTheme.BACKGROUND);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UiTheme.BACKGROUND);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 1;

        gbc.gridy = 0;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.weightx = 0;
        formPanel.add(createLabel("Title:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        formPanel.add(createLabel("Category:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(categoryCombo, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(createLabel("Attachment Path (optional):"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(attachmentField, gbc);

        JButton submitButton = new JButton("Submit Complaint");
        UiTheme.stylePrimaryButton(submitButton);
        submitButton.addActionListener(e -> submit());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(UiTheme.BACKGROUND);
        buttonPanel.add(submitButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UiTheme.LABEL_FONT);
        label.setForeground(UiTheme.TEXT);
        return label;
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
