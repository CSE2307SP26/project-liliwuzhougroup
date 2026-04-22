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
import java.time.LocalDate;
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
        assertTrue(dashboard.contains("5. AI Help Assistant"));
        assertTrue(dashboard.contains("8. AI Budget Advice"));
        assertTrue(dashboard.contains("9. Back to customer access"));
        assertTrue(dashboard.contains("10. Exit the app"));
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
            menu.processInput(10);
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

    @Test
    public void testSelectingAnAccountAllowsViewingMonthlyStatement() {
        Customer customer = new Customer("Ava");
        customer.getAccounts().get(0).deposit(120.0);
        LocalDate today = LocalDate.now();
        String input = String.join(System.lineSeparator(),
                "9",
                String.valueOf(today.getYear()),
                String.valueOf(today.getMonthValue()),
                "",
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

        String printed = output.toString();
        assertTrue(printed.contains("Statement for"));
        assertTrue(printed.contains("Deposited: 120.0"));
        assertTrue(printed.contains("Press Enter to return to the account menu."));
    }

    @Test
    public void testShowAIHelpAssistantAcceptsMultipleQuestionsUntilExit() {
        Customer customer = new Customer("Ava");
        String input = String.join(System.lineSeparator(),
                "How do I transfer money?",
                "exit"
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
            menu.showAIHelpAssistant();
        } finally {
            System.setOut(originalOutput);
        }

        String printed = output.toString();
        assertTrue(printed.contains("=== AI Help Assistant ==="));
        assertTrue(printed.contains("Ask a question about how to use the app"));
        assertTrue(printed.contains("Transfer money from this account"));
    }

    @Test
    public void testShowBudgetAdviceWaitsBeforeReturningToDashboard() {
        Customer customer = new Customer("Ava");
        String input = System.lineSeparator() + System.lineSeparator();
        CustomerMenu menu = new CustomerMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))),
                customer
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.showBudgetAdvice();
        } finally {
            System.setOut(originalOutput);
        }

        String printed = output.toString();
        assertTrue(printed.contains("=== AI Budget Advice ==="));
        assertTrue(printed.contains("No transactions found"));
        assertTrue(printed.contains("Press Enter to return to the account dashboard."));
    }
}
