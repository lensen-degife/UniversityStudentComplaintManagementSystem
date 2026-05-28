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
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        setTitle("University Complaint Management - Login");
        setSize(650, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Login as:"), gbc);

        gbc.gridx = 1;
        panel.add(roleCombo, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> login());

        JButton registerButton = new JButton("Student Registration");
        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(registerButton);
        buttons.add(loginButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(buttons, gbc);

        setContentPane(panel);
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        Role role = (Role) roleCombo.getSelectedItem();

        if (email.isBlank() || password.isBlank() || role == null) {
            JOptionPane.showMessageDialog(this, "Please fill all login fields.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<User> user = authService.login(email, password, role);
        if (user.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid credentials or role.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (role == Role.STUDENT) {
            new StudentDashboard(user.get()).setVisible(true);
        } else {
            new AdminDashboard(user.get()).setVisible(true);
        }
        dispose();
    }
}
