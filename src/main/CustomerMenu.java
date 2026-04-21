package main;

import java.util.List;
import java.util.Scanner;

public class CustomerMenu {

    private static final int DASHBOARD_OPTION_COUNT = 6;
    private static final int ACCOUNT_MENU_BACK_SELECTION = 10;
    private static final int ACCOUNT_MENU_EXIT_SELECTION = 11;
    private static final int ACCOUNT_MENU_MAX_SELECTION = 11;

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
        while (selection != getDashboardBackSelection()) {
            displayOptions();
            selection = io.readSelection(getDashboardMaxSelection());
            processInput(selection);
        }
    }

    public void displayOptions() {
        MenuScreen.redraw();
        System.out.println("Account Dashboard:");
        if (customer.getAccounts().isEmpty()) {
            System.out.println("No accounts available.");
        } else {
            accountSelector.displayAccounts(customer.getAccounts());
        }
        int option = customer.getAccounts().size() + 1;
        System.out.println(option++ + ". Create an additional account");
        System.out.println(option++ + ". Manage recurring payments");
        System.out.println(option++ + ". Update personal information");
        System.out.println(option++ + ". Set password or PIN");
        System.out.println(option++ + ". Back to customer access");
        System.out.println(option + ". Exit the app");
    }

    public void processInput(int selection) {
        try {
            int accountCount = customer.getAccounts().size();
            if (selection <= accountCount) {
                openAccountMenu(customer.getAccounts().get(selection - 1));
                return;
            }
            if (selection == accountCount + 1) {
                createAdditionalAccount();
                return;
            }
            if (selection == accountCount + 2) {
                manageRecurringPayments();
                return;
            }
            if (selection == accountCount + 3) {
                updatePersonalInformation();
                return;
            }
            if (selection == accountCount + 4) {
                setPasswordOrPin();
                return;
            }
            if (selection == accountCount + 5) {
                System.out.println("Returning to customer access.");
                return;
            }
            MainMenu.requestExit();
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    protected void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void performDeposit() {
        performDeposit(accountSelector.selectAccount(customer, "deposit into"));
    }

    public void performDeposit(BankAccount account) {
        double depositAmount = io.readPositiveAmount("How much would you like to deposit: ");
        account.deposit(depositAmount);
        System.out.println("Deposit successful.");
    }

    public void performWithdrawal() {
        performWithdrawal(accountSelector.selectAccount(customer, "withdraw from"));
    }

    public void performWithdrawal(BankAccount account) {
        double withdrawAmount = io.readPositiveAmount("How much would you like to withdraw: ");
        try {
            account.withdraw(withdrawAmount);
            System.out.println("Withdrawal successful.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void displayBalance() {
        displayBalance(accountSelector.selectAccount(customer, "check balance for"));
    }

    public void displayBalance(BankAccount account) {
        String status = account.isFrozen() ? "Frozen" : "Active";
        System.out.println("Your current balance is: " + account.getBalance());
        System.out.println("Account status: " + status);
        System.out.println("Maximum withdrawal amount is: " + account.getDisplayMaxWithdrawAmount());
    }

    public void displayTransactionHistory() {
        displayTransactionHistory(accountSelector.selectAccount(customer, "view transaction history for"));
    }

    public void displayTransactionHistory(BankAccount account) {
        System.out.println("Transaction History:");
        String history = account.getTransactionHistory();
        System.out.println(history.isEmpty() ? "No transactions yet." : history);
    }

    public void createAdditionalAccount() {
        customer.openAccount();
        System.out.println("Account created successfully.");
    }

    public void closeExistingAccount() {
        closeExistingAccount(accountSelector.selectAccount(customer, "close"));
    }

    public void closeExistingAccount(BankAccount account) {
        try {
            customer.closeAccount(account);
            System.out.println("Account closed successfully.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void transferBetweenAccounts() {
        if (customer.getAccounts().size() < 2) {
            throw new IllegalStateException("At least two accounts are required to transfer money.");
        }
        System.out.println("Select source account:");
        BankAccount sourceAccount = accountSelector.selectAccount(customer, "transfer money from");
        transferBetweenAccounts(sourceAccount);
    }

    public void transferBetweenAccounts(BankAccount sourceAccount) {
        if (customer.getAccounts().size() < 2) {
            throw new IllegalStateException("At least two accounts are required to transfer money.");
        }
        System.out.println("Select target account:");
        BankAccount targetAccount = accountSelector.selectAccount(customer, "transfer money to");
        while (sourceAccount == targetAccount) {
            System.out.println("Source and target accounts must be different.");
            targetAccount = accountSelector.selectAccount(customer, "transfer money to");
        }
        double amount = io.readPositiveAmount("How much would you like to transfer: ");

        try {
            sourceAccount.transferMoney(targetAccount, amount);
            System.out.println("Transfer successful.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setMaximumWithdrawalAmount() {
        setMaximumWithdrawalAmount(accountSelector.selectAccount(customer, "set maximum withdrawal amount for"));
    }

    public void setMaximumWithdrawalAmount(BankAccount account) {
        double maxAmount = io.readPositiveAmount("Enter the maximum withdrawal amount: ");
        try {
            account.setMaxWithdrawAmount(maxAmount);
            System.out.println("Maximum withdrawal amount updated successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    public void payPendingFee(BankAccount account) {
        List<Fee> fees = account.getRemainingFees();
        if (fees.isEmpty()) {
            throw new IllegalStateException("There are no pending fees to pay.");
        }

        System.out.println("Select a fee to pay:");
        for (int i = 0; i < fees.size(); i++) {
            Fee fee = fees.get(i);
            System.out.println((i + 1) + ". " + fee.getDescription()
                    + " - Amount: " + fee.getAmount()
                    + ", Due: " + fee.getDueDate());
        }

        int selectedFee = io.readSelection(fees.size()) - 1;
        try {
            account.payFee(selectedFee);
            System.out.println("Fee paid successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void viewMonthlyStatement(BankAccount account) {
        new MonthlyStatementViewer(keyboardInput, account).viewMonthlyStatement();
    }

    public void manageRecurringPayments() {
        new RecurringPaymentMenu(keyboardInput, customer).run();
    }

    public void updatePersonalInformation() {
        io.prepareForTextInput();
        String address = io.readRequiredText("Enter your address: ");
        String phoneNumber = io.readPhoneNumber("Enter your phone number: ");
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

    private void openAccountMenu(BankAccount account) {
        while (customer.getAccounts().contains(account)) {
            displayAccountOptions(account);
            int selection = io.readSelection(ACCOUNT_MENU_MAX_SELECTION);
            if (processAccountSelection(account, selection)) {
                return;
            }
        }
    }

    private void displayAccountOptions(BankAccount account) {
        MenuScreen.redraw();
        int accountNumber = customer.getAccounts().indexOf(account) + 1;
        System.out.println("Account #" + accountNumber + " Menu:");
        displayBalance(account);
        accountSelector.displayRemainingFees(account);
        System.out.println("1. Make a deposit");
        System.out.println("2. Make a withdrawal");
        System.out.println("3. Check account balance");
        System.out.println("4. Check transaction history");
        System.out.println("5. Transfer money from this account");
        System.out.println("6. Set maximum withdrawal amount");
        System.out.println("7. Pay a pending fee");
        System.out.println("8. View monthly statement");
        System.out.println("9. Close this account");
        System.out.println("10. Back to account dashboard");
        System.out.println("11. Exit the app");
    }

    private boolean processAccountSelection(BankAccount account, int selection) {
        switch (selection) {
            case 1: performDeposit(account); return false;
            case 2: performWithdrawal(account); return false;
            case 3: displayBalance(account); return false;
            case 4: displayTransactionHistory(account); return false;
            case 5: transferBetweenAccounts(account); return false;
            case 6: setMaximumWithdrawalAmount(account); return false;
            case 7: payPendingFee(account); return false;
            case 8: viewMonthlyStatement(account); return false;
            case 9:
                closeExistingAccount(account);
                return !customer.getAccounts().contains(account);
            case ACCOUNT_MENU_BACK_SELECTION:
                System.out.println("Returning to account dashboard.");
                return true;
            case ACCOUNT_MENU_EXIT_SELECTION:
                MainMenu.requestExit();
                return true;
            default: return false;
        }
    }

    private int getDashboardMaxSelection() {
        return customer.getAccounts().size() + DASHBOARD_OPTION_COUNT;
    }

    private int getDashboardBackSelection() {
        return customer.getAccounts().size() + DASHBOARD_OPTION_COUNT - 1;
    }
}
