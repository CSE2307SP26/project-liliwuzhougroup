package main;

import java.util.List;
import java.util.Scanner;

public class RecurringPaymentMenu {
    private static final int EXIT_SELECTION = 5;
    private static final int MAX_SELECTION = 5;

    private final MenuInput io;
    private final Customer customer;

    public RecurringPaymentMenu(Scanner scanner, Customer customer) {
        this.io = new MenuInput(scanner);
        this.customer = customer;
    }

    public void run() {
        int selection = -1;
        while (selection != EXIT_SELECTION) {
            displayOptions();
            selection = io.readSelection(MAX_SELECTION);
            processInput(selection);
        }
    }

    public void displayOptions() {
        System.out.println("\n--- Recurring Payments ---");
        System.out.println("1. Set up a new recurring payment");
        System.out.println("2. View all recurring payments");
        System.out.println("3. Process all recurring payments now");
        System.out.println("4. Cancel a recurring payment");
        System.out.println("5. Back to main menu");
    }

    public void processInput(int selection) {
        try {
            if (selection == EXIT_SELECTION) {
                System.out.println("Leaving recurring payment menu.");
                return;
            }
            if (runPaymentAction(selection)) {
                return;
            }
            System.out.println("Invalid selection.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setupPayment() {
        List<BankAccount> accounts = customer.getAccounts();
        if (accounts.size() < 2) {
            throw new IllegalStateException("At least two accounts are required for recurring payments.");
        }
        io.prepareForTextInput();
        String description = io.readRequiredText("Enter a description: ");
        int sourceIndex = selectAccountIndex(accounts, "Select source account:");
        int targetIndex = selectTargetIndex(accounts, sourceIndex);
        double amount = io.readPositiveAmount("Enter amount: ");
        customer.setupRecurringPayment(description, sourceIndex, targetIndex, amount, readFrequency());
        System.out.println("Recurring payment set up successfully.");
    }

    public void viewPayments() {
        List<RecurringPayment> payments = customer.getRecurringPayments();
        if (payments.isEmpty()) {
            System.out.println("No recurring payments set up.");
            return;
        }
        for (int i = 0; i < payments.size(); i++) {
            System.out.println((i + 1) + ". " + payments.get(i));
        }
    }

    public void processAllPayments() {
        if (customer.getRecurringPayments().isEmpty()) {
            System.out.println("No recurring payments to process.");
            return;
        }
        int processedCount = customer.processRecurringPayments();
        if (processedCount == 0) {
            System.out.println("No recurring payments are due today.");
            return;
        }
        System.out.println(processedCount + " recurring payment(s) processed.");
    }

    public void cancelPayment() {
        if (customer.getRecurringPayments().isEmpty()) {
            throw new IllegalStateException("No recurring payments to cancel.");
        }
        viewPayments();
        int index = io.readSelection(customer.getRecurringPayments().size()) - 1;
        customer.cancelRecurringPayment(index);
        System.out.println("Recurring payment cancelled.");
    }

    private boolean runPaymentAction(int selection) {
        switch (selection) {
            case 1: setupPayment(); return true;
            case 2: viewPayments(); return true;
            case 3: processAllPayments(); return true;
            case 4: cancelPayment(); return true;
            default: return false;
        }
    }

    private int selectAccountIndex(List<BankAccount> accounts, String prompt) {
        System.out.println(prompt);
        printAccounts(accounts);
        return io.readSelection(accounts.size()) - 1;
    }

    private int selectTargetIndex(List<BankAccount> accounts, int sourceIndex) {
        int targetIndex = selectAccountIndex(accounts, "Select target account:");
        while (targetIndex == sourceIndex) {
            System.out.println("Source and target accounts must be different.");
            targetIndex = selectAccountIndex(accounts, "Select target account:");
        }
        return targetIndex;
    }

    private void printAccounts(List<BankAccount> accounts) {
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println("  " + (i + 1) + ". Account #" + (i + 1)
                    + " (Balance: " + accounts.get(i).getBalance() + ")");
        }
    }

    private RecurringPayment.Frequency readFrequency() {
        System.out.println("Select frequency: 1=Daily  2=Weekly  3=Monthly");
        RecurringPayment.Frequency[] frequencies = RecurringPayment.Frequency.values();
        return frequencies[io.readSelection(frequencies.length) - 1];
    }
}
