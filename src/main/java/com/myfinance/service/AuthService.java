package com.myfinance.service;

import com.myfinance.model.User;
import com.myfinance.repository.UserRepository;
import com.myfinance.util.FileStorage;
import java.io.IOException;

public class AuthService {
    private UserRepository userRepository;
    private User currentUser;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.currentUser = null;
    }

    public boolean login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.checkPassword(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public boolean register(String username, String password) {
        if (userRepository.exists(username)) {
            return false;
        }

        try {
            User user = new User(username, password);
            userRepository.save(user);
            currentUser = user;
            return true;
        } catch (IOException e) {
            System.err.println("Ошибка сохранения пользователя: " + e.getMessage());
            return false;
        }
    }

    public void logout() {
        try {
            // Сохраняем всех пользователей перед выходом
            FileStorage.saveUsers(userRepository.getAllUsers());
            currentUser = null;
        } catch (IOException e) {
            System.err.println("Ошибка сохранения данных: " + e.getMessage());
        }
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}