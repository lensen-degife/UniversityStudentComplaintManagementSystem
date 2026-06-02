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
    private final JCheckBox showPasswordCheck = new JCheckBox("Show Passwords");

    private final AuthService authService = new AuthService();
    private final LoginFrame loginFrame;

    public RegisterFrame(LoginFrame loginFrame) {
        this.loginFrame = loginFrame;

        UiTheme.configureAuthFrame(this, "Student Account Registration");

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.add(UiTheme.createFormRow("Full Name", fullNameField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(UiTheme.createFormRow("Email Address", emailField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(UiTheme.createFormRow("Academic Department", departmentField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(UiTheme.createFormRow("Password", passwordField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(UiTheme.createFormRow("Confirm Password", confirmPasswordField));
        formPanel.add(Box.createVerticalStrut(6));

        showPasswordCheck.setBackground(UiTheme.CARD_BACKGROUND);
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPasswordCheck.setForeground(UiTheme.MUTED);
        showPasswordCheck.setFocusPainted(false);
        showPasswordCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPasswordCheck.addActionListener(e -> {
            char echoChar = showPasswordCheck.isSelected() ? (char) 0 : '•';
            passwordField.setEchoChar(echoChar);
            confirmPasswordField.setEchoChar(echoChar);
        });

        JPanel checkWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkWrapper.setBackground(UiTheme.CARD_BACKGROUND);
        checkWrapper.add(showPasswordCheck);
        formPanel.add(checkWrapper);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        JButton backButton = new JButton("Return to Login");
        JButton registerButton = new JButton("Register Account");

        UiTheme.styleMutedButton(backButton);
        UiTheme.stylePrimaryButton(registerButton);

        backButton.addActionListener(e -> switchToLogin());
        registerButton.addActionListener(e -> register());

        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);

        setContentPane(UiTheme.createAuthShell("Join the Platform", formPanel, buttonPanel));
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
            JOptionPane.showMessageDialog(this, "All fields are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean registered = authService.registerStudent(fullName, email, password, department);

        if (registered) {
            JOptionPane.showMessageDialog(this, "Registration complete! You may now log in.");
            switchToLogin();
        } else {
            JOptionPane.showMessageDialog(this, "Email is already registered or system error occurred.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}