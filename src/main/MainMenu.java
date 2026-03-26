package main;

import java.util.Scanner;

public class MainMenu {

    private static final int EXIT_SELECTION = 3;
    private static final int MAX_SELECTION = 3;

    private BankAccount userAccount;
    private Scanner keyboardInput;

    public MainMenu() {
        this.userAccount = new BankAccount();
        this.keyboardInput = new Scanner(System.in);
    }

    public void displayOptions() {
        System.out.println("Welcome to the 237 Bank App!");
        System.out.println("1. Make a deposit");
        System.out.println("2. Check transaction history");
        System.out.println("3. Make a withdrawal");
        System.out.println("4. Check balance");
        System.out.println("5. Exit the app");
    }

    public int getUserSelection(int max) {
        int selection = -1;
        while (selection < 1 || selection > max) {
            System.out.print("Please make a selection: ");
            selection = keyboardInput.nextInt();
        }
        return selection;
    }

    public void processInput(int selection) {
        switch (selection) {
            case 1:
                performDeposit();
                break;
            case 2:
                displayTransactionHistory();
                break;
            case 3:
                performWithdrawal();
                break;
            case 4:
                displayBalance();
                break;
            case 5:
                System.out.println("Thank you for using the 237 Bank App!");
                break;
        }
    }

    public void performDeposit() {
        double depositAmount = -1;
        while (depositAmount <= 0) {
            System.out.print("How much would you like to deposit: ");
            depositAmount = keyboardInput.nextDouble();
        }
        userAccount.deposit(depositAmount);
        System.out.println("Deposit successful.");
    }

    public void displayTransactionHistory() {
        System.out.println("Transaction History:");
        System.out.println(userAccount.getTransactionHistory());
    }
    
    public void performWithdrawal() {
        double withdrawAmount = -1;
        while (withdrawAmount <= 0) {
            System.out.print("How much would you like to withdraw: ");
            withdrawAmount = keyboardInput.nextDouble();
        }

        try {
            userAccount.withdraw(withdrawAmount);
            System.out.println("Withdrawal successful.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid withdrawal. Please make sure the amount is greater than 0 and does not exceed your balance.");
        }
    }

    public void run() {
        int selection = -1;
        while (selection != EXIT_SELECTION) {
            displayOptions();
            selection = getUserSelection(MAX_SELECTION);
            processInput(selection);
        }
    }

    public void displayBalance() {
        System.out.println("Your current balance is: " + userAccount.getBalance());
    }

    public static void main(String[] args) {
        MainMenu bankApp = new MainMenu();
        bankApp.run();
    }
}