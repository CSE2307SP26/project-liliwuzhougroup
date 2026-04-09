package main;

import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private static final int EXIT_SELECTION = 11;
    private static final int MAX_SELECTION = 11;

    private final Scanner keyboardInput;
    private final Bank bank;

    public MainMenu() {
        this.keyboardInput = new Scanner(System.in);
        this.bank = BankDataStore.loadBank();
        if (this.bank.getCustomers().isEmpty()) {
            this.bank.addCustomer(new Customer("Default User"));
        }
    }

    public void displayOptions() {
        System.out.println("Welcome to the 237 Bank App!");
        System.out.println("1. Make a deposit");
        System.out.println("2. Make a withdrawal");
        System.out.println("3. Check account balance");
        System.out.println("4. Check transaction history");
        System.out.println("5. Create an additional account");
        System.out.println("6. Close an existing account");
        System.out.println("7. Transfer money between your accounts");
        System.out.println("8. Admin: Collect fee");
        System.out.println("9. Admin: Add interest payment");
        System.out.println("10. Set password or PIN");
        System.out.println("11. Exit the app");
    }

    public int getUserSelection(int max) {
        int selection = -1;
        while (selection < 1 || selection > max) {
            System.out.print("Please make a selection: ");
            if (keyboardInput.hasNextInt()) {
                selection = keyboardInput.nextInt();
            } else {
                keyboardInput.next();
            }
        }
        return selection;
    }

    public void processInput(int selection) {
        try {
            switch (selection) {
                case 1:
                    runCustomerMenu();
                    break;
                case 2:
                    runAdminMenu();
                    break;
                case 3:
                    displayBalance();
                    break;
                case 4:
                    displayTransactionHistory();
                    break;
                case 5:
                    createAdditionalAccount();
                    break;
                case 6:
                    closeExistingAccount();
                    break;
                case 7:
                    transferBetweenAccounts();
                    break;
                case 8:
                    collectFee();
                    break;
                case 9:
                    addInterest();
                    break;
                case 10:
                    setPasswordOrPin();
                    break;
                case 11:
                    System.out.println("Thank you for using the 237 Bank App!");
                    break;
                default:
                    System.out.println("Invalid selection.");
                    break;
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        int selection = -1;
        while (selection != EXIT_SELECTION) {
            displayOptions();
            selection = getUserSelection(MAX_SELECTION);
            processInput(selection);
        }
        saveData();
    }

    private void runCustomerMenu() {
        Customer selectedCustomer = selectCustomer();
        new CustomerMenu(bank, keyboardInput, selectedCustomer).run();
    }

    private void runAdminMenu() {
        System.out.print("Enter admin password: ");
        String password = keyboardInput.next();
        if (!AdminAuth.isValidPassword(password)) {
            System.out.println("Invalid admin password.");
            return;
        }
        Customer defaultCustomer = bank.getCustomers().get(0);
        new AdminMenu(bank, keyboardInput, defaultCustomer).run();
    }

    private Customer selectCustomer() {
        System.out.println("Select customer:");
        for (int i = 0; i < bank.getCustomers().size(); i++) {
            System.out.println((i + 1) + ". " + bank.getCustomers().get(i).getName());
        }
        int selectedCustomer = getUserSelection(bank.getCustomers().size());
        return bank.getCustomers().get(selectedCustomer - 1);
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
        Customer selected = customers.get(getUserSelection(customers.size()) - 1);

    public void setPasswordOrPin() {
        keyboardInput.nextLine();
        System.out.println("1. Set password");
        System.out.println("2. Set PIN");
        int choice = getUserSelection(2);
        keyboardInput.nextLine();

        if (choice == 1) {
            System.out.print("Enter your new password: ");
            String password = keyboardInput.nextLine();
            customer.setPassword(password);
            System.out.println("Password set successfully.");
        } else {
            System.out.print("Enter your 4-digit PIN: ");
            String pin = keyboardInput.nextLine();
            customer.setPin(pin);
            System.out.println("PIN set successfully.");
        }
    }

    private BankAccount selectAccount(String action) {
        List<BankAccount> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            throw new IllegalStateException("No accounts available to " + action + ".");
        }
        System.out.println("Select account to " + action + ":");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.println((i + 1) + ". Account #" + (i + 1) + " (Balance: " + accounts.get(i).getBalance() + ")");
        }
        BankAccount account = accounts.get(getUserSelection(accounts.size()) - 1);

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
