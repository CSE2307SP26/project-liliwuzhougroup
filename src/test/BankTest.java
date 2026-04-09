package test;

import main.Bank;
import main.BankAccount;
import main.Customer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class BankTest {

    @Test
    public void testAddAndRemoveCustomer() {
        Bank bank = new Bank();
        Customer customer = new Customer("John");
        bank.addCustomer(customer);
        assertEquals(1, bank.getCustomers().size());
        bank.removeCustomer(customer);
        assertEquals(0, bank.getCustomers().size());
    }

    @Test
    public void testCollectFee() {
        Bank bank = new Bank();
        BankAccount account = new BankAccount();
        account.deposit(100.0);
        bank.collectFee(account, 10.0);
        assertEquals(90.0, account.getBalance(), 0.001);
    }

    @Test
    public void testAddInterest() {
        Bank bank = new Bank();
        BankAccount account = new BankAccount();
        account.deposit(100.0);
        bank.addInterest(account, 5.0);
        assertEquals(105.0, account.getBalance(), 0.001);
    }

    @Test
    public void testTransactionHistoryAccessibleViaBank() {
        Bank bank = new Bank();
        Customer customer = new Customer("Alice");
        bank.addCustomer(customer);
        customer.getAccounts().get(0).deposit(200.0);
        String history = bank.getCustomers().get(0).getAccounts().get(0).getTransactionHistory();
        assertEquals("Deposited: 200.0\n", history);
    }

    @Test
    public void testTransactionHistoryEmptyForNewAccount() {
        Bank bank = new Bank();
        Customer customer = new Customer("Bob");
        bank.addCustomer(customer);
        String history = bank.getCustomers().get(0).getAccounts().get(0).getTransactionHistory();
        assertEquals("", history);
    }

    @Test
    public void testTransactionHistoryAcrossMultipleCustomers() {
        Bank bank = new Bank();
        Customer alice = new Customer("Alice");
        Customer bob = new Customer("Bob");
        bank.addCustomer(alice);
        bank.addCustomer(bob);
        alice.getAccounts().get(0).deposit(100.0);
        bob.getAccounts().get(0).deposit(500.0);
        String aliceHistory = bank.getCustomers().get(0).getAccounts().get(0).getTransactionHistory();
        String bobHistory = bank.getCustomers().get(1).getAccounts().get(0).getTransactionHistory();
        assertEquals("Deposited: 100.0\n", aliceHistory);
        assertEquals("Deposited: 500.0\n", bobHistory);
    }

    @Test
    public void testFreezeAndUnfreezeAccount() {
        Bank bank = new Bank();
        BankAccount account = new BankAccount();

        bank.freezeAccount(account);
        assertTrue(account.isFrozen());

        bank.unfreezeAccount(account);
        assertFalse(account.isFrozen());
    }

    @Test
    public void testCollectFeeRejectsNullAccount() {
        Bank bank = new Bank();

        try {
            bank.collectFee(null, 10.0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Account cannot be null.", e.getMessage());
        }
    }

    @Test
    public void testAddInterestRejectsNullAccount() {
        Bank bank = new Bank();

        try {
            bank.addInterest(null, 5.0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Account cannot be null.", e.getMessage());
        }
    }

    @Test
    public void testFreezeAccountRejectsNullAccount() {
        Bank bank = new Bank();

        try {
            bank.freezeAccount(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Account cannot be null.", e.getMessage());
        }
    }

    @Test
    public void testUnfreezeAccountRejectsNullAccount() {
        Bank bank = new Bank();

        try {
            bank.unfreezeAccount(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Account cannot be null.", e.getMessage());
        }
    }

    @Test
    public void testGetAllCustomersHistoryIncludesEmptyAccounts() {
        Bank bank = new Bank();
        Customer customer = new Customer("Alice");
        bank.addCustomer(customer);

        String history = bank.getAllCustomersHistory();

        assertTrue(history.contains("Customer: Alice"));
        assertTrue(history.contains("No transactions yet."));
    }
}
