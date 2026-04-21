package main;

import java.util.Scanner;

public class MainMenu {
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
        MenuScreen.redraw();
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
            AppExit.request();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        try {
            while (true) {
                displayOptions();
                processInput(io.readSelection(MAX_SELECTION));
            }
        } catch (AppExit.Requested e) {
            // Exit requested from any menu level.
        } finally {
            saveData();
        }
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
