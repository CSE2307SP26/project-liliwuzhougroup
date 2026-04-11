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
