package app.dao;

import app.db.DBConnection;
import app.model.Complaint;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    // Existing method (addComplaint)
    public static boolean addComplaint(Complaint c) {
        String sql = "INSERT INTO Complaints(studentName, category, description, status) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getStudentName());
            ps.setString(2, c.getCategory());
            ps.setString(3, c.getDescription());
            ps.setString(4, c.getStatus());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        c.setId(rs.getInt(1));
                    }
                }
                System.out.println("✅ Complaint added with ID: " + c.getId());
                return true;
            }
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== NEW METHOD ====================
    public static List<Complaint> getAllComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        String sql = "SELECT * FROM Complaints ORDER BY complaintDate DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Complaint c = new Complaint(
                        rs.getString("studentName"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getString("status")
                );
                c.setId(rs.getInt("id"));
                complaints.add(c);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return complaints;
    }
}