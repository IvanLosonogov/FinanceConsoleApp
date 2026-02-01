package com.myfinance;

import com.myfinance.cli.CommandExecutor;
import com.myfinance.repository.UserRepository;
import com.myfinance.repository.WalletRepository;
import com.myfinance.service.AuthService;
import com.myfinance.service.NotificationService;
import com.myfinance.service.WalletService;

public class Main {
    public static void main(String[] args) {
        try {
            UserRepository userRepository = new UserRepository();
            WalletRepository walletRepository = new WalletRepository();

            AuthService authService = new AuthService(userRepository);
            WalletService walletService = new WalletService(walletRepository);
            NotificationService notificationService = new NotificationService();

            CommandExecutor executor = new CommandExecutor(
                    authService, walletService, notificationService
            );

            executor.run();

        } catch (Exception e) {
            System.err.println("Критическая ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}