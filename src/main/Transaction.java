package main;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public final class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDate date;
    private final String description;
    private final double amount;

    public Transaction(LocalDate date, String description, double amount) {
        this.date = Objects.requireNonNull(date, "date cannot be null.");
        this.description = Objects.requireNonNull(description, "description cannot be null.");
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return date + " | " + description + " | $" + amount;
    }
}
