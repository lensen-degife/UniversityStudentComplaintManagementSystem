package app.ui;

import javax.swing.*;
import java.awt.*;

public final class UiTheme {

    public static final Color BACKGROUND = new Color(245, 247, 252);
    public static final Color PRIMARY = new Color(0, 102, 204);
    public static final Color SECONDARY = new Color(70, 130, 180);
    public static final Color MUTED = new Color(108, 117, 125);
    public static final Color TEXT = new Color(50, 50, 50);
    public static final Color HEADING = new Color(0, 51, 102);

    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 32);
    public static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 16);

    private UiTheme() {
    }

    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static JPanel createFormRow(String labelText, JComponent field) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(0, 0, 0, 12);
        labelConstraints.weightx = 0;

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT);
        label.setPreferredSize(new Dimension(140, 28));
        panel.add(label, labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = 0;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.anchor = GridBagConstraints.WEST;

        field.setFont(FIELD_FONT);
        field.setPreferredSize(new Dimension(0, 38));
        panel.add(field, fieldConstraints);

        return panel;
    }

    public static void stylePrimaryButton(JButton button) {
        styleButton(button, PRIMARY);
    }

    public static void styleSecondaryButton(JButton button) {
        styleButton(button, SECONDARY);
    }

    public static void styleMutedButton(JButton button) {
        styleButton(button, MUTED);
    }

    public static void styleButton(JButton button, Color color) {
        button.setPreferredSize(new Dimension(140, 45));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(BUTTON_FONT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static JPanel createAuthShell(String title, JPanel formPanel, JPanel buttonPanel) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(HEADING);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        formPanel.setBackground(BACKGROUND);
        buttonPanel.setBackground(BACKGROUND);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    public static void configureAuthFrame(JFrame frame, String title) {
        frame.setTitle(title);
        frame.setMinimumSize(new Dimension(520, 480));
        frame.setSize(670, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }
}
