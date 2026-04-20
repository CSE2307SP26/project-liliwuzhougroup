package test;

import main.Bank;
import main.Customer;
import main.CustomerMenu;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomerDashboardMenuTest {

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
        assertTrue(dashboard.contains("Account Dashboard:"));
        assertTrue(dashboard.contains("1. Account #1"));
        assertTrue(dashboard.contains("2. Account #2"));
        assertTrue(dashboard.contains("3. Create an additional account"));
        assertTrue(dashboard.contains("7. Back to main menu"));
    }

    @Test
    public void testSelectingAnAccountOpensAccountMenuAndAllowsDeposit() {
        Customer customer = new Customer("Ava");
        String input = String.join(System.lineSeparator(),
                "1",
                "50",
                "9",
                "9"
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
        assertTrue(output.toString().contains("Deposit successful."));
    }

    @Test
    public void testSelectingAnAccountAllowsPayingPendingFee() {
        Customer customer = new Customer("Ava");
        customer.getAccounts().get(0).deposit(100.0);
        customer.getAccounts().get(0).createFee(
                new main.Fee(20.0, "Service fee", new java.util.Date(System.currentTimeMillis() + 86400000))
        );
        String input = String.join(System.lineSeparator(),
                "7",
                "1",
                "9",
                "9"
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
}
