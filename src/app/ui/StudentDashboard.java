package app.ui;

import app.dao.ComplaintDAO;
import app.model.Complaint;

import javax.swing.*;

public class StudentDashboard extends JFrame {

    public StudentDashboard() {
        setTitle("Student Dashboard");
        setSize(400,300);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTextField name = new JTextField();
        name.setBounds(20,20,150,25);
        add(name);

        JTextField category = new JTextField();
        category.setBounds(20,60,150,25);
        add(category);

        JTextArea desc = new JTextArea();
        desc.setBounds(20,100,300,80);
        add(desc);

        JButton submit = new JButton("Submit Complaint");
        submit.setBounds(20,200,200,30);
        add(submit);

        submit.addActionListener(e -> {
            Complaint c = new Complaint(
                name.getText(),
                category.getText(),
                desc.getText(),
                "Pending"
            );
            boolean ok = ComplaintDAO.addComplaint(c);
            JOptionPane.showMessageDialog(this, ok ? "Submitted" : "Failed");
        });
    }
}
