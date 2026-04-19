package main;

import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private static final int EXIT_SELECTION = 3;
    private static final int MAX_SELECTION = 3;

    private final Scanner keyboardInput;
    private final MenuInput io;
    private final Bank bank;

    public MainMenu() {
        this.keyboardInput = new Scanner(System.in);
        this.io = new MenuInput(keyboardInput);
        this.bank = BankDataStore.loadBank();
    }

    public void displayOptions() {
        System.out.println("Welcome to the 237 Bank App!");
        System.out.println("1. Log in as customer");
        System.out.println("2. Log in as admin");
        System.out.println("3. Exit the app");
    }

    public void processInput(int selection) {
        try {
            if (selection == 1) {
                runCustomerMenu();
                return;
            }
            if (selection == 2) {
                runAdminMenu();
                return;
            }
            System.out.println("Thank you for using the 237 Bank App!");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        int selection = -1;
        while (selection != EXIT_SELECTION) {
            displayOptions();
            selection = io.readSelection(MAX_SELECTION);
            processInput(selection);
        }
        saveData();
    }

    private void runCustomerMenu() {
        new CustomerAccessPortal(bank, keyboardInput).run();
    }

    private void runAdminMenu() {
        System.out.print("Enter admin password: ");
        String password = keyboardInput.next();
        if (!AdminAuth.isValidPassword(password)) {
            System.out.println("Invalid admin password.");
            return;
        }
        Customer adminContext = bank.getCustomers().isEmpty() ? null : bank.getCustomers().get(0);
        new AdminMenu(bank, keyboardInput, adminContext).run();
    }

    public void viewAdminTransactionHistory() {
        List<Customer> customers = bank.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
            return;
        }
        System.out.println("Select a customer:");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }
        Customer selected = customers.get(io.readSelection(customers.size()) - 1);

        List<BankAccount> accounts = selected.getAccounts();
        System.out.println("Select an account:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println((i + 1) + ". Account #" + (i + 1) + " (Balance: " + accounts.get(i).getBalance() + ")");
        }
        BankAccount account = accounts.get(io.readSelection(accounts.size()) - 1);

        String history = account.getTransactionHistory();
        System.out.println("Transaction History:");
        System.out.println(history.isEmpty() ? "No transactions yet." : history);
    }

    private void saveData() {
        try {
            BankDataStore.saveBank(bank);
            System.out.println("Data saved.");
        } catch (RuntimeException e) {
            System.out.println("Warning: Could not save data.");
        }
    }

    public static void main(String[] args) {
        MainMenu bankApp = new MainMenu();
        bankApp.run();
    }
}
