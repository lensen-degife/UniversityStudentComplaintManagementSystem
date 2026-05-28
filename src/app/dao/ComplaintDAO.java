package app.dao;

import app.db.DBConnection;
import app.model.Complaint;
import app.model.ComplaintCategory;
import app.model.ComplaintStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDAO {

    public boolean addComplaint(Complaint complaint) {
        String sql = "INSERT INTO complaints(student_id, category, title, description, attachment_path, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, complaint.getStudentId());
            preparedStatement.setString(2, complaint.getCategory().name());
            preparedStatement.setString(3, complaint.getTitle());
            preparedStatement.setString(4, complaint.getDescription());
            preparedStatement.setString(5, complaint.getAttachmentPath());
            preparedStatement.setString(6, complaint.getStatus().name());

            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        complaint.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public List<Complaint> getByStudentId(int studentId) {
        String sql = "SELECT c.id, c.student_id, u.full_name, c.category, c.title, c.description, c.attachment_path, c.status, c.submitted_at, c.last_updated_at " +
                "FROM complaints c JOIN users u ON c.student_id = u.id WHERE c.student_id = ? ORDER BY c.submitted_at DESC";
        return fetchComplaints(sql, preparedStatement -> preparedStatement.setInt(1, studentId));
    }

    public List<Complaint> getAll(String search, ComplaintStatus status, LocalDate fromDate, LocalDate toDate) {
        StringBuilder sql = new StringBuilder("SELECT c.id, c.student_id, u.full_name, c.category, c.title, c.description, c.attachment_path, c.status, c.submitted_at, c.last_updated_at FROM complaints c JOIN users u ON c.student_id = u.id WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            sql.append(" AND (c.title LIKE ? OR c.description LIKE ? OR u.full_name LIKE ?)");
            String keyword = "%" + search.trim() + "%";
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }

        if (status != null) {
            sql.append(" AND c.status = ?");
            params.add(status.name());
        }

        if (fromDate != null) {
            sql.append(" AND CAST(c.submitted_at AS DATE) >= ?");
            params.add(java.sql.Date.valueOf(fromDate));
        }

        if (toDate != null) {
            sql.append(" AND CAST(c.submitted_at AS DATE) <= ?");
            params.add(java.sql.Date.valueOf(toDate));
        }

        sql.append(" ORDER BY c.submitted_at DESC");

        return fetchComplaints(sql.toString(), preparedStatement -> {
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }
        });
    }

    public boolean updateStatus(int complaintId, ComplaintStatus status) {
        String sql = "UPDATE complaints SET status = ?, last_updated_at = SYSDATETIME() WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, status.name());
            preparedStatement.setInt(2, complaintId);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public Complaint getById(int complaintId) {
        String sql = "SELECT c.id, c.student_id, u.full_name, c.category, c.title, c.description, c.attachment_path, c.status, c.submitted_at, c.last_updated_at " +
                "FROM complaints c JOIN users u ON c.student_id = u.id WHERE c.id = ?";
        List<Complaint> complaints = fetchComplaints(sql, ps -> ps.setInt(1, complaintId));
        return complaints.isEmpty() ? null : complaints.getFirst();
    }

    public int countAll() {
        return countBySql("SELECT COUNT(*) FROM complaints");
    }

    public int countByStatus(ComplaintStatus status) {
        String sql = "SELECT COUNT(*) FROM complaints WHERE status = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, status.name());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private int countBySql(String sql) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    private List<Complaint> fetchComplaints(String sql, SqlSetter setter) {
        List<Complaint> complaints = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            setter.accept(preparedStatement);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Complaint complaint = new Complaint(
                            resultSet.getInt("id"),
                            resultSet.getInt("student_id"),
                            resultSet.getString("full_name"),
                            ComplaintCategory.valueOf(resultSet.getString("category")),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            resultSet.getString("attachment_path"),
                            ComplaintStatus.valueOf(resultSet.getString("status")),
                            resultSet.getTimestamp("submitted_at").toLocalDateTime(),
                            resultSet.getTimestamp("last_updated_at").toLocalDateTime()
                    );
                    complaints.add(complaint);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return complaints;
    }

    @FunctionalInterface
    private interface SqlSetter {
        void accept(PreparedStatement preparedStatement) throws Exception;
    }
}
