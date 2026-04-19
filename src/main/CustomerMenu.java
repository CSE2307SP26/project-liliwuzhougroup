package main;

import java.util.List;
import java.util.Scanner;

public class CustomerMenu {

    protected static final int CUSTOMER_EXIT_SELECTION = 11;
    protected static final int CUSTOMER_MAX_SELECTION = 11;

    protected final Scanner keyboardInput;
    protected final Bank bank;
    protected final MenuInput io;
    protected final CustomerAccountSelector accountSelector;
    protected Customer customer;

    public CustomerMenu(Bank bank, Customer customer) {
        this(bank, new Scanner(System.in), customer);
    }

    public CustomerMenu(Bank bank, Scanner keyboardInput, Customer customer) {
        this.bank = bank;
        this.keyboardInput = keyboardInput;
        this.io = new MenuInput(keyboardInput);
        this.accountSelector = new CustomerAccountSelector(io);
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
            selection = io.readSelection(CUSTOMER_MAX_SELECTION);
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
        System.out.println("10. Set password or PIN");
        System.out.println("11. Back to main menu");
    }

    public void processInput(int selection) {
        try {
            if (selection == CUSTOMER_EXIT_SELECTION) {
                System.out.println("Leaving customer menu.");
                return;
            }
            if (processTransactionSelection(selection)) {
                return;
            }
            if (processAccountSelection(selection)) {
                return;
            }
            processProfileSelection(selection);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void performDeposit() {
        BankAccount account = accountSelector.selectAccount(customer, "deposit into");
        double depositAmount = io.readPositiveAmount("How much would you like to deposit: ");
        account.deposit(depositAmount);
        System.out.println("Deposit successful.");
    }

    public void performWithdrawal() {
        BankAccount account = accountSelector.selectAccount(customer, "withdraw from");
        double withdrawAmount = io.readPositiveAmount("How much would you like to withdraw: ");
        try {
            account.withdraw(withdrawAmount);
            System.out.println("Withdrawal successful.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void displayBalance() {
        BankAccount account = accountSelector.selectAccount(customer, "check balance for");
        String status = account.isFrozen() ? "Frozen" : "Active";
        System.out.println("Your current balance is: " + account.getBalance());
        System.out.println("Account status: " + status);
        System.out.println("Maximum withdrawal amount is: " + account.getMaxWithdrawAmount());
    }

    public void displayTransactionHistory() {
        BankAccount account = accountSelector.selectAccount(customer, "view transaction history for");
        System.out.println("Transaction History:");
        String history = account.getTransactionHistory();
        System.out.println(history.isEmpty() ? "No transactions yet." : history);
    }

    public void createAdditionalAccount() {
        customer.openAccount();
        System.out.println("Account created successfully.");
    }

    public void closeExistingAccount() {
        BankAccount account = accountSelector.selectAccount(customer, "close");
        try {
            customer.closeAccount(account);
            System.out.println("Account closed successfully.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void transferBetweenAccounts() {
        System.out.println("Select source account:");
        BankAccount sourceAccount = accountSelector.selectAccount(customer, "transfer money from");
        System.out.println("Select target account:");
        BankAccount targetAccount = accountSelector.selectAccount(customer, "transfer money to");
        double amount = io.readPositiveAmount("How much would you like to transfer: ");

        try {
            sourceAccount.transferMoney(targetAccount, amount);
            System.out.println("Transfer successful.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setMaximumWithdrawalAmount() {
        BankAccount account = accountSelector.selectAccount(customer, "set maximum withdrawal amount for");
        double maxAmount = io.readPositiveAmount("Enter the maximum withdrawal amount: ");
        try {
            account.setMaxWithdrawAmount(maxAmount);
            System.out.println("Maximum withdrawal amount updated successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void manageRecurringPayments() {
        new RecurringPaymentMenu(keyboardInput, customer).run();
    }

    public void updatePersonalInformation() {
        io.prepareForTextInput();
        String address = io.readRequiredText("Enter your address: ");
        String phoneNumber = io.readRequiredText("Enter your phone number: ");
        String email = io.readRequiredText("Enter your email: ");
        customer.updatePersonalInformation(address, phoneNumber, email);
        System.out.println("Personal information updated successfully.");
    }

    public void setPasswordOrPin() {
        io.prepareForTextInput();
        System.out.println("1. Set password");
        System.out.println("2. Set PIN");
        int choice = io.readSelection(2);
        io.prepareForTextInput();
        if (choice == 1) {
            customer.setPassword(io.readRequiredText("Enter your new password: "));
            System.out.println("Password set successfully.");
            return;
        }
        customer.setPin(io.readPin("Enter your 4-digit PIN: "));
        System.out.println("PIN set successfully.");
    }

    private boolean processTransactionSelection(int selection) {
        switch (selection) {
            case 1: performDeposit(); return true;
            case 2: performWithdrawal(); return true;
            case 3: displayBalance(); return true;
            case 4: displayTransactionHistory(); return true;
            default: return false;
        }
    }

    private boolean processAccountSelection(int selection) {
        switch (selection) {
            case 5: createAdditionalAccount(); return true;
            case 6: closeExistingAccount(); return true;
            case 7: transferBetweenAccounts(); return true;
            case 8: manageRecurringPayments(); return true;
            default: return false;
        }
    }

    private void processProfileSelection(int selection) {
        switch (selection) {
            case 9:
                updatePersonalInformation();
                return;
            case 10:
                setPasswordOrPin();
                return;
            default:
                System.out.println("Invalid selection.");
        }
    }
}
