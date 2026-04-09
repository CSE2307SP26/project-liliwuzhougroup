package test;

import main.Customer;
import main.RecurringPayment;
import main.RecurringPayment.Frequency;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RecurringPaymentTest {

    private Customer customerWithTwoAccounts() {
        Customer customer = new Customer("Test User");
        customer.openAccount();
        customer.getAccounts().get(0).deposit(500.0);
        return customer;
    }

    @Test
    public void testSetupRecurringPayment() {
        Customer customer = customerWithTwoAccounts();
        customer.setupRecurringPayment("Rent", 0, 1, 200.0, Frequency.MONTHLY);
        assertEquals(1, customer.getRecurringPayments().size());
        assertEquals(LocalDate.now(), customer.getRecurringPayments().get(0).getNextPaymentDate());
    }

    @Test
    public void testProcessTransfersAmountAndAdvancesNextDate() {
        Customer customer = customerWithTwoAccounts();
        customer.setupRecurringPayment("Savings", 0, 1, 100.0, Frequency.WEEKLY);
        RecurringPayment payment = customer.getRecurringPayments().get(0);

        int processedCount = customer.processRecurringPayments();

        assertEquals(1, processedCount);
        assertEquals(400.0, customer.getAccounts().get(0).getBalance(), 0.01);
        assertEquals(100.0, customer.getAccounts().get(1).getBalance(), 0.01);
        assertEquals(LocalDate.now().plusWeeks(1), payment.getNextPaymentDate());
    }

    @Test
    public void testProcessInsufficientFunds() {
        Customer customer = customerWithTwoAccounts();
        customer.setupRecurringPayment("Large", 0, 1, 1000.0, Frequency.MONTHLY);
        try {
            customer.processRecurringPayments();
            fail("Expected IllegalArgumentException for insufficient funds.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSetupInvalidSourceIndex() {
        try {
            customerWithTwoAccounts().setupRecurringPayment("Bad source", 99, 1, 50.0, Frequency.DAILY);
            fail("Expected IllegalArgumentException for invalid source index.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSetupInvalidTargetIndex() {
        try {
            customerWithTwoAccounts().setupRecurringPayment("Bad target", 0, 99, 50.0, Frequency.DAILY);
            fail("Expected IllegalArgumentException for invalid target index.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSetupNullDescription() {
        Customer customer = customerWithTwoAccounts();
        try {
            customer.setupRecurringPayment(null, 0, 1, 100.0, Frequency.WEEKLY);
            fail("Expected IllegalArgumentException for null description.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSetupZeroAmount() {
        Customer customer = customerWithTwoAccounts();
        try {
            customer.setupRecurringPayment("Savings", 0, 1, 0.0, Frequency.MONTHLY);
            fail("Expected IllegalArgumentException for zero amount.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSetupNegativeAmount() {
        Customer customer = customerWithTwoAccounts();
        try {
            customer.setupRecurringPayment("Savings", 0, 1, -50.0, Frequency.MONTHLY);
            fail("Expected IllegalArgumentException for negative amount.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testCancelRecurringPayment() {
        Customer customer = customerWithTwoAccounts();
        customer.setupRecurringPayment("Rent", 0, 1, 200.0, Frequency.MONTHLY);
        customer.cancelRecurringPayment(0);
        assertEquals(0, customer.getRecurringPayments().size());
    }

    @Test
    public void testCancelInvalidIndex() {
        Customer customer = customerWithTwoAccounts();
        try {
            customer.cancelRecurringPayment(5);
            fail("Expected IllegalArgumentException for invalid index.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testGetRecurringPaymentsIsUnmodifiable() {
        Customer customer = customerWithTwoAccounts();
        try {
            customer.getRecurringPayments().add(
                new RecurringPayment("Test", 0, 1, 50.0, Frequency.DAILY, LocalDate.now()));
            fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testMultiplePaymentsProcessedInOrder() {
        Customer customer = customerWithTwoAccounts();
        customer.setupRecurringPayment("First", 0, 1, 100.0, Frequency.WEEKLY);
        customer.setupRecurringPayment("Second", 0, 1, 50.0, Frequency.MONTHLY);
        customer.processRecurringPayments();
        assertEquals(350.0, customer.getAccounts().get(0).getBalance(), 0.01);
        assertEquals(150.0, customer.getAccounts().get(1).getBalance(), 0.01);
    }

    @Test
    public void testRecurringPaymentIsNotDueBeforeNextDate() {
        RecurringPayment payment = new RecurringPayment(
                "Future payment",
                0,
                1,
                50.0,
                Frequency.MONTHLY,
                LocalDate.now().plusDays(1)
        );

        assertFalse(payment.isDue(LocalDate.now()));
        assertTrue(payment.isDue(LocalDate.now().plusDays(1)));
    }

    @Test
    public void testRecurringPaymentProcessThrowsWhenNotDue() {
        Customer customer = customerWithTwoAccounts();
        RecurringPayment payment = new RecurringPayment(
                "Future payment",
                0,
                1,
                50.0,
                Frequency.DAILY,
                LocalDate.now().plusDays(1)
        );

        try {
            payment.process(customer.getAccounts(), LocalDate.now());
            fail("Expected IllegalStateException when payment is not due.");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testRecurringPaymentRejectsSameSourceAndTarget() {
        try {
            new RecurringPayment("Invalid", 0, 0, 25.0, Frequency.DAILY, LocalDate.now());
            fail("Expected IllegalArgumentException for same source and target.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testRecurringPaymentRejectsNullProcessingDate() {
        RecurringPayment payment = new RecurringPayment(
                "Future payment",
                0,
                1,
                50.0,
                Frequency.MONTHLY,
                LocalDate.now()
        );

        try {
            payment.isDue(null);
            fail("Expected IllegalArgumentException for null processing date.");
        } catch (IllegalArgumentException e) {
            assertEquals("Processing date is required.", e.getMessage());
        }
    }

    @Test
    public void testRecurringPaymentRejectsMissingAccountsDuringProcessing() {
        RecurringPayment payment = new RecurringPayment(
                "Transfer",
                0,
                1,
                25.0,
                Frequency.DAILY,
                LocalDate.now()
        );

        try {
            payment.process(null, LocalDate.now());
            fail("Expected IllegalArgumentException for null accounts.");
        } catch (IllegalArgumentException e) {
            assertEquals("Accounts cannot be null.", e.getMessage());
        }
    }

    @Test
    public void testRecurringPaymentRejectsMissingTargetAccount() {
        Customer customer = new Customer("Test User");
        customer.getAccounts().get(0).deposit(100.0);
        RecurringPayment payment = new RecurringPayment(
                "Transfer",
                0,
                1,
                25.0,
                Frequency.DAILY,
                LocalDate.now()
        );

        try {
            payment.process(customer.getAccounts(), LocalDate.now());
            fail("Expected IllegalArgumentException for a missing target account.");
        } catch (IllegalArgumentException e) {
            assertEquals("Target account no longer exists.", e.getMessage());
        }
    }
}
