package app.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class UiTheme {

    // Modern SaaS Slate Palette
    public static final Color APP_BACKGROUND = new Color(248, 250, 252);   // Slate 50
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color PRIMARY = new Color(15, 23, 42);             // Slate 900
    public static final Color PRIMARY_HOVER = new Color(51, 65, 85);       // Slate 700
    public static final Color ACCENT = new Color(79, 70, 229);             // Indigo 600
    public static final Color ACCENT_HOVER = new Color(67, 56, 202);       // Indigo 700
    public static final Color MUTED = new Color(100, 116, 139);            // Slate 500
    public static final Color MUTED_HOVER = new Color(71, 85, 105);        // Slate 600
    public static final Color TEXT_MAIN = new Color(15, 23, 42);           // Slate 900
    public static final Color TEXT_MUTED = new Color(148, 163, 184);       // Slate 400
    public static final Color BORDER_COLOR = new Color(226, 232, 240);     // Slate 200

    // PC-Optimized Typography (Larger Sizes)
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 16);

    private UiTheme() {}

    public static void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("Panel.background", APP_BACKGROUND);
            UIManager.put("Label.foreground", TEXT_MAIN);
            UIManager.put("Label.font", FIELD_FONT);
            UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(BORDER_COLOR, 1));
            UIManager.put("TabbedPane.background", APP_BACKGROUND);
            UIManager.put("TabbedPane.selected", CARD_BACKGROUND);
            UIManager.put("TabbedPane.font", HEADER_FONT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JPanel createFormRow(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 8)); // Slightly larger gap for PC
        panel.setBackground(CARD_BACKGROUND);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_MAIN);
        panel.add(label, BorderLayout.NORTH);

        field.setFont(FIELD_FONT);
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_MAIN);

        if (field instanceof JTextField || field instanceof JComboBox) {
            field.setPreferredSize(new Dimension(0, 50)); // Increased height from 42 to 50
            field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, BORDER_COLOR),
                    BorderFactory.createEmptyBorder(0, 14, 0, 14) // Slightly wider text inset
            ));
            if (field instanceof JTextField) {
                ((JTextField) field).setCaretColor(ACCENT);
            }
        }
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    public static void stylePrimaryButton(JButton button) { styleButton(button, ACCENT, ACCENT_HOVER); }
    public static void styleSecondaryButton(JButton button) { styleButton(button, PRIMARY, PRIMARY_HOVER); }
    public static void styleMutedButton(JButton button) { styleButton(button, MUTED, MUTED_HOVER); }

    static void styleButton(JButton button, Color base, Color hover) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(base);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 50)); // Increased from 145x42
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(hover); }
            public void mouseExited(MouseEvent e) { button.setBackground(base); }
        });
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(50); // Increased from 40
        table.setFont(FIELD_FONT);
        table.setForeground(TEXT_MAIN);
        table.setGridColor(BORDER_COLOR);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(238, 242, 255)); // Light Indigo selection
        table.setSelectionForeground(TEXT_MAIN);

        JTableHeader header = table.getTableHeader();
        header.setFont(HEADER_FONT);
        header.setBackground(APP_BACKGROUND);
        header.setForeground(PRIMARY);
        header.setPreferredSize(new Dimension(0, 50)); // Increased from 42
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
    }

    public static JPanel createAuthShell(String title, JPanel formPanel, JPanel buttonPanel) {
        JPanel mainWrapper = new JPanel(new GridBagLayout());
        mainWrapper.setBackground(APP_BACKGROUND);

        JPanel cardPanel = new JPanel(new BorderLayout(0, 30)); // Increased gap from 24
        cardPanel.setBackground(CARD_BACKGROUND);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(16, BORDER_COLOR),
                BorderFactory.createEmptyBorder(50, 55, 50, 55) // Wider padding
        ));
        cardPanel.setPreferredSize(new Dimension(560, 680)); // Increased from 460x560

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY);

        formPanel.setBackground(CARD_BACKGROUND);
        buttonPanel.setBackground(CARD_BACKGROUND);

        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(formPanel, BorderLayout.CENTER);
        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainWrapper.add(cardPanel);
        return mainWrapper;
    }

    public static void configureAuthFrame(JFrame frame, String title) {
        frame.setTitle(title);
        // Scaled up for standard PC desktop dimensions
        frame.setMinimumSize(new Dimension(1024, 768));
        frame.setSize(1280, 720);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        applyLookAndFeel();
    }

    public static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;
        public RoundedBorder(int radius, Color color) { this.radius = radius; this.color = color; }
        public Insets getBorderInsets(Component c) { return new Insets(radius/2, radius/2, radius/2, radius/2); }
        public boolean isBorderOpaque() { return true; }
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, w - 1, h - 1, radius, radius);
            g2.dispose();
        }
    }
}