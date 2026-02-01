package com.myfinance.repository;

import com.myfinance.model.User;
import com.myfinance.util.FileStorage;
import java.io.IOException;
import java.util.Map;

public class UserRepository {
    private Map<String, User> users;

    public UserRepository() {
        try {
            this.users = FileStorage.loadUsers();
            System.out.println("Пользователи загружены из файла");
        } catch (IOException e) {
            System.err.println("Ошибка загрузки пользователей: " + e.getMessage());
            this.users = new java.util.HashMap<>();
            users.put("test", new User("test", "test123"));
        }
    }

    public User findByUsername(String username) {
        return users.get(username);
    }

    public void save(User user) throws IOException {
        users.put(user.getUsername(), user);
        FileStorage.saveUsers(users);
    }

    public boolean exists(String username) {
        return users.containsKey(username);
    }

    public Map<String, User> getAllUsers() {
        return users;
    }
}