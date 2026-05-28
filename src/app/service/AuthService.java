package app.service;

import app.dao.UserDAO;
import app.model.Role;
import app.model.User;
import app.util.PasswordUtil;

import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public Optional<User> login(String email, String plainPassword, Role role) {
        Optional<User> userOptional = userDAO.findByEmail(email);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();
        if (!user.isActive() || user.getRole() != role) {
            return Optional.empty();
        }

        if (!PasswordUtil.verifyPassword(plainPassword, user.getPasswordHash())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public boolean registerStudent(String fullName, String email, String plainPassword, String department) {
        if (userDAO.findByEmail(email).isPresent()) {
            return false;
        }

        User user = new User(fullName, email, PasswordUtil.hashPassword(plainPassword), Role.STUDENT, department);
        return userDAO.registerStudent(user);
    }
}
