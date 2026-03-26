package test;
import java.util.ArrayList;
import main.BankAccount;
import main.CloseAccount;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CloseAccountTest {


    @Test
    public void testCloseAccount() {
        CloseAccount closeAccount = new CloseAccount();
        BankAccount account = new BankAccount();
        ArrayList<BankAccount> accounts = new ArrayList<>();
        accounts.add(account);
        closeAccount.closeAccount(accounts, account);
        assertTrue(accounts.isEmpty());
    }

    @Test
    public void testCloseAccountWithNonZeroBalance() {
        CloseAccount closeAccount = new CloseAccount();
        BankAccount account = new BankAccount();
        account.deposit(100);
        ArrayList<BankAccount> accounts = new ArrayList<>();
        accounts.add(account);
        try {
            closeAccount.closeAccount(accounts, account);
        } catch (IllegalStateException e) {
            assertEquals("Cannot close account with non-zero balance.", e.getMessage());
        }
        assertTrue(accounts.contains(account));
    }

    @Test
    public void testCloseAndReturnAccounts() {
        CloseAccount closeAccount = new CloseAccount();
        BankAccount account1 = new BankAccount();
        BankAccount account2 = new BankAccount();
        ArrayList<BankAccount> accounts = new ArrayList<>();
        accounts.add(account1);
        accounts.add(account2);
        ArrayList<BankAccount> updatedAccounts = closeAccount.closeAndReturnAccounts(accounts, account1);
        assertEquals(1, updatedAccounts.size());
        assertTrue(updatedAccounts.contains(account2));
    }

}
