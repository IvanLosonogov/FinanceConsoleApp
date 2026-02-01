package com.myfinance.cli;

import com.myfinance.model.*;
import java.util.List;
import java.util.Map;

public class ConsolePrinter {

    public void printWelcome() {
        System.out.println("=".repeat(60));
        System.out.println("          ПРИЛОЖЕНИЕ ДЛЯ УПРАВЛЕНИЯ ФИНАНСАМИ");
        System.out.println("=".repeat(60));
        System.out.println("Введите '10' в главном меню для получения справки");
        System.out.println("=".repeat(60));
    }

    public void printMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("ГЛАВНОЕ МЕНЮ");
        System.out.println("=".repeat(40));
        System.out.println(" 1. Добавить доход");
        System.out.println(" 2. Добавить расход");
        System.out.println(" 3. Установить бюджет");
        System.out.println(" 4. Показать статистику");
        System.out.println(" 5. Показать транзакции");
        System.out.println(" 6. Показать бюджеты");
        System.out.println(" 7. Создать категорию");
        System.out.println(" 8. Экспорт отчета (CSV)");
        System.out.println(" 9. Редактировать бюджет");
        System.out.println("10. Справка");
        System.out.println(" 0. Выход");
        System.out.println("=".repeat(40));
        System.out.print("Введите номер команды: ");
    }

    public void printAuthMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("АВТОРИЗАЦИЯ");
        System.out.println("=".repeat(40));
        System.out.println("1. Войти");
        System.out.println("2. Зарегистрироваться");
        System.out.println("3. Выход из приложения");
        System.out.println("=".repeat(40));
        System.out.print("Выберите действие: ");
    }

    public void printStatistics(Wallet wallet) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ФИНАНСОВАЯ СТАТИСТИКА");
        System.out.println("=".repeat(60));

        System.out.printf("Общий доход:    %,15.2f%n", wallet.getTotalIncome());
        System.out.printf("Общие расходы:  %,15.2f%n", wallet.getTotalExpense());
        System.out.printf("Текущий баланс: %,15.2f%n", wallet.getBalance());

        System.out.println("\n" + "-".repeat(60));
        System.out.println("ДОХОДЫ ПО КАТЕГОРИЯМ:");
        System.out.println("-".repeat(60));
        Map<String, Double> incomeByCategory = wallet.getIncomeByCategory();
        if (incomeByCategory.isEmpty()) {
            System.out.println("  Нет данных о доходах");
        } else {
            incomeByCategory.forEach((cat, amount) ->
                    System.out.printf("  %-25s %,15.2f%n", cat + ":", amount));
        }

        System.out.println("\n" + "-".repeat(60));
        System.out.println("РАСХОДЫ ПО КАТЕГОРИЯМ:");
        System.out.println("-".repeat(60));
        Map<String, Double> expenseByCategory = wallet.getExpenseByCategory();
        if (expenseByCategory.isEmpty()) {
            System.out.println("  Нет данных о расходах");
        } else {
            expenseByCategory.forEach((cat, amount) ->
                    System.out.printf("  %-25s %,15.2f%n", cat + ":", amount));
        }

        System.out.println("\n" + "-".repeat(60));
        System.out.println("БЮДЖЕТЫ:");
        System.out.println("-".repeat(60));
        List<Budget> budgets = wallet.getAllBudgets();
        if (budgets.isEmpty()) {
            System.out.println("  Бюджеты не установлены");
        } else {
            for (Budget budget : budgets) {
                String status = budget.isExceeded() ? "!!! ПРЕВЫШЕН" : "В норме";
                double percentage = (budget.getSpent() / budget.getLimit()) * 100;
                System.out.printf("  %-20s%n", budget.getCategory().getName() + ":");
                System.out.printf("    Лимит:     %,15.2f%n", budget.getLimit());
                System.out.printf("    Потрачено: %,15.2f (%5.1f%%)%n",
                        budget.getSpent(), percentage);
                System.out.printf("    Осталось:  %,15.2f%n", budget.getRemaining());
                System.out.printf("    Статус:    %s%n", status);
                System.out.println();
            }
        }
        System.out.println("=".repeat(60));
    }

    public void printTransactions(List<Transaction> transactions) {
        System.out.println("\n" + "=".repeat(80));
        System.out.printf("%-50s%n", "ИСТОРИЯ ТРАНЗАКЦИЙ");
        System.out.println("=".repeat(80));

        if (transactions.isEmpty()) {
            System.out.println("Транзакций нет");
        } else {
            System.out.printf("%-4s %-10s %-20s %-15s %-20s %-15s%n",
                    "№", "Тип", "Категория", "Сумма", "Дата", "Описание");
            System.out.println("-".repeat(80));

            int i = 1;
            for (Transaction t : transactions) {
                String type = t.getType() == TransactionType.INCOME ? "Доход" : "Расход";
                System.out.printf("%-4d %-10s %-20s %,15.2f %-20s %-15s%n",
                        i++,
                        type,
                        t.getCategory().getName(),
                        t.getAmount(),
                        t.getDate().toLocalDate(),
                        t.getDescription());
            }
        }
        System.out.println("=".repeat(80));
    }

    public void printError(String message) {
        System.out.println("\nОШИБКА: " + message);
    }

    public void printSuccess(String message) {
        System.out.println("\nУСПЕХ: " + message);
    }

    public void printInfo(String message) {
        System.out.println("\nИНФО: " + message);
    }
}