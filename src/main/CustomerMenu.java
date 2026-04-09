package main;

import java.util.List;
import java.util.Scanner;

public class CustomerMenu {

    protected static final int CUSTOMER_EXIT_SELECTION = 10;
    protected static final int CUSTOMER_MAX_SELECTION = 10;

    protected final Scanner keyboardInput;
    protected final Bank bank;
    protected Customer customer;

    public CustomerMenu(Bank bank, Customer customer) {
        this(bank, new Scanner(System.in), customer);
    }

    public CustomerMenu(Bank bank, Scanner keyboardInput, Customer customer) {
        this.bank = bank;
        this.keyboardInput = keyboardInput;
        this.customer = customer;
    }

    public boolean canRunAdminOperations() {
        return false;
    }

    public boolean canRunCustomerOperations() {
        return true;
    }

    public void run() {
        int selection = -1;
        while (selection != CUSTOMER_EXIT_SELECTION) {
            displayOptions();
            selection = getUserSelection(CUSTOMER_MAX_SELECTION);
            processInput(selection);
        }
    }

    public void displayOptions() {
        System.out.println("Customer Menu:");
        System.out.println("1. Make a deposit");
        System.out.println("2. Make a withdrawal");
        System.out.println("3. Check account balance");
        System.out.println("4. Check transaction history");
        System.out.println("5. Create an additional account");
        System.out.println("6. Close an existing account");
        System.out.println("7. Transfer money between your accounts");
        System.out.println("8. Manage recurring payments");
        System.out.println("9. Update personal information");
        System.out.println("10. Back to main menu");
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
                    manageRecurringPayments();
                    break;
                case 9:
                    updatePersonalInformation();
                    break;
                case 10:
                    System.out.println("Leaving customer menu.");
                    break;
                default:
                    System.out.println("Invalid selection.");
                    break;
            }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void setCustomer(Customer customer) {
        this.customer = customer;
    }

    protected int getUserSelection(int max) {
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

    public void performDeposit() {
        BankAccount account = selectAccount("deposit into");
        double depositAmount = readPositiveAmount("How much would you like to deposit: ");
        account.deposit(depositAmount);
        System.out.println("Deposit successful.");
    }

    public void performWithdrawal() {
        BankAccount account = selectAccount("withdraw from");
        double withdrawAmount = readPositiveAmount("How much would you like to withdraw: ");
        try {
            account.withdraw(withdrawAmount);
            System.out.println("Withdrawal successful.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void displayBalance() {
        BankAccount account = selectAccount("check balance for");
        String status = account.isFrozen() ? "Frozen" : "Active";
        System.out.println("Your current balance is: " + account.getBalance());
        System.out.println("Account status: " + status);
        System.out.println("Maximum withdrawal amount is: " + account.getMaxWithdrawAmount());
    }

    public void displayTransactionHistory() {
        BankAccount account = selectAccount("view transaction history for");
        System.out.println("Transaction History:");
        String history = account.getTransactionHistory();
        System.out.println(history.isEmpty() ? "No transactions yet." : history);
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

    public void setMaximumWithdrawalAmount() {
        BankAccount account = selectAccount("set maximum withdrawal amount for");
        double maxAmount = readPositiveAmount("Enter the maximum withdrawal amount: ");
        try {
            account.setMaxWithdrawAmount(maxAmount);
            System.out.println("Maximum withdrawal amount updated successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void displayRemainingFees(BankAccount account) {
        List<Fee> fees = account.getRemainingFees();
        for (int i = 0; i < fees.size(); i++) {
            Fee fee = fees.get(i);
            System.out.println("You have " + fee.getAmount() + " due for " + fee.getDescription() + " on " + fee.getDueDate() + ".");
        }
    }

    public void manageRecurringPayments() {
        new RecurringPaymentMenu(keyboardInput, customer).run();
    }

    public void updatePersonalInformation() {
        keyboardInput.skip("\\R?");
        String address = readRequiredText("Enter your address: ");
        String phoneNumber = readRequiredText("Enter your phone number: ");
        String email = readRequiredText("Enter your email: ");

        customer.updatePersonalInformation(address, phoneNumber, email);
        System.out.println("Personal information updated successfully.");
    }

    protected BankAccount selectAccount(String action) {
        List<BankAccount> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            throw new IllegalStateException("No accounts available to " + action + ".");
        }

        System.out.println("Select account to " + action + ":");
        for (int i = 0; i < accounts.size(); i++) {
            BankAccount account = accounts.get(i);
            String status = account.isFrozen() ? "Frozen" : "Active";
            System.out.println((i + 1) + ". Account #" + (i + 1)
                    + " (Balance: " + account.getBalance()
                    + ", Status: " + status
                    + ", Max Withdraw: " + account.getMaxWithdrawAmount() + ")");
        }

        int selectedAccount = getUserSelection(accounts.size());
        BankAccount account = accounts.get(selectedAccount - 1);
        displayRemainingFees(account);
        return account;
    }

    protected double readPositiveAmount(String prompt) {
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

    protected String readRequiredText(String prompt) {
        String value = "";
        while (value.trim().isEmpty()) {
            System.out.print(prompt);
            value = keyboardInput.nextLine();
        }
        return value.trim();
    }
}
