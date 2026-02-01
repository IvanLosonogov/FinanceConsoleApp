package com.myfinance.util;

import com.myfinance.model.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileStorage {
    private static final String DATA_DIR = "data/";
    private static final String WALLETS_DIR = DATA_DIR + "wallets/";
    private static final String USERS_FILE = DATA_DIR + "users.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    static {
        try {
            Files.createDirectories(Paths.get(WALLETS_DIR));
        } catch (IOException e) {
            System.err.println("Ошибка создания директорий: " + e.getMessage());
        }
    }

    public static void saveUsers(Map<String, User> users) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.println(user.getUsername() + "|" + user.getPassword());
            }
        }
    }

    public static Map<String, User> loadUsers() throws IOException {
        Map<String, User> users = new HashMap<>();
        File file = new File(USERS_FILE);

        if (!file.exists()) {
            users.put("test", new User("test", "test123"));
            saveUsers(users);
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    User user = new User(parts[0], parts[1]);
                    users.put(user.getUsername(), user);
                }
            }
        }
        return users;
    }

    public static void saveWallet(Wallet wallet) throws IOException {
        String filename = WALLETS_DIR + wallet.getId() + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("WALLET_ID:" + wallet.getId());
            writer.println("USER_ID:" + wallet.getUserId());
            writer.println("BALANCE:" + wallet.getBalance());

            writer.println("==CATEGORIES==");
            for (Category category : wallet.getAllCategories()) {
                writer.println(category.getName() + "|" + category.getType());
            }

            writer.println("==BUDGETS==");
            for (Budget budget : wallet.getAllBudgets()) {
                writer.println(budget.getCategory().getName() + "|" +
                        budget.getLimit() + "|" + budget.getSpent());
            }

            writer.println("==TRANSACTIONS==");
            for (Transaction transaction : wallet.getTransactions()) {
                writer.println(transaction.getId() + "|" +
                        transaction.getType() + "|" +
                        transaction.getCategory().getName() + "|" +
                        transaction.getAmount() + "|" +
                        transaction.getDate().format(DATE_FORMATTER) + "|" +
                        transaction.getDescription());
            }
        }
    }

    public static Wallet loadWallet(String walletId) throws IOException {
        String filename = WALLETS_DIR + walletId + ".txt";
        File file = new File(filename);

        if (!file.exists()) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String id = null;
            String userId = null;
            double balance = 0.0;
            Wallet wallet = null;
            boolean readingCategories = false;
            boolean readingBudgets = false;
            boolean readingTransactions = false;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("WALLET_ID:")) {
                    id = line.substring(10);
                } else if (line.startsWith("USER_ID:")) {
                    userId = line.substring(8);
                    wallet = new Wallet(id, userId);
                } else if (line.startsWith("BALANCE:")) {
                    balance = Double.parseDouble(line.substring(8));
                } else if (line.equals("==CATEGORIES==")) {
                    readingCategories = true;
                    readingBudgets = false;
                    readingTransactions = false;
                } else if (line.equals("==BUDGETS==")) {
                    readingCategories = false;
                    readingBudgets = true;
                    readingTransactions = false;
                } else if (line.equals("==TRANSACTIONS==")) {
                    readingCategories = false;
                    readingBudgets = false;
                    readingTransactions = true;
                } else if (readingCategories && !line.isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 2) {
                        TransactionType type = TransactionType.valueOf(parts[1]);
                        Category category = new Category(parts[0], type);
                        wallet.addCategory(category);
                    }
                } else if (readingBudgets && !line.isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 3) {
                        String categoryName = parts[0];
                        double limit = Double.parseDouble(parts[1]);
                        double spent = Double.parseDouble(parts[2]);

                        Category category = wallet.getCategory(categoryName, TransactionType.EXPENSE);
                        if (category == null) {
                            category = new Category(categoryName, TransactionType.EXPENSE);
                            wallet.addCategory(category);
                        }

                        Budget budget = new Budget(category, limit);
                        for (int i = 0; i < Math.round(spent / 100); i++) {
                            budget.addSpent(100);
                        }
                        if (Math.round(spent % 100) > 0) {
                            budget.addSpent(spent % 100);
                        }
                        wallet.setBudget(category, limit);
                    }
                } else if (readingTransactions && !line.isEmpty()) {
                    String[] parts = line.split("\\|", 6);
                    if (parts.length >= 6) {
                        TransactionType type = TransactionType.valueOf(parts[1]);
                        String categoryName = parts[2];
                        double amount = Double.parseDouble(parts[3]);
                        String description = parts.length > 5 ? parts[5] : "";

                        Category category = wallet.getCategory(categoryName, type);
                        if (category == null) {
                            category = new Category(categoryName, type);
                            wallet.addCategory(category);
                        }

                        Transaction transaction;
                        if (type == TransactionType.INCOME) {
                            transaction = new Income(amount, category, description);
                        } else {
                            transaction = new Expense(amount, category, description);
                        }

                        wallet.addTransaction(transaction);
                    }
                }
            }

            if (wallet != null) {
                return wallet;
            }
        }

        return null;
    }

    public static void exportToCSV(Wallet wallet, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename + ".csv"))) {
            writer.println("Тип,Категория,Сумма,Дата,Описание");

            for (Transaction transaction : wallet.getTransactions()) {
                writer.printf("%s,%s,%.2f,%s,%s%n",
                        transaction.getType() == TransactionType.INCOME ? "Доход" : "Расход",
                        transaction.getCategory().getName(),
                        transaction.getAmount(),
                        transaction.getDate().toLocalDate(),
                        transaction.getDescription());
            }

            System.out.println("Отчет экспортирован в " + filename + ".csv");
        }
    }

    public static void saveAll(Map<String, User> users, Map<String, Wallet> wallets) throws IOException {
        saveUsers(users);
        for (Wallet wallet : wallets.values()) {
            saveWallet(wallet);
        }
    }
}