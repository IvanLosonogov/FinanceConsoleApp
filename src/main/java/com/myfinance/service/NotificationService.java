package com.myfinance.service;

import com.myfinance.model.Budget;
import com.myfinance.model.Wallet;
import java.util.List;

public class NotificationService {

    public void checkNotifications(Wallet wallet) {
        checkBalance(wallet);
        checkBudgets(wallet);
    }

    private void checkBalance(Wallet wallet) {
        if (wallet.getBalance() < 0) {
            System.out.println("ВНИМАНИЕ: Расходы превышают доходы!");
            System.out.println("Текущий баланс: " + wallet.getBalance());
        }

        if (wallet.getBalance() < 1000) {
            System.out.println("Информация: Баланс ниже 1000");
        }
    }

    private void checkBudgets(Wallet wallet) {
        List<Budget> budgets = wallet.getAllBudgets();
        for (Budget budget : budgets) {
            double remaining = budget.getRemaining();
            double spentPercentage = (budget.getSpent() / budget.getLimit()) * 100;

            if (budget.isExceeded()) {
                System.out.println("ПРЕВЫШЕНИЕ БЮДЖЕТА: " +
                        budget.getCategory().getName() +
                        " превышен на " + Math.abs(remaining));
            } else if (spentPercentage >= 80) {
                System.out.println("ПРЕДУПРЕЖДЕНИЕ: Бюджет " +
                        budget.getCategory().getName() +
                        " израсходован на " + String.format("%.1f", spentPercentage) + "%");
            }
        }
    }
}