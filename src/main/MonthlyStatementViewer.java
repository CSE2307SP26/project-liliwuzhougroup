package main;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Scanner;

public class MonthlyStatementViewer {

    private final MenuInput io;
    private final BankAccount account;

    public MonthlyStatementViewer(Scanner scanner, BankAccount account) {
        this.io = new MenuInput(scanner);
        this.account = account;
    }

    public void viewMonthlyStatement() {
        io.prepareForTextInput();
        System.out.print("Enter year (e.g., 2024): ");
        int year;
        try {
            year = Integer.parseInt(io.readRequiredText("").trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid year.");
            return;
        }

        System.out.println("Enter month (1-12): ");
        int month = io.readSelection(12);

        List<Transaction> transactions = account.getTransactionsByYearMonth(year, month);
        String monthLabel = Month.of(month).name().charAt(0)
                + Month.of(month).name().substring(1).toLowerCase()
                + " " + year;

        if (transactions.isEmpty()) {
            System.out.println("No transactions found for " + monthLabel + ".");
            return;
        }

        System.out.println("Statement for " + monthLabel + ":");
        System.out.println("--------------------------------------------");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
        System.out.println("--------------------------------------------");

        double totalCredits = 0;
        double totalDebits = 0;
        for (Transaction t : transactions) {
            if (t.getAmount() >= 0) {
                totalCredits += t.getAmount();
            } else {
                totalDebits += Math.abs(t.getAmount());
            }
        }
        System.out.println("Total credits:  " + totalCredits);
        System.out.println("Total debits:   " + totalDebits);
        System.out.println("Net change:     " + (totalCredits - totalDebits));
    }
}

class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    private final LocalDate date;
    private final String description;
    private final double amount;

    Transaction(LocalDate date, String description, double amount) {
        this.date = date;
        this.description = description;
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
