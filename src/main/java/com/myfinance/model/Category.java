package com.myfinance.model;

import java.util.Objects;

public class Category {
    private String name;
    private TransactionType type;

    public Category(String name, TransactionType type) {
        this.name = name;
        this.type = type;
    }

    public String getName() { return name; }
    public TransactionType getType() { return type; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return name.equals(category.name) && type == category.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}