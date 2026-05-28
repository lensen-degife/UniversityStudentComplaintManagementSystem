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
    private final AuthService authService = new AuthService();

    public RegisterFrame() {
        setTitle("Student Registration");
        setSize(500, 360);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addField(panel, gbc, 0, "Full Name:", fullNameField);
        addField(panel, gbc, 1, "Email:", emailField);
        addField(panel, gbc, 2, "Department:", departmentField);
        addField(panel, gbc, 3, "Password:", passwordField);
        addField(panel, gbc, 4, "Confirm Password:", confirmPasswordField);

        JButton backButton = new JButton("Back to Login");
        backButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> register());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        setContentPane(panel);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
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
            new LoginFrame().setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Email already exists or registration failed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
