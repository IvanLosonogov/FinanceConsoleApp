package com.myfinance.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {

    public static boolean isValidAmount(String input) {
        try {
            double amount = Double.parseDouble(input);
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static boolean isValidUsername(String username) {
        return username != null &&
                username.length() >= 3 &&
                username.matches("^[a-zA-Z0-9_]+$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 3;
    }
}