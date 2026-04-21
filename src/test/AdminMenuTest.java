package test;

import main.AppExit;
import main.AdminMenu;
import main.Bank;
import main.BankAccount;
import main.Customer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    public void testDisplayOptionsIncludesExitApp() {
        AdminMenu menu = new AdminMenu(new Bank(), new Customer("Admin View"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.displayOptions();
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("\u001B[H\u001B[2J"));
        assertTrue(output.toString().contains("18. Exit the app"));
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
    public void testAdminCanViewSpecificAccountHistoryFromMenu() {
        Bank bank = new Bank();
        Customer firstCustomer = new Customer("First");
        Customer secondCustomer = new Customer("Second");
        bank.addCustomer(firstCustomer);
        bank.addCustomer(secondCustomer);
        firstCustomer.getAccounts().get(0).deposit(11.0);
        secondCustomer.getAccounts().get(0).deposit(22.0);

        String input = String.join(System.lineSeparator(), "2", "1") + System.lineSeparator();
        AdminMenu menu = new AdminMenu(
                bank,
                new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))),
                firstCustomer
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.processInput(15);
        } finally {
            System.setOut(originalOutput);
        }

        String screen = output.toString();
        assertTrue(screen.contains("Transaction History:"));
        assertTrue(screen.contains("Deposited: 22.0"));
        assertFalse(screen.contains("Deposited: 11.0\n"));
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

    @Test
    public void testCollectFeeReportsWhenNoCustomersExist() {
        AdminMenu menu = new AdminMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(new byte[0])),
                null
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.processInput(8);
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("No customers available to choose account to collect fee from."));
    }

    @Test
    public void testViewingSpecificAccountHistoryReportsWhenNoCustomersExist() {
        AdminMenu menu = new AdminMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(new byte[0])),
                null
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.processInput(15);
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains(
                "No customers available to choose account to view transaction history for."
        ));
    }

    @Test
    public void testCreatePendingFeeRepromptsUntilDateIsValid() {
        Bank bank = new Bank();
        bank.addCustomer(new Customer("Ava"));
        String rawInput = String.join(System.lineSeparator(),
                "1",
                "1",
                "Late fee",
                "not-a-date",
                "2099-05-01",
                "15"
        ) + System.lineSeparator();
        AdminMenu menu = new AdminMenu(
                bank,
                new Scanner(new ByteArrayInputStream(rawInput.getBytes())),
                null
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.createPendingFee();
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Invalid date format. Please use yyyy-MM-dd."));
        assertTrue(bank.getCustomers().get(0).getAccounts().get(0).getRemainingFees().size() == 1);
    }

    @Test
    public void testExitSelectionRequestsAppExit() {
        AdminMenu menu = new AdminMenu(new Bank(), new Customer("Admin View"));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.processInput(18);
        } catch (AppExit.Requested e) {
            assertTrue(output.toString().contains("Thank you for using the 237 Bank App!"));
            return;
        } finally {
            System.setOut(originalOutput);
        }

        org.junit.Assert.fail("Expected app exit to be requested.");
    }
}
