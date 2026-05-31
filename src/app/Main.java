package app;

import app.ui.LoginFrame;
import app.ui.UiTheme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UiTheme.applyLookAndFeel();
            new LoginFrame().setVisible(true);
        });
    }
}
