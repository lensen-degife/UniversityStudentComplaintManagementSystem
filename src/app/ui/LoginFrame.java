package app.ui;

import app.model.Role;
import app.model.User;
import app.service.AuthService;
import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField(25);
    private final JPasswordField passwordField = new JPasswordField(25);
    private final JComboBox<Role> roleCombo = new JComboBox<>(Role.values());
    private final JCheckBox showPasswordCheck = new JCheckBox("Show Password");

    private final AuthService authService = new AuthService();

    // Reference to RegisterFrame for smooth switching
    private final RegisterFrame registerFrame;

    public LoginFrame() {
        // Store reference to RegisterFrame
        this.registerFrame = new RegisterFrame(this);

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        setTitle("University Complaint Management - Login");
        setSize(670, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(HIDE_ON_CLOSE);   // Changed for better switching

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        mainPanel.setBackground(new Color(245, 247, 252));

        // Title
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 51, 102));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        formPanel.setBackground(new Color(245, 247, 252));

        formPanel.add(createLabelFieldPanel("Email:", emailField));
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createPasswordFieldPanel());
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(showPasswordCheck);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(createLabelFieldPanel("Login as:", roleCombo));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 15));
        buttonPanel.setBackground(new Color(245, 247, 252));

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        styleButton(loginButton, new Color(0, 102, 204));
        styleButton(registerButton, new Color(70, 130, 180));

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> switchToRegister());

        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);

        // Assemble main panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(35));
        mainPanel.add(buttonPanel);

        setContentPane(mainPanel);

        // Password Toggle
        showPasswordCheck.setBackground(new Color(245, 247, 252));
        showPasswordCheck.setFont(new Font("Arial", Font.PLAIN, 15));
        showPasswordCheck.addActionListener(e -> {
            char echo = showPasswordCheck.isSelected() ? (char) 0 : '•';
            passwordField.setEchoChar(echo);
        });
    }

    private void switchToRegister() {
        clearFields();               // Clear login form
        setVisible(false);           // Hide LoginFrame
        registerFrame.setVisible(true); // Show RegisterFrame
    }

    void clearFields() {
        emailField.setText("");
        passwordField.setText("");
        showPasswordCheck.setSelected(false);
        passwordField.setEchoChar('•');
        roleCombo.setSelectedIndex(0);
    }

    private JPanel createLabelFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(new Color(245, 247, 252));

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 28));
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(new Color(50, 50, 50));

        field.setMaximumSize(new Dimension(400, 38));
        field.setFont(new Font("Arial", Font.PLAIN, 16));

        panel.add(label, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPasswordFieldPanel() {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(new Color(245, 247, 252));

        JLabel label = new JLabel("Password:");
        label.setPreferredSize(new Dimension(120, 28));
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setForeground(new Color(50, 50, 50));

        passwordField.setMaximumSize(new Dimension(400, 38));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));

        panel.add(label, BorderLayout.WEST);
        panel.add(passwordField, BorderLayout.CENTER);
        return panel;
    }

    private void styleButton(JButton button, Color color) {
        button.setPreferredSize(new Dimension(140, 45));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

        // Open dashboard
        if (role == Role.STUDENT) {
            new StudentDashboard(user.get()).setVisible(true);
        } else {
            new AdminDashboard(user.get(), this).setVisible(true);
        }

        setVisible(false);   // Hide login frame after successful login
    }
}