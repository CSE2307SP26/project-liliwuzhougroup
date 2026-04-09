package test;

import main.BankAccount;
import main.Customer;
import org.junit.Test;

import static org.junit.Assert.*;

public class CreateAccountTest {

    @Test
    public void testCreateBlankAccount() {
        Customer customer = new Customer("John");
        BankAccount account = customer.openAccount();
        assertNotNull(account);
        assertEquals(2, customer.getAccounts().size());
        assertEquals(0, account.getBalance(), 0.01);
    }

    @Test
    public void testCreateAccount() {
        Customer customer = new Customer("John");
        BankAccount account = new BankAccount();
        customer.openAccount(account);
        assertEquals(2, customer.getAccounts().size());
        assertSame(account, customer.getAccounts().get(1));
    }
}
