package app.dao;

import app.db.DBConnection;
import app.model.ComplaintResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ComplaintResponseDAO {

    public boolean addResponse(ComplaintResponse response) {
        String sql = "INSERT INTO complaint_responses(complaint_id, responder_user_id, message) VALUES (?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, response.getComplaintId());
            preparedStatement.setInt(2, response.getResponderUserId());
            preparedStatement.setString(3, response.getMessage());
            int affected = preparedStatement.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        response.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public List<ComplaintResponse> getByComplaintId(int complaintId) {
        List<ComplaintResponse> responses = new ArrayList<>();
        String sql = "SELECT cr.id, cr.complaint_id, cr.responder_user_id, cr.message, cr.created_at, u.full_name " +
                "FROM complaint_responses cr " +
                "JOIN users u ON u.id = cr.responder_user_id " +
                "WHERE cr.complaint_id = ? ORDER BY cr.created_at ASC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, complaintId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    ComplaintResponse response = new ComplaintResponse();
                    response.setId(resultSet.getInt("id"));
                    response.setComplaintId(resultSet.getInt("complaint_id"));
                    response.setResponderUserId(resultSet.getInt("responder_user_id"));
                    response.setResponderName(resultSet.getString("full_name"));
                    response.setMessage(resultSet.getString("message"));
                    response.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    responses.add(response);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return responses;
    }
}
