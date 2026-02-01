package com.myfinance.model;

public class Budget {
    private Category category;
    private double limit;
    private double spent;

    public Budget(Category category, double limit) {
        this.category = category;
        this.limit = limit;
        this.spent = 0.0;
    }

    public void addSpent(double amount) {
        spent += amount;
    }

    public double getRemaining() {
        return limit - spent;
    }

    public boolean isExceeded() {
        return spent > limit;
    }

    public Category getCategory() { return category; }
    public double getLimit() { return limit; }
    public double getSpent() { return spent; }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return String.format("%s: Лимит=%.2f, Потрачено=%.2f, Осталось=%.2f",
                category.getName(), limit, spent, getRemaining());
    }
}