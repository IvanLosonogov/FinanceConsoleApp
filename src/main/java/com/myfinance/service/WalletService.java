package com.myfinance.service;

import com.myfinance.model.*;
import com.myfinance.repository.WalletRepository;

import java.io.IOException;
import java.util.List;

public class WalletService {
    private WalletRepository walletRepository;
    private Wallet currentWallet;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public void createWalletForUser(String userId) throws IOException {
        Wallet wallet = new Wallet(userId + "_wallet", userId);
        try {
            walletRepository.save(wallet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        currentWallet = wallet;
    }

    public void loadWallet(String userId) {
        currentWallet = walletRepository.findByUserId(userId);
        if (currentWallet == null) {
            try {
                createWalletForUser(userId);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Wallet getCurrentWallet() {
        return currentWallet;
    }

    public void addIncome(double amount, String categoryName, String description) {
        Category category = currentWallet.getCategory(categoryName, TransactionType.INCOME);
        if (category == null) {
            category = new Category(categoryName, TransactionType.INCOME);
            currentWallet.addCategory(category);
        }

        Income income = new Income(amount, category, description);
        currentWallet.addTransaction(income);
    }

    public void addExpense(double amount, String categoryName, String description) {
        Category category = currentWallet.getCategory(categoryName, TransactionType.EXPENSE);
        if (category == null) {
            category = new Category(categoryName, TransactionType.EXPENSE);
            currentWallet.addCategory(category);
        }

        Expense expense = new Expense(amount, category, description);
        currentWallet.addTransaction(expense);
    }

    public void setBudget(String categoryName, double limit) {
        Category category = currentWallet.getCategory(categoryName, TransactionType.EXPENSE);
        if (category == null) {
            category = new Category(categoryName, TransactionType.EXPENSE);
            currentWallet.addCategory(category);
        }
        currentWallet.setBudget(category, limit);
    }

    public List<Transaction> getTransactions() {
        return currentWallet.getTransactions();
    }

    public List<Budget> getBudgets() {
        return currentWallet.getAllBudgets();
    }

    public void saveWallet() {
        if (currentWallet != null) {
            try {
                walletRepository.save(currentWallet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public double getExpenseByCategories(List<String> categoryNames) {
        return currentWallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .filter(t -> categoryNames.contains(t.getCategory().getName()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getIncomeByCategories(List<String> categoryNames) {
        return currentWallet.getTransactions().stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .filter(t -> categoryNames.contains(t.getCategory().getName()))
                .mapToDouble(Transaction::getAmount)
                .sum();
    }
}