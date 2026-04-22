package main;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class AdminMenu extends CustomerMenu {

    private static final int VIEW_SPECIFIC_ACCOUNT_HISTORY_SELECTION = 15;
    private static final int VIEW_ALL_ACCOUNT_HISTORY_SELECTION = 16;
    private static final int ADMIN_BACK_SELECTION = 17;
    private static final int ADMIN_EXIT_SELECTION = 18;
    private static final int ADMIN_MAX_SELECTION = 18;

    public AdminMenu(Bank bank, Customer customer) {
        this(bank, new Scanner(System.in), customer);
    }

    public AdminMenu(Bank bank, Scanner keyboardInput, Customer customer) {
        super(bank, keyboardInput, customer);
    }

    @Override
    public boolean canRunAdminOperations() {
        return true;
    }

    @Override
    public boolean canRunCustomerOperations() {
        return true;
    }

    @Override
    public void run() {
        int selection = -1;
        while (selection != ADMIN_BACK_SELECTION) {
            displayOptions();
            selection = io.readSelection(ADMIN_MAX_SELECTION);
            processInput(selection);
        }
    }

    @Override
    public void displayOptions() {
        MenuScreen.redraw();
        System.out.println("Admin Menu:");
        System.out.println("1. Customer: Make a deposit");
        System.out.println("2. Customer: Make a withdrawal");
        System.out.println("3. Customer: Check account balance");
        System.out.println("4. Customer: Check transaction history");
        System.out.println("5. Customer: Create an additional account");
        System.out.println("6. Customer: Close an existing account");
        System.out.println("7. Customer: Transfer money between accounts");
        System.out.println("8. Admin: Collect fee");
        System.out.println("9. Admin: Add interest payment");
        System.out.println("10. Admin: Freeze account");
        System.out.println("11. Admin: Unfreeze account");
        System.out.println("12. Customer: Set maximum withdrawal amount");
        System.out.println("13. Customer: Manage recurring payments");
        System.out.println("14. Admin: Create pending fee");
        System.out.println("15. Admin: View a specific account history");
        System.out.println("16. Admin: View all account history");
        System.out.println("17. Back to main menu");
        System.out.println("18. Exit the app");
    }

    @Override
    public void processInput(int selection) {
        try {
            if (selection == ADMIN_BACK_SELECTION) {
                System.out.println("Leaving admin menu.");
                return;
            }
            if (selection == ADMIN_EXIT_SELECTION) {
                MainMenu.requestExit();
                return;
            }
            if (processCustomerSelection(selection)) {
                return;
            }
            if (processAdminSelection(selection)) {
                return;
            }
            System.out.println("Invalid selection.");
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    public void collectFee() {
        BankAccount account = selectAnyCustomerAccount("collect fee from");
        double feeAmount = io.readPositiveAmount("Enter fee amount: ");
        bank.collectFee(account, feeAmount);
        System.out.println("Fee collected successfully.");
    }

    public void addInterest() {
        BankAccount account = selectAnyCustomerAccount("add interest to");
        double interestAmount = io.readPositiveAmount("Enter interest amount: ");
        bank.addInterest(account, interestAmount);
        System.out.println("Interest added successfully.");
    }

    public void freezeAccount() {
        BankAccount account = selectAnyCustomerAccount("freeze");
        bank.freezeAccount(account);
        System.out.println("Account frozen successfully.");
    }

    public void unfreezeAccount() {
        BankAccount account = selectAnyCustomerAccount("unfreeze");
        bank.unfreezeAccount(account);
        System.out.println("Account unfrozen successfully.");
    }

    public void createPendingFee() {
        BankAccount account = selectAnyCustomerAccount("create pending fee for");
        io.prepareForTextInput();
        String description = io.readRequiredText("Enter fee description: ");
        Date dueDate = readFeeDueDate();
        double feeAmount = io.readPositiveAmount("Enter fee amount: ");
        bank.createPendingFee(account, feeAmount, description, dueDate);
        System.out.println("Pending fee created successfully.");
    }

    public String getAllCustomersHistory() {
        return bank.getAllCustomersHistory();
    }

    public void displaySelectedAccountHistory() {
        BankAccount account = selectAnyCustomerAccount("view transaction history for");
        System.out.println("Transaction History:");
        String history = account.getTransactionHistory();
        System.out.println(history.isEmpty() ? "No transactions yet." : history);
    }

    public void displayAllCustomersHistory() {
        String allHistory = getAllCustomersHistory();
        System.out.println(allHistory.isEmpty() ? "No history available." : allHistory);
    }

    private boolean processCustomerSelection(int selection) {
        switch (selection) {
            case 1: prepareCustomerForOperation("deposit into"); performDeposit(); return true;
            case 2: prepareCustomerForOperation("withdraw from"); performWithdrawal(); return true;
            case 3: prepareCustomerForOperation("check balance for"); displayBalance(); return true;
            case 4: prepareCustomerForOperation("view transaction history for"); displayTransactionHistory(); return true;
            case 5: prepareCustomerForOperation("create account for"); createAdditionalAccount(); return true;
            case 6: prepareCustomerForOperation("close account for"); closeExistingAccount(); return true;
            case 7: prepareCustomerForOperation("transfer between accounts for"); transferBetweenAccounts(); return true;
            case 12: prepareCustomerForOperation("set maximum withdrawal amount for"); setMaximumWithdrawalAmount(); return true;
            case 13: prepareCustomerForOperation("manage recurring payments for"); manageRecurringPayments(); return true;
            default: return false;
        }
    }

    private boolean processAdminSelection(int selection) {
        switch (selection) {
            case 8: collectFee(); return true;
            case 9: addInterest(); return true;
            case 10: freezeAccount(); return true;
            case 11: unfreezeAccount(); return true;
            case 14: createPendingFee(); return true;
            case VIEW_SPECIFIC_ACCOUNT_HISTORY_SELECTION: displaySelectedAccountHistory(); return true;
            case VIEW_ALL_ACCOUNT_HISTORY_SELECTION: displayAllCustomersHistory(); return true;
            default: return false;
        }
    }

    private Date readFeeDueDate() {
        while (true) {
            String rawDate = io.readRequiredText("Enter fee due date (yyyy-MM-dd): ");
            try {
                LocalDate parsedDate = LocalDate.parse(rawDate);
                return Date.from(parsedDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }

    private BankAccount selectAnyCustomerAccount(String action) {
        setCustomer(selectCustomer("choose account to " + action));
        return accountSelector.selectAccount(customer, action);
    }

    private void prepareCustomerForOperation(String action) {
        setCustomer(selectCustomer(action));
    }

    private Customer selectCustomer(String action) {
        List<Customer> customers = bank.getCustomers();
        if (customers.isEmpty()) {
            throw new IllegalStateException("No customers available to " + action + ".");
        }
        System.out.println("Select customer to " + action + ":");
        for (int i = 0; i < customers.size(); i++) {
            System.out.println((i + 1) + ". " + customers.get(i).getName());
        }
        int selectedCustomer = io.readSelection(customers.size());
        return customers.get(selectedCustomer - 1);
    }
}
