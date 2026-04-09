package test;

import main.Customer;
import main.Customer.RecurringPayment;
import main.Customer.RecurringPayment.Frequency;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
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
    }

    @Test
    public void testProcessTransfersAmount() {
        Customer customer = customerWithTwoAccounts();
        customer.setupRecurringPayment("Savings", 0, 1, 100.0, Frequency.WEEKLY);
        customer.processRecurringPayments();
        assertEquals(400.0, customer.getAccounts().get(0).getBalance(), 0.01);
        assertEquals(100.0, customer.getAccounts().get(1).getBalance(), 0.01);
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
    public void testProcessInvalidSourceIndex() {
        Customer customer = customerWithTwoAccounts();
        customer.setupRecurringPayment("Bad source", 99, 1, 50.0, Frequency.DAILY);
        try {
            customer.processRecurringPayments();
            fail("Expected IndexOutOfBoundsException for invalid source index.");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testProcessInvalidTargetIndex() {
        Customer customer = customerWithTwoAccounts();
        customer.setupRecurringPayment("Bad target", 0, 99, 50.0, Frequency.DAILY);
        try {
            customer.processRecurringPayments();
            fail("Expected IndexOutOfBoundsException for invalid target index.");
        } catch (IndexOutOfBoundsException e) {
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
            fail("Expected IndexOutOfBoundsException for invalid index.");
        } catch (IndexOutOfBoundsException e) {
            // expected
        }
    }

    @Test
    public void testGetRecurringPaymentsIsUnmodifiable() {
        Customer customer = customerWithTwoAccounts();
        try {
            customer.getRecurringPayments().add(
                new RecurringPayment("Test", 0, 1, 50.0, Frequency.DAILY));
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
}
