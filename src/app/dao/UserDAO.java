package app.dao;

import app.db.DBConnection;
import app.model.Role;
import app.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;

public class UserDAO {

    public boolean registerStudent(User user) {
        String sql = "INSERT INTO users(full_name, email, password_hash, role, department, is_active) VALUES (?, ?, ?, ?, ?, 1)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, user.getFullName());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setString(4, Role.STUDENT.name());
            preparedStatement.setString(5, user.getDepartment());
            int affected = preparedStatement.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, full_name, email, password_hash, role, department, is_active, created_at FROM users WHERE email = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setFullName(resultSet.getString("full_name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPasswordHash(resultSet.getString("password_hash"));
                    user.setRole(Role.valueOf(resultSet.getString("role")));
                    user.setDepartment(resultSet.getString("department"));
                    user.setActive(resultSet.getBoolean("is_active"));
                    var timestamp = resultSet.getTimestamp("created_at");
                    if (timestamp != null) {
                        user.setCreatedAt(timestamp.toLocalDateTime());
                    } else {
                        user.setCreatedAt(LocalDateTime.now());
                    }
                    return Optional.of(user);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }
}
