package app.ui;

import app.service.AuthService;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final JTextField fullNameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField departmentField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmPasswordField = new JPasswordField();
    private final JCheckBox showPasswordCheck = new JCheckBox("Show Password");

    private final AuthService authService = new AuthService();
    private final LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;

        UiTheme.configureAuthFrame(this, "Student Registration");

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        formPanel.add(UiTheme.createFormRow("Full Name:", fullNameField));
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(UiTheme.createFormRow("Email:", emailField));
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(UiTheme.createFormRow("Department:", departmentField));
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(UiTheme.createFormRow("Password:", passwordField));
        formPanel.add(Box.createVerticalStrut(8));

        showPasswordCheck.setBackground(UiTheme.BACKGROUND);
        showPasswordCheck.setFont(UiTheme.LABEL_FONT);
        showPasswordCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPasswordCheck.addActionListener(e -> {
            char echoChar = showPasswordCheck.isSelected() ? (char) 0 : '•';
            passwordField.setEchoChar(echoChar);
            confirmPasswordField.setEchoChar(echoChar);
        });
        formPanel.add(showPasswordCheck);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(UiTheme.createFormRow("Confirm Password:", confirmPasswordField));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));

        JButton backButton = new JButton("Back to Login");
        JButton registerButton = new JButton("Register");
        UiTheme.styleMutedButton(backButton);
        UiTheme.stylePrimaryButton(registerButton);

        backButton.addActionListener(e -> switchToLogin());
        registerButton.addActionListener(e -> register());

        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);

        setContentPane(UiTheme.createAuthShell("Register", formPanel, buttonPanel));
    }

    private void switchToLogin() {
        clearFields();
        setVisible(false);
        loginFrame.setVisible(true);
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
            switchToLogin();
        } else {
            JOptionPane.showMessageDialog(this, "Email already exists or registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
