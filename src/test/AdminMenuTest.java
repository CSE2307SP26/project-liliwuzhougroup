package test;

import main.AdminMenu;
import main.Bank;
import main.BankAccount;
import main.Customer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AdminMenuTest {

    @Test
    public void testCanAccessAdminOperations() {
        AdminMenu menu = new AdminMenu(new Bank(), new Customer("Admin View"));
        assertTrue(menu.canRunAdminOperations());
    }

    @Test
    public void testCanAccessCustomerOperations() {
        AdminMenu menu = new AdminMenu(new Bank(), new Customer("Admin View"));
        assertTrue(menu.canRunCustomerOperations());
    }

    @Test
    public void testAdminCanViewAllAccountHistory() {
        Bank bank = new Bank();
        Customer one = new Customer("One");
        Customer two = new Customer("Two");
        bank.addCustomer(one);
        bank.addCustomer(two);

        BankAccount accountOne = one.getAccounts().get(0);
        BankAccount accountTwo = two.getAccounts().get(0);
        accountOne.deposit(10);
        accountTwo.deposit(20);

        AdminMenu menu = new AdminMenu(bank, one);
        String allHistory = menu.getAllCustomersHistory();

        assertTrue(allHistory.contains("Customer: One"));
        assertTrue(allHistory.contains("Customer: Two"));
        assertTrue(allHistory.contains("Deposited: 10.0"));
        assertTrue(allHistory.contains("Deposited: 20.0"));
    }
}
