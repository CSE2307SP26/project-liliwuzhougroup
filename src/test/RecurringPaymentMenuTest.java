package test;

import main.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RecurringPaymentMenuTest {

    @Test
    public void testSetupPaymentRequiresAtLeastTwoAccounts() {
        RecurringPaymentMenu menu = new RecurringPaymentMenu(
                new Scanner(new ByteArrayInputStream(new byte[0])),
                new Customer("Ava")
        );

        try {
            menu.setupPayment();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("At least two accounts are required for recurring payments.", e.getMessage());
        }
    }

    @Test
    public void testSetupPaymentRepromptsUntilTargetDiffersFromSource() {
        Customer customer = new Customer("Ava");
        customer.openAccount();
        String rawInput = String.join(System.lineSeparator(),
                "Rent",
                "1",
                "1",
                "2",
                "25",
                "2"
        ) + System.lineSeparator();
        RecurringPaymentMenu menu = new RecurringPaymentMenu(
                new Scanner(new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8))),
                customer
        );

        menu.setupPayment();

        assertEquals(1, customer.getRecurringPayments().size());
        assertEquals(0, customer.getRecurringPayments().get(0).getSourceAccountIndex());
        assertEquals(1, customer.getRecurringPayments().get(0).getTargetAccountIndex());
        assertEquals(RecurringPayment.Frequency.WEEKLY, customer.getRecurringPayments().get(0).getFrequency());
    }

    @Test
    public void testProcessInputExplainsSetupRequiresAnotherAccount() {
        RecurringPaymentMenu menu = new RecurringPaymentMenu(
                new Scanner(new ByteArrayInputStream(new byte[0])),
                new Customer("Ava")
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.displayOptions();
            menu.processInput(1);
        } finally {
            System.setOut(originalOutput);
        }

        String printed = output.toString();
        assertTrue(printed.contains("\u001B[H\u001B[2J"));
        assertTrue(printed.contains("Set up a new recurring payment (requires at least 2 accounts)"));
        assertTrue(printed.contains("5. Back to previous menu"));
        assertTrue(printed.contains("6. Exit the app"));
        assertTrue(printed.contains(
                "Recurring payments require at least two accounts. "
                        + "Create another account from the customer dashboard first."
        ));
    }

    @Test
    public void testProcessInputExitSelectionRequestsAppExit() {
        RecurringPaymentMenu menu = new RecurringPaymentMenu(
                new Scanner(new ByteArrayInputStream(new byte[0])),
                new Customer("Ava")
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.processInput(6);
            fail();
        } catch (AppExit.Requested e) {
            assertTrue(output.toString().contains("Thank you for using the 237 Bank App!"));
        } finally {
            System.setOut(originalOutput);
        }
    }
}
