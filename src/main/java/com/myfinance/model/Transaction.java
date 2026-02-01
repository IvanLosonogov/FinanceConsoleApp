package com.myfinance.model;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Transaction {
    private final String id;
    private final double amount;
    private final Category category;
    private final LocalDateTime date;
    private final String description;

    protected Transaction(double amount, Category category, String description) {
        this.id = UUID.randomUUID().toString();
        this.amount = amount;
        this.category = category;
        this.date = LocalDateTime.now();
        this.description = description != null ? description : "";
    }

    public abstract TransactionType getType();

    public String getId() { return id; }
    public double getAmount() { return amount; }
    public Category getCategory() { return category; }
    public LocalDateTime getDate() { return date; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("%s: %.2f (%s) - %s",
                category.getName(), amount, date.toLocalDate(), description);
    }
}