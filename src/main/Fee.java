package main;

import java.util.Date;

public class Fee {
    private double amount;
    private String description;
    private Date dueDate;

    public Fee(double amount, String description, Date dueDate) {
        if (amount < 0) {
            throw new IllegalArgumentException("Fee amount cannot be negative.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Fee description cannot be blank.");
        }
        if (dueDate == null) {
            throw new IllegalArgumentException("Due date is required.");
        }
        this.amount = amount;
        this.description = description.trim();
        if (dueDate.before(new Date())) {
            throw new IllegalArgumentException("Due date cannot be in the past.");
        }
        this.dueDate = dueDate;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDate() {
        return dueDate;
    }
}
