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

        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        setBackground(UiTheme.APP_BACKGROUND);

        JPanel cardBody = new JPanel(new GridBagLayout());
        cardBody.setBackground(UiTheme.CARD_BACKGROUND);
        cardBody.setBorder(BorderFactory.createCompoundBorder(
                new UiTheme.RoundedBorder(12, UiTheme.BORDER_COLOR),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        gbc.gridy = 0;
        cardBody.add(UiTheme.createFormRow("Complaint Title", titleField), gbc);

        gbc.gridy = 1;
        cardBody.add(UiTheme.createFormRow("Category Classification", categoryCombo), gbc);

        gbc.gridy = 2;
        descriptionArea.setFont(UiTheme.FIELD_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(0, 140));
        descScroll.setBorder(new UiTheme.RoundedBorder(8, UiTheme.BORDER_COLOR));

        JPanel descWrapper = new JPanel(new BorderLayout(0, 6));
        descWrapper.setBackground(UiTheme.CARD_BACKGROUND);
        JLabel descLabel = new JLabel("Detailed Description");
        descLabel.setFont(UiTheme.LABEL_FONT);
        descWrapper.add(descLabel, BorderLayout.NORTH);
        descWrapper.add(descScroll, BorderLayout.CENTER);
        cardBody.add(descWrapper, gbc);

        gbc.gridy = 3;
        cardBody.add(UiTheme.createFormRow("Optional Document Attachment Path", attachmentField), gbc);

        JButton submitButton = new JButton("File Complaint");
        UiTheme.stylePrimaryButton(submitButton);
        submitButton.addActionListener(e -> submit());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(UiTheme.APP_BACKGROUND);
        buttonPanel.add(submitButton);

        add(cardBody, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void submit() {
        String title = titleField.getText().trim();
        ComplaintCategory category = (ComplaintCategory) categoryCombo.getSelectedItem();
        String description = descriptionArea.getText().trim();
        String attachmentPath = attachmentField.getText().trim();

        if (title.isBlank() || description.isBlank() || category == null) {
            JOptionPane.showMessageDialog(this, "Please enter a title, category, and description.", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Complaint complaint = new Complaint(student.getId(), title, category, description, attachmentPath.isBlank() ? null : attachmentPath);
        boolean success = complaintDAO.addComplaint(complaint);

        if (success) {
            JOptionPane.showMessageDialog(this, "Your complaint was filed successfully.");
            titleField.setText("");
            descriptionArea.setText("");
            attachmentField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to file complaint due to a connection issue.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}