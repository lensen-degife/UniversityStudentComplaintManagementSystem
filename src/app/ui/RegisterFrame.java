package app.ui;

import app.service.AuthService;
import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final JTextField fullNameField = new JTextField(25);
    private final JTextField emailField = new JTextField(25);
    private final JTextField departmentField = new JTextField(25);
    private final JPasswordField passwordField = new JPasswordField(25);
    private final JPasswordField confirmPasswordField = new JPasswordField(25);

    private final JCheckBox showPasswordCheck = new JCheckBox("Show Password");

    private final AuthService authService = new AuthService();

    // Reference to LoginFrame for smooth switching
    private final LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;

        setTitle("Student Registration");
        setSize(670, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);   // Changed to HIDE_ON_CLOSE

        // Main panel with BoxLayout (vertical)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        // Add fields
        addField(mainPanel, "Full Name:", fullNameField);
        addField(mainPanel, "Email:", emailField);
        addField(mainPanel, "Department:", departmentField);
        addPasswordFieldWithToggle(mainPanel);
        addField(mainPanel, "Confirm Password:", confirmPasswordField);

        // Button Panel
        JButton backButton = new JButton("Back to Login");
        JButton registerButton = new JButton("Register");

        backButton.addActionListener(e -> switchToLogin());
        registerButton.addActionListener(e -> register());

        // Style buttons
        styleButton(registerButton, new Color(0, 123, 255), Color.WHITE); // Blue
        styleButton(backButton, new Color(108, 117, 125), Color.WHITE);   // Gray

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);

        mainPanel.add(Box.createVerticalStrut(35));
        mainPanel.add(buttonPanel);

        setContentPane(mainPanel);
    }

    private void switchToLogin() {
        clearFields();           // Clear the form
        setVisible(false);       // Hide this frame
        loginFrame.setVisible(true);  // Show Login frame
    }

    private void clearFields() {
        fullNameField.setText("");
        emailField.setText("");
        departmentField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        showPasswordCheck.setSelected(false);
        passwordField.setEchoChar('•');
        confirmPasswordField.setEchoChar('•');
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.setMaximumSize(new Dimension(480, 50));
        field.setPreferredSize(new Dimension(480, 50));
        field.setFont(new Font("Arial", Font.PLAIN, 15));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(12));
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
    }

    private void addPasswordFieldWithToggle(JPanel panel) {
        JLabel label = new JLabel("Password:");
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField.setMaximumSize(new Dimension(480, 50));
        passwordField.setPreferredSize(new Dimension(480, 50));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 15));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        showPasswordCheck.setFont(new Font("Arial", Font.PLAIN, 15));
        showPasswordCheck.setAlignmentX(Component.CENTER_ALIGNMENT);
        showPasswordCheck.setBackground(null);
        showPasswordCheck.setFocusPainted(false);

        showPasswordCheck.addActionListener(e -> {
            char echoChar = showPasswordCheck.isSelected() ? (char) 0 : '•';
            passwordField.setEchoChar(echoChar);
            confirmPasswordField.setEchoChar(echoChar);
        });

        panel.add(Box.createVerticalStrut(12));
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(showPasswordCheck);
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(140, 42));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void register() {
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String department = departmentField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (fullName.isBlank() || email.isBlank() || department.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean registered = authService.registerStudent(fullName, email, password, department);

        if (registered) {
            JOptionPane.showMessageDialog(this, "Registration successful. Please login.");
            switchToLogin();                    // Better switching
        } else {
            JOptionPane.showMessageDialog(this, "Email already exists or registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}