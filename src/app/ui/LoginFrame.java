package app.ui;

import javax.swing.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        setTitle("Login");
        setSize(400,300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JLabel l1 = new JLabel("Username:");
        l1.setBounds(20,20,80,25);
        add(l1);

        JTextField t1 = new JTextField();
        t1.setBounds(100,20,150,25);
        add(t1);

        JLabel l2 = new JLabel("Password:");
        l2.setBounds(20,60,80,25);
        add(l2);

        JPasswordField p1 = new JPasswordField();
        p1.setBounds(100,60,150,25);
        add(p1);

        JButton b1 = new JButton("Login");
        b1.setBounds(100,100,100,30);
        add(b1);

        b1.addActionListener(e -> {
            new StudentDashboard().setVisible(true);
            dispose();
        });
    }
}
