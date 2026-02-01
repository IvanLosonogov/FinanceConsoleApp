package com.myfinance.model;

public class User {
    private String username;
    private String password;
    private String walletId;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.walletId = username + "_wallet";
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getWalletId() { return walletId; }

    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
}