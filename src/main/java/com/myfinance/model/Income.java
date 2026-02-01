package com.myfinance.model;

public class Income extends Transaction {
    public Income(double amount, Category category, String description) {
        super(amount, category, description);
    }

    @Override
    public TransactionType getType() {
        return TransactionType.INCOME;
    }
}