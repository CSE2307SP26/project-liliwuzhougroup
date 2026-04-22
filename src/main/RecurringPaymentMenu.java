package main;

import java.util.List;
import java.util.Scanner;

public class RecurringPaymentMenu {
    private static final int BACK_SELECTION = 5;
    private static final int EXIT_SELECTION = 6;
    private static final int MAX_SELECTION = 6;
    private static final String SETUP_REQUIRES_TWO_ACCOUNTS_MESSAGE =
            "Recurring payments require at least two accounts. "
                    + "Create another account from the customer dashboard first.";

    private final MenuInput io;
    private final Customer customer;

    public RecurringPaymentMenu(Scanner scanner, Customer customer) {
        this.io = new MenuInput(scanner);
        this.customer = customer;
    }

    public void run() {
        int selection = -1;
        while (selection != BACK_SELECTION) {
            displayOptions();
            selection = io.readSelection(MAX_SELECTION);
            processInput(selection);
        }
    }

    public void displayOptions() {
        MenuScreen.redraw();
        System.out.println("\n--- Recurring Payments ---");
        if (canSetupPayment()) {
            System.out.println("1. Set up a new recurring payment");
        } else {
            System.out.println("1. Set up a new recurring payment (requires at least 2 accounts)");
        }
        System.out.println("2. View all recurring payments");
        System.out.println("3. Process all recurring payments now");
        System.out.println("4. Cancel a recurring payment");
        System.out.println("5. Back to previous menu");
        System.out.println("6. Exit the app");
    }

    public void processInput(int selection) {
        try {
            if (selection == BACK_SELECTION) {
                System.out.println("Returning to previous menu.");
                return;
            }
            if (selection == EXIT_SELECTION) {
                MainMenu.requestExit();
                return;
            }
            if (selection == 1 && !canSetupPayment()) {
                System.out.println(SETUP_REQUIRES_TWO_ACCOUNTS_MESSAGE);
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

    private boolean canSetupPayment() {
        return customer.getAccounts().size() >= 2;
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
