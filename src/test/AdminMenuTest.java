package test;

import main.AdminMenu;
import main.Bank;
import main.BankAccount;
import main.Customer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

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

    @Test
    public void testAdminCanCreatePendingFee() {
        Bank bank = new Bank();
        Customer customer = new Customer("One");
        bank.addCustomer(customer);
        String rawInput = String.join(System.lineSeparator(),
                "1",
                "1",
                "Monthly maintenance fee",
                "2099-12-31",
                "25"
        ) + System.lineSeparator();
        AdminMenu menu = new AdminMenu(
                bank,
                new Scanner(new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8))),
                customer
        );

        menu.createPendingFee();

        assertEquals(1, customer.getAccounts().get(0).getRemainingFees().size());
        assertEquals("Monthly maintenance fee",
                customer.getAccounts().get(0).getRemainingFees().get(0).getDescription());
        assertEquals(25.0,
                customer.getAccounts().get(0).getRemainingFees().get(0).getAmount(),
                0.001);
    }
}
