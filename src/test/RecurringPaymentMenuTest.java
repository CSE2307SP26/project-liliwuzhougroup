package test;

import main.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
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
}
