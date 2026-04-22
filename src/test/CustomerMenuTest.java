package test;

import main.Bank;
import main.Customer;
import main.CustomerMenu;
import main.Fee;
import main.MainMenu;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomerMenuTest {

    @Test
    public void testCannotAccessAdminOperations() {
        CustomerMenu menu = new CustomerMenu(new Bank(), new Customer("User"));
        assertFalse(menu.canRunAdminOperations());
    }

    @Test
    public void testCanAccessCustomerOperations() {
        CustomerMenu menu = new CustomerMenu(new Bank(), new Customer("User"));
        assertTrue(menu.canRunCustomerOperations());
    }

    @Test
    public void testDashboardListsAccountsBeforeActions() {
        Customer customer = new Customer("Ava");
        customer.openAccount();
        CustomerMenu menu = new CustomerMenu(new Bank(), customer);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.displayOptions();
        } finally {
            System.setOut(originalOutput);
        }

        String dashboard = output.toString();
        assertTrue(dashboard.contains("\u001B[H\u001B[2J"));
        assertTrue(dashboard.contains("Account Dashboard:"));
        assertTrue(dashboard.contains("1. Account #1"));
        assertTrue(dashboard.contains("2. Account #2"));
        assertTrue(dashboard.contains("Max Withdraw: Unlimited"));
        assertTrue(dashboard.contains("3. Create an additional account"));
        assertTrue(dashboard.contains("7. AI Budget Advice"));
        assertTrue(dashboard.contains("8. Back to customer access"));
        assertTrue(dashboard.contains("9. Exit the app"));
    }

    @Test
    public void testDashboardExitSelectionRequestsAppExit() {
        Customer customer = new Customer("Ava");
        customer.openAccount();
        CustomerMenu menu = new CustomerMenu(new Bank(), customer);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.processInput(9);
            fail();
        } catch (MainMenu.ExitRequested e) {
            assertTrue(output.toString().contains("Thank you for using the 237 Bank App!"));
        } finally {
            System.setOut(originalOutput);
        }
    }

    @Test
    public void testSelectingAnAccountOpensAccountMenuAndAllowsDeposit() {
        Customer customer = new Customer("Ava");
        String input = String.join(System.lineSeparator(),
                "1",
                "50",
                "11"
        ) + System.lineSeparator();
        CustomerMenu menu = new CustomerMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))),
                customer
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.processInput(1);
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals(50.0, customer.getAccounts().get(0).getBalance(), 0.001);
        assertTrue(output.toString().contains("Account #1 Menu:"));
        assertTrue(output.toString().contains("Maximum withdrawal amount is: Unlimited"));
        assertTrue(output.toString().contains("Deposit successful."));
    }

    @Test
    public void testSelectingAnAccountAllowsPayingPendingFee() {
        Customer customer = new Customer("Ava");
        customer.getAccounts().get(0).deposit(100.0);
        customer.getAccounts().get(0).createFee(
                new Fee(20.0, "Service fee", new java.util.Date(System.currentTimeMillis() + 86400000))
        );
        String input = String.join(System.lineSeparator(),
                "7",
                "1",
                "11"
        ) + System.lineSeparator();
        CustomerMenu menu = new CustomerMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))),
                customer
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.processInput(1);
        } finally {
            System.setOut(originalOutput);
        }

        assertEquals(80.0, customer.getAccounts().get(0).getBalance(), 0.001);
        assertTrue(customer.getAccounts().get(0).getRemainingFees().isEmpty());
        assertTrue(output.toString().contains("Fee paid successfully."));
    }

    @Test
    public void testTransferRequiresAtLeastTwoAccounts() {
        CustomerMenu menu = new CustomerMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(new byte[0])),
                new Customer("Ava")
        );

        try {
            menu.transferBetweenAccounts();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("At least two accounts are required to transfer money.", e.getMessage());
        }
    }

    @Test
    public void testPayPendingFeeRequiresExistingFees() {
        Customer customer = new Customer("Ava");
        CustomerMenu menu = new CustomerMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(new byte[0])),
                customer
        );

        try {
            menu.payPendingFee(customer.getAccounts().get(0));
            fail();
        } catch (IllegalStateException e) {
            assertEquals("There are no pending fees to pay.", e.getMessage());
        }
    }

    @Test
    public void testPayPendingFeeReportsInsufficientFunds() {
        Customer customer = new Customer("Ava");
        customer.getAccounts().get(0).deposit(5.0);
        customer.getAccounts().get(0).createFee(
                new Fee(20.0, "Late fee", new java.util.Date(System.currentTimeMillis() + 86400000))
        );
        CustomerMenu menu = new CustomerMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(("1" + System.lineSeparator()).getBytes())),
                customer
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.payPendingFee(customer.getAccounts().get(0));
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Insufficient funds to pay this fee."));
    }
}
