package app.dao;

import app.db.DBConnection;
import app.model.Complaint;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ComplaintDAO {

    public static boolean addComplaint(Complaint c) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO Complaints(studentName, category, description, status) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, c.studentName);
            ps.setString(2, c.category);
            ps.setString(3, c.description);
            ps.setString(4, c.status);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }
}
