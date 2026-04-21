package main;

import java.util.List;

public final class CustomerAccountSelector {
    private final MenuInput io;

    public CustomerAccountSelector(MenuInput io) {
        this.io = io;
    }

    public BankAccount selectAccount(Customer customer, String action) {
        List<BankAccount> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            throw new IllegalStateException("No accounts available to " + action + ".");
        }
        System.out.println("Select account to " + action + ":");
        displayAccounts(accounts);
        BankAccount account = accounts.get(io.readSelection(accounts.size()) - 1);
        displayRemainingFees(account);
        return account;
    }

    void displayAccounts(List<BankAccount> accounts) {
        for (int i = 0; i < accounts.size(); i++) {
            BankAccount account = accounts.get(i);
            String status = account.isFrozen() ? "Frozen" : "Active";
            System.out.println((i + 1) + ". Account #" + (i + 1)
                    + " (Balance: " + account.getBalance()
                    + ", Status: " + status
                    + ", Max Withdraw: " + account.getMaxWithdrawAmount() + ")");
        }
    }

    void displayRemainingFees(BankAccount account) {
        for (Fee fee : account.getRemainingFees()) {
            System.out.println("You have " + fee.getAmount() + " due for "
                    + fee.getDescription() + " on " + fee.getDueDate() + ".");
        }
    }
}
