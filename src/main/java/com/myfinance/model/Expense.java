package com.myfinance.model;

public class Expense extends Transaction {
    public Expense(double amount, Category category, String description) {
        super(amount, category, description);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.EXPENSE;
    }
}