package test;

import main.BankAccount;
import main.CreateAccount;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class CreateAccountTest {

    @Test
    public void testCreateBlankAccount() {
        CreateAccount ca = new CreateAccount();
        ArrayList<BankAccount> accounts = new ArrayList<>();

        ca.createBlankAccount(accounts);

        assertEquals(1, accounts.size());
        assertNotNull(accounts.get(0));
        assertEquals(0, accounts.get(0).getBalance(), 0.01);
    }

    @Test
    public void testCreateAccount() {
        CreateAccount ca = new CreateAccount();
        ArrayList<BankAccount> accounts = new ArrayList<>();
        BankAccount account = new BankAccount();

        ca.createAccount(accounts, account);

        assertEquals(1, accounts.size());
        assertSame(account, accounts.get(0));
    }
}
