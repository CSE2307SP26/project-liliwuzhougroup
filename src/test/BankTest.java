package test;

import main.Bank;
import main.BankAccount;
import main.Customer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
