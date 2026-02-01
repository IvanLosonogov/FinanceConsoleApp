package com.myfinance.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
    private String id;
    private String userId;
    private double balance;
    private List<Transaction> transactions;
    private Map<String, Budget> budgets;
    private Map<String, Category> categories;

    public Wallet(String id, String userId) {
        this.id = id;
        this.userId = userId;
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
        this.budgets = new HashMap<>();
        this.categories = new HashMap<>();
        initializeDefaultCategories();
    }

    private void initializeDefaultCategories() {
        addCategory(new Category("Зарплата", TransactionType.INCOME));
        addCategory(new Category("Бонус", TransactionType.INCOME));
        addCategory(new Category("Еда", TransactionType.EXPENSE));
        addCategory(new Category("Развлечения", TransactionType.EXPENSE));
        addCategory(new Category("Коммунальные услуги", TransactionType.EXPENSE));
        addCategory(new Category("Транспорт", TransactionType.EXPENSE));
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);

        if (transaction.getType() == TransactionType.INCOME) {
            balance += transaction.getAmount();
        } else {
            balance -= transaction.getAmount();

            Budget budget = budgets.get(transaction.getCategory().getName());
            if (budget != null) {
                budget.addSpent(transaction.getAmount());
            }
        }
    }

    public void addCategory(Category category) {
        categories.put(category.getName() + "_" + category.getType(), category);
    }

    public Category getCategory(String name, TransactionType type) {
        return categories.get(name + "_" + type);
    }

    public List<Category> getAllCategories() {
        return new ArrayList<>(categories.values());
    }

    public void setBudget(Category category, double limit) {
        budgets.put(category.getName(), new Budget(category, limit));
    }

    public Budget getBudget(String categoryName) {
        return budgets.get(categoryName);
    }

    public List<Budget> getAllBudgets() {
        return new ArrayList<>(budgets.values());
    }

    public double getTotalIncome() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public double getTotalExpense() {
        return transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public Map<String, Double> getIncomeByCategory() {
        Map<String, Double> result = new HashMap<>();
        transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .forEach(t -> result.merge(t.getCategory().getName(),
                        t.getAmount(), Double::sum));
        return result;
    }

    public Map<String, Double> getExpenseByCategory() {
        Map<String, Double> result = new HashMap<>();
        transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .forEach(t -> result.merge(t.getCategory().getName(),
                        t.getAmount(), Double::sum));
        return result;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return new ArrayList<>(transactions); }
}