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
        UiTheme.applyLookAndFeel();
        this.registerFrame = new RegisterFrame(this);

        UiTheme.configureAuthFrame(this, "University Portal - Login");

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.add(UiTheme.createFormRow("Email Address", emailField));
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(UiTheme.createFormRow("Password", passwordField));
        formPanel.add(Box.createVerticalStrut(6));

        showPasswordCheck.setBackground(UiTheme.CARD_BACKGROUND);
        showPasswordCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPasswordCheck.setForeground(UiTheme.MUTED);
        showPasswordCheck.setFocusPainted(false);
        showPasswordCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        showPasswordCheck.addActionListener(e -> {
            passwordField.setEchoChar(showPasswordCheck.isSelected() ? (char) 0 : '•');
        });

        JPanel checkWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkWrapper.setBackground(UiTheme.CARD_BACKGROUND);
        checkWrapper.add(showPasswordCheck);
        formPanel.add(checkWrapper);

        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(UiTheme.createFormRow("Portal Role", roleCombo));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        JButton registerButton = new JButton("Create Account");
        JButton loginButton = new JButton("Sign In");

        UiTheme.styleSecondaryButton(registerButton);
        UiTheme.stylePrimaryButton(loginButton);

        registerButton.addActionListener(e -> switchToRegister());
        loginButton.addActionListener(e -> login());

        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        setContentPane(UiTheme.createAuthShell("Welcome Back", formPanel, buttonPanel));
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
            JOptionPane.showMessageDialog(this, "Please fill in all details.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<User> user = authService.login(email, password, role);

        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid credentials or incorrect role.", "Access Denied", JOptionPane.ERROR_MESSAGE);
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