package com.myfinance.cli;

import com.myfinance.service.*;
import com.myfinance.util.Validator;
import com.myfinance.util.FileStorage;
import java.util.Scanner;

public class CommandExecutor {
    private AuthService authService;
    private WalletService walletService;
    private NotificationService notificationService;
    private ConsolePrinter printer;
    private Scanner scanner;
    private boolean running;

    public CommandExecutor(AuthService authService,
                           WalletService walletService,
                           NotificationService notificationService) {
        this.authService = authService;
        this.walletService = walletService;
        this.notificationService = notificationService;
        this.printer = new ConsolePrinter();
        this.scanner = new Scanner(System.in);
        this.running = true;
    }

    public void run() {
        printer.printWelcome();

        while (running && !authService.isAuthenticated()) {
            handleAuth();
        }

        while (running && authService.isAuthenticated()) {
            printer.printMenu();
            String choice = scanner.nextLine().trim();
            handleMainMenu(choice);
        }

        scanner.close();
        System.out.println("До свидания!");
    }

    private void handleAuth() {
        printer.printAuthMenu();
        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "2":
                handleRegister();
                break;
            case "3":
                running = false;
                break;
            default:
                printer.printError("Неверный выбор");
        }
    }

    private void handleMainMenu(String choice) {
        switch (choice) {
            case "1":
                addIncome();
                break;
            case "2":
                addExpense();
                break;
            case "3":
                setBudget();
                break;
            case "4":
                showStatistics();
                break;
            case "5":
                showTransactions();
                break;
            case "6":
                showBudgets();
                break;
            case "7":
                createCategory();
                break;
            case "8":
                exportToCSV();
                break;
            case "9":
                editBudget();
                break;
            case "10":
                showHelp();
                break;
            case "0":
                handleLogout();
                break;
            default:
                printer.printError("Неверный выбор. Введите '10' для справки.");
        }
    }

    private void handleLogin() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine().trim();

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine().trim();

        if (!Validator.isValidUsername(username) || !Validator.isValidPassword(password)) {
            printer.printError("Неверный формат логина или пароля");
            return;
        }

        if (authService.login(username, password)) {
            walletService.loadWallet(username);
            printer.printSuccess("Добро пожаловать, " + username + "!");

            notificationService.checkNotifications(walletService.getCurrentWallet());
        } else {
            printer.printError("Неверный логин или пароль");
        }
    }

    private void handleRegister() {
        System.out.print("Введите логин (минимум 3 символа): ");
        String username = scanner.nextLine().trim();

        System.out.print("Введите пароль (минимум 3 символа): ");
        String password = scanner.nextLine().trim();

        if (!Validator.isValidUsername(username) || !Validator.isValidPassword(password)) {
            printer.printError("Неверный формат логина или пароля");
            return;
        }

        if (authService.register(username, password)) {
            walletService.loadWallet(username);
            printer.printSuccess("Регистрация успешна! Добро пожаловать, " + username);
        } else {
            printer.printError("Пользователь уже существует");
        }
    }

    private void addIncome() {
        try {
            System.out.print("Введите сумму дохода: ");
            String amountStr = scanner.nextLine().trim();

            if (!Validator.isValidAmount(amountStr)) {
                printer.printError("Неверная сумма");
                return;
            }

            double amount = Double.parseDouble(amountStr);

            System.out.print("Введите категорию дохода: ");
            String category = scanner.nextLine().trim();

            if (!Validator.isNotEmpty(category)) {
                printer.printError("Категория не может быть пустой");
                return;
            }

            System.out.print("Введите описание (необязательно): ");
            String description = scanner.nextLine().trim();

            walletService.addIncome(amount, category, description);
            printer.printSuccess("Доход добавлен");
            notificationService.checkNotifications(walletService.getCurrentWallet());

        } catch (Exception e) {
            printer.printError("Ошибка при добавлении дохода: " + e.getMessage());
        }
    }

    private void addExpense() {
        try {
            System.out.print("Введите сумму расхода: ");
            String amountStr = scanner.nextLine().trim();

            if (!Validator.isValidAmount(amountStr)) {
                printer.printError("Неверная сумма");
                return;
            }

            double amount = Double.parseDouble(amountStr);

            System.out.print("Введите категорию расхода: ");
            String category = scanner.nextLine().trim();

            if (!Validator.isNotEmpty(category)) {
                printer.printError("Категория не может быть пустой");
                return;
            }

            System.out.print("Введите описание (необязательно): ");
            String description = scanner.nextLine().trim();

            walletService.addExpense(amount, category, description);
            printer.printSuccess("Расход добавлен");
            notificationService.checkNotifications(walletService.getCurrentWallet());

        } catch (Exception e) {
            printer.printError("Ошибка при добавлении расхода: " + e.getMessage());
        }
    }

    private void setBudget() {
        try {
            System.out.print("Введите категорию для бюджета: ");
            String category = scanner.nextLine().trim();

            if (!Validator.isNotEmpty(category)) {
                printer.printError("Категория не может быть пустой");
                return;
            }

            System.out.print("Введите лимит бюджета: ");
            String limitStr = scanner.nextLine().trim();

            if (!Validator.isValidAmount(limitStr)) {
                printer.printError("Неверный лимит");
                return;
            }

            double limit = Double.parseDouble(limitStr);

            walletService.setBudget(category, limit);
            printer.printSuccess("Бюджет установлен");

        } catch (Exception e) {
            printer.printError("Ошибка при установке бюджета: " + e.getMessage());
        }
    }

    private void editBudget() {
        try {
            System.out.print("Введите категорию для редактирования бюджета: ");
            String category = scanner.nextLine().trim();

            if (!Validator.isNotEmpty(category)) {
                printer.printError("Категория не может быть пустой");
                return;
            }

            var budget = walletService.getCurrentWallet().getBudget(category);
            if (budget == null) {
                printer.printError("Бюджет для категории '" + category + "' не найден");
                return;
            }

            System.out.println("Текущий бюджет: " + budget.getLimit());
            System.out.print("Введите новый лимит бюджета: ");
            String limitStr = scanner.nextLine().trim();

            if (!Validator.isValidAmount(limitStr)) {
                printer.printError("Неверный лимит");
                return;
            }

            double newLimit = Double.parseDouble(limitStr);
            budget.setLimit(newLimit);
            printer.printSuccess("Бюджет обновлен");

        } catch (Exception e) {
            printer.printError("Ошибка при редактировании бюджета: " + e.getMessage());
        }
    }

    private void showStatistics() {
        printer.printStatistics(walletService.getCurrentWallet());
    }

    private void showTransactions() {
        printer.printTransactions(walletService.getTransactions());
    }

    private void showBudgets() {
        var budgets = walletService.getBudgets();
        if (budgets.isEmpty()) {
            printer.printInfo("Бюджеты не установлены");
        } else {
            System.out.println("\n=== БЮДЖЕТЫ ===");
            budgets.forEach(System.out::println);
        }
    }

    private void createCategory() {
        System.out.print("Введите название категории: ");
        String name = scanner.nextLine().trim();

        System.out.print("Тип (1 - доход, 2 - расход): ");
        String typeChoice = scanner.nextLine().trim();

        com.myfinance.model.TransactionType type;
        if (typeChoice.equals("1")) {
            type = com.myfinance.model.TransactionType.INCOME;
        } else if (typeChoice.equals("2")) {
            type = com.myfinance.model.TransactionType.EXPENSE;
        } else {
            printer.printError("Неверный тип");
            return;
        }

        var category = new com.myfinance.model.Category(name, type);
        walletService.getCurrentWallet().addCategory(category);
        printer.printSuccess("Категория создана: " + category);
    }

    private void exportToCSV() {
        try {
            System.out.print("Введите имя файла для экспорта (без расширения): ");
            String filename = scanner.nextLine().trim();

            if (!Validator.isNotEmpty(filename)) {
                printer.printError("Имя файла не может быть пустым");
                return;
            }

            FileStorage.exportToCSV(walletService.getCurrentWallet(), filename);
            printer.printSuccess("Отчет успешно экспортирован в " + filename + ".csv");

        } catch (Exception e) {
            printer.printError("Ошибка при экспорте: " + e.getMessage());
        }
    }

    private void showHelp() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("СПРАВКА ПО КОМАНДАМ");
        System.out.println("=".repeat(60));
        System.out.println("1  - Добавить доход");
        System.out.println("2  - Добавить расход");
        System.out.println("3  - Установить бюджет для категории");
        System.out.println("4  - Показать статистику");
        System.out.println("5  - Показать все транзакции");
        System.out.println("6  - Показать бюджеты");
        System.out.println("7  - Создать новую категорию");
        System.out.println("8  - Экспортировать отчет в CSV");
        System.out.println("9  - Редактировать бюджет");
        System.out.println("10 - Показать эту справку");
        System.out.println("0  - Выйти из системы");
        System.out.println("=".repeat(60));
        System.out.println("Примеры использования:");
        System.out.println("  Для добавления дохода: введите 1, затем сумму, категорию");
        System.out.println("  Для установки бюджета: введите 3, затем 'Еда', '4000'");
        System.out.println("=".repeat(60));
    }

    private void handleLogout() {
        try {
            walletService.saveWallet();
            FileStorage.saveWallet(walletService.getCurrentWallet());

            authService.logout();
            printer.printSuccess("Вы вышли из системы. Данные сохранены.");
            running = false;
        } catch (Exception e) {
            printer.printError("Ошибка при сохранении данных: " + e.getMessage());
        }
    }
}