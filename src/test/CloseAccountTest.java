package test;

import main.BankAccount;
import main.Customer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class CloseAccountTest {


    @Test
    public void testCloseAccount() {
        Customer customer = new Customer("John");
        BankAccount account = customer.openAccount();
        customer.closeAccount(account);
        assertEquals(1, customer.getAccounts().size());
    }

    @Test
    public void testCloseAccountWithNonZeroBalance() {
        Customer customer = new Customer("John");
        BankAccount account = new BankAccount();
        account.deposit(100);
        customer.openAccount(account);
        try {
            customer.closeAccount(account);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Cannot close account with non-zero balance.", e.getMessage());
        }
        assertTrue(customer.getAccounts().contains(account));
    }

    @Test
    public void testCloseAndReturnAccounts() {
        Customer customer = new Customer("John");
        BankAccount account1 = customer.openAccount();
        BankAccount account2 = customer.openAccount();
        customer.closeAccount(account1);
        assertEquals(2, customer.getAccounts().size());
        assertTrue(customer.getAccounts().contains(account2));
    }

}
