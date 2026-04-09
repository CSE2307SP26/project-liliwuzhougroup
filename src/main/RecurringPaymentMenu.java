package main;

import java.util.List;
import java.util.Scanner;

public class RecurringPaymentMenu {

    private final Scanner scanner;
    private final Customer customer;

    public RecurringPaymentMenu(Scanner scanner, Customer customer) {
        this.scanner = scanner;
        this.customer = customer;
    }

    public void run() {
        int sel = -1;
        while (sel != 5) {
            System.out.println("\n--- Recurring Payments ---");
            System.out.println("1. Set up a new recurring payment");
            System.out.println("2. View all recurring payments");
            System.out.println("3. Process all recurring payments now");
            System.out.println("4. Cancel a recurring payment");
            System.out.println("5. Back to main menu");
            sel = getUserSelection(5);
            if (sel == 1) setupPayment();
            else if (sel == 2) viewPayments();
            else if (sel == 3) processAllPayments();
            else if (sel == 4) cancelPayment();
        }
    }

    private void setupPayment() {
        List<BankAccount> accounts = customer.getAccounts();
        if (accounts.size() < 2) {
            System.out.println("You need at least 2 accounts to set up a recurring payment.");
            return;
        }
        System.out.print("Enter a description: ");
        scanner.nextLine();
        String description = scanner.nextLine().trim();

        System.out.println("Select source account:");
        printAccounts(accounts);
        int sourceIndex = getUserSelection(accounts.size()) - 1;

        System.out.println("Select target account:");
        printAccounts(accounts);
        int targetIndex = getUserSelection(accounts.size()) - 1;

        if (sourceIndex == targetIndex) {
            System.out.println("Source and target accounts must be different.");
            return;
        }

        double amount = readPositiveAmount("Enter amount: ");

        System.out.println("Select frequency: 1=Daily  2=Weekly  3=Monthly");
        RecurringPayment.Frequency[] freqs = RecurringPayment.Frequency.values();
        RecurringPayment.Frequency frequency = freqs[getUserSelection(3) - 1];

        try {
            customer.setupRecurringPayment(description, sourceIndex, targetIndex, amount, frequency);
            System.out.println("Recurring payment set up successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void viewPayments() {
        List<RecurringPayment> payments = customer.getRecurringPayments();
        if (payments.isEmpty()) {
            System.out.println("No recurring payments set up.");
            return;
        }
        for (int i = 0; i < payments.size(); i++) {
            System.out.println((i + 1) + ". " + payments.get(i));
        }
    }

    private void processAllPayments() {
        if (customer.getRecurringPayments().isEmpty()) {
            System.out.println("No recurring payments to process.");
            return;
        }
        try {
            int processedCount = customer.processRecurringPayments();
            if (processedCount == 0) {
                System.out.println("No recurring payments are due today.");
            } else {
                System.out.println(processedCount + " recurring payment(s) processed.");
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            System.out.println("A payment failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Recurring payment could not be processed: " + e.getMessage());
        }
    }

    private void cancelPayment() {
        if (customer.getRecurringPayments().isEmpty()) {
            System.out.println("No recurring payments to cancel.");
            return;
        }
        viewPayments();
        System.out.println("Enter the number of the payment to cancel:");
        int index = getUserSelection(customer.getRecurringPayments().size()) - 1;
        customer.cancelRecurringPayment(index);
        System.out.println("Recurring payment cancelled.");
    }

    private void printAccounts(List<BankAccount> accounts) {
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println("  " + (i + 1) + ". Account #" + (i + 1)
                    + " (Balance: " + accounts.get(i).getBalance() + ")");
        }
    }

    private int getUserSelection(int max) {
        int selection = -1;
        while (selection < 1 || selection > max) {
            System.out.print("Please make a selection: ");
            if (scanner.hasNextInt()) {
                selection = scanner.nextInt();
            } else {
                scanner.next();
            }
        }
        return selection;
    }

    private double readPositiveAmount(String prompt) {
        double amount = -1;
        while (amount <= 0) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                amount = scanner.nextDouble();
            } else {
                scanner.next();
            }
        }
        return amount;
    }
}
