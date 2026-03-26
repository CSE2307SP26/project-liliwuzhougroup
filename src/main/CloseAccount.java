package main;

import java.util.ArrayList;

public class CloseAccount {
    public ArrayList<BankAccount> closeAndReturnAccounts(ArrayList<BankAccount> accounts, BankAccount account) {
        if (account.getBalance() == 0) {
            System.out.println("Account closed successfully.");
            accounts.remove(account);
            return accounts;
        } else {
            throw new IllegalStateException("Cannot close account with non-zero balance.");
        }
    }
    
    public void closeAccount(ArrayList<BankAccount> accounts, BankAccount account) {
        if (account.getBalance() == 0) {
            System.out.println("Account closed successfully.");
            accounts.remove(account);
        } else {
            throw new IllegalStateException("Cannot close account with non-zero balance.");
        }
    }

    
}
