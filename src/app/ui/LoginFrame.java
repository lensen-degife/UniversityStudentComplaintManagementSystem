package app.ui;

import app.model.Role;
import app.model.User;
import app.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JComboBox<Role> roleCombo = new JComboBox<>(Role.values());
    private final JCheckBox showPasswordCheck = new JCheckBox("Show Password");

    private final AuthService authService = new AuthService();
    private final RegisterFrame registerFrame;

    public LoginFrame() {
        this.registerFrame = new RegisterFrame(this);

        UiTheme.configureAuthFrame(this, "University Complaint Management - Login");

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        formPanel.add(UiTheme.createFormRow("Email:", emailField));
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(UiTheme.createFormRow("Password:", passwordField));
        formPanel.add(Box.createVerticalStrut(8));

        showPasswordCheck.setBackground(UiTheme.BACKGROUND);
        showPasswordCheck.setFont(UiTheme.LABEL_FONT);
        showPasswordCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPasswordCheck.addActionListener(e -> {
            char echo = showPasswordCheck.isSelected() ? (char) 0 : '•';
            passwordField.setEchoChar(echo);
        });
        formPanel.add(showPasswordCheck);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(UiTheme.createFormRow("Login as:", roleCombo));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));

        JButton registerButton = new JButton("Register");
        JButton loginButton = new JButton("Login");
        UiTheme.styleSecondaryButton(registerButton);
        UiTheme.stylePrimaryButton(loginButton);

        registerButton.addActionListener(e -> switchToRegister());
        loginButton.addActionListener(e -> login());

        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        setContentPane(UiTheme.createAuthShell("Login", formPanel, buttonPanel));
    }

    private void switchToRegister() {
        clearFields();
        setVisible(false);
        registerFrame.setVisible(true);
    }

    void clearFields() {
        emailField.setText("");
        passwordField.setText("");
        showPasswordCheck.setSelected(false);
        passwordField.setEchoChar('•');
        roleCombo.setSelectedIndex(0);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role role = (Role) roleCombo.getSelectedItem();

        if (email.isBlank() || password.isBlank() || role == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<User> user = authService.login(email, password, role);

        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid credentials or role.",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (role == Role.STUDENT) {
            new StudentDashboard(user.get(), this).setVisible(true);
        } else {
            new AdminDashboard(user.get(), this).setVisible(true);
        }

        setVisible(false);
    }
}
