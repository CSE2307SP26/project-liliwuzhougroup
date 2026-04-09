package main;

import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private static final int EXIT_SELECTION = 10;
    private static final int MAX_SELECTION = 10;

    private final Scanner keyboardInput;
    private final Customer customer;
    private final Bank bank;

    public MainMenu() {
        this.keyboardInput = new Scanner(System.in);
        this.bank = BankDataStore.loadBank();
        if (this.bank.getCustomers().isEmpty()) {
            this.customer = new Customer("Default User");
            this.bank.addCustomer(this.customer);
        } else {
            this.customer = this.bank.getCustomers().get(0);
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
        System.out.println("10. Exit the app");
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
                    performDeposit();
                    break;
                case 2:
                    performWithdrawal();
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
                    System.out.println("Thank you for using the 237 Bank App!");
                    break;
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void performDeposit() {
        BankAccount account = selectAccount("deposit into");
        double depositAmount = readPositiveAmount("How much would you like to deposit: ");
        account.deposit(depositAmount);
        System.out.println("Deposit successful.");
    }

    public void displayTransactionHistory() {
        BankAccount account = selectAccount("view transaction history for");
        System.out.println("Transaction History:");
        String history = account.getTransactionHistory();
        System.out.println(history.isEmpty() ? "No transactions yet." : history);
    }
    
    public void performWithdrawal() {
        BankAccount account = selectAccount("withdraw from");
        double withdrawAmount = readPositiveAmount("How much would you like to withdraw: ");

        try {
            account.withdraw(withdrawAmount);
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
        saveData();
    }

    public void displayBalance() {
        BankAccount account = selectAccount("check balance for");
        System.out.println("Your current balance is: " + account.getBalance());
    }

    public void createAdditionalAccount() {
        customer.openAccount();
        System.out.println("Account created successfully.");
    }

    public void closeExistingAccount() {
        BankAccount account = selectAccount("close");
        try {
            customer.closeAccount(account);
            System.out.println("Account closed successfully.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void transferBetweenAccounts() {
        System.out.println("Select source account:");
        BankAccount sourceAccount = selectAccount("transfer money from");
        System.out.println("Select target account:");
        BankAccount targetAccount = selectAccount("transfer money to");
        double amount = readPositiveAmount("How much would you like to transfer: ");

        try {
            sourceAccount.transferMoney(targetAccount, amount);
            System.out.println("Transfer successful.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void collectFee() {
        BankAccount account = selectAccount("collect fee from");
        double feeAmount = readPositiveAmount("Enter fee amount: ");
        try {
            bank.collectFee(account, feeAmount);
            System.out.println("Fee collected successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid fee amount.");
        }
    }

    public void addInterest() {
        BankAccount account = selectAccount("add interest to");
        double interestAmount = readPositiveAmount("Enter interest amount: ");
        try {
            bank.addInterest(account, interestAmount);
            System.out.println("Interest added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid interest amount.");
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
        int selectedAccount = getUserSelection(accounts.size());
        BankAccount account = accounts.get(selectedAccount - 1);
        displayRemainingFees(account);
        return account;
    }

    public void displayRemainingFees(BankAccount account) {
        List<Fee> fees = account.getRemainingFees();
        for (int i = 0; i < fees.size(); i++) {
            Fee fee = fees.get(i);
            System.out.println("You have " + fee.getAmount() + " due for " + fee.getDescription() + " on " + fee.getDueDate() + ".");
        }
    }

    private double readPositiveAmount(String prompt) {
        double amount = -1;
        while (amount <= 0) {
            System.out.print(prompt);
            if (keyboardInput.hasNextDouble()) {
                amount = keyboardInput.nextDouble();
            } else {
                keyboardInput.next();
            }
        }
        return amount;
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