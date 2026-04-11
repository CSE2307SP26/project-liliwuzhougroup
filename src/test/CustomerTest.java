package test;

import main.BankAccount;
import main.Customer;
import main.RecurringPayment;
import main.RecurringPayment.Frequency;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomerTest {

    @Test
    public void testUpdatePersonalInformation() {
        Customer customer = new Customer("Nick");
        customer.updatePersonalInformation("123 Main St", "5551234567", "Nick@test.com");

        assertEquals("123 Main St", customer.getAddress());
        assertEquals("5551234567", customer.getPhoneNumber());
        assertEquals("Nick@test.com", customer.getEmail());
    }

    @Test
    public void testUpdatePersonalInformationRejectsNullAddress() {
        Customer customer = new Customer("Nick");

        try {
            customer.updatePersonalInformation(null, "5551234567", "Nick@test.com");
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testUpdatePersonalInformationRejectsBlankPhoneNumber() {
        Customer customer = new Customer("Nick");

        try {
            customer.updatePersonalInformation("123 Main St", "   ", "Nick@test.com");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phone number cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testUpdatePersonalInformationRejectsBlankEmail() {
        Customer customer = new Customer("Nick");

        try {
            customer.updatePersonalInformation("123 Main St", "5551234567", "");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Email cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testSetPasswordAndVerifyPassword() {
        Customer customer = new Customer("Nick");
        customer.setPassword("securePass123");

        assertTrue(customer.verifyPassword("securePass123"));
        assertFalse(customer.verifyPassword("wrongPassword"));
    }

    @Test
    public void testSetPinAndVerifyPin() {
        Customer customer = new Customer("Nick");
        customer.setPin("1234");

        assertTrue(customer.verifyPin("1234"));
        assertFalse(customer.verifyPin("5678"));
    }

    @Test
    public void testSetPinRejectsInvalidPin() {
        Customer customer = new Customer("Nick");

        try {
            customer.setPin("12a4");
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testOpenAccountRejectsNullAccount() {
        Customer customer = new Customer("Nick");

        try {
            customer.openAccount(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Account cannot be null.", e.getMessage());
        }
    }

    @Test
    public void testCloseAccountRejectsNullAccount() {
        Customer customer = new Customer("Nick");

        try {
            customer.closeAccount(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Account cannot be null.", e.getMessage());
        }
    }

    @Test
    public void testAccountsListIsUnmodifiable() {
        Customer customer = new Customer("Nick");
        List<BankAccount> accounts = customer.getAccounts();

        try {
            accounts.add(new BankAccount());
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testCreateBlankAccount() {
        Customer customer = new Customer("John");
        BankAccount account = customer.openAccount();

        assertEquals(2, customer.getAccounts().size());
        assertEquals(0, account.getBalance(), 0.01);
    }

    @Test
    public void testCreateAccount() {
        Customer customer = new Customer("John");
        BankAccount account = new BankAccount();

        customer.openAccount(account);

        assertEquals(2, customer.getAccounts().size());
        assertTrue(customer.getAccounts().contains(account));
    }

    @Test
    public void testCloseAccount() {
        Customer customer = new Customer("John");
        BankAccount account = customer.openAccount();

        customer.closeAccount(account);

        assertEquals(1, customer.getAccounts().size());
    }

    @Test
    public void testCloseAccountWithNonZeroBalance() {
        Customer customer = new Customer("John");
        BankAccount account = new BankAccount();
        account.deposit(100);
        customer.openAccount(account);

        try {
            customer.closeAccount(account);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("Cannot close account with non-zero balance.", e.getMessage());
        }

        assertTrue(customer.getAccounts().contains(account));
    }

    @Test
    public void testCloseAndReturnAccounts() {
        Customer customer = new Customer("John");
        BankAccount account1 = customer.openAccount();
        BankAccount account2 = customer.openAccount();

        customer.closeAccount(account1);

        assertEquals(2, customer.getAccounts().size());
        assertTrue(customer.getAccounts().contains(account2));
    }

    private Customer customerWithRecurringAccounts() {
        Customer customer = new Customer("Test User");
        customer.openAccount();
        customer.getAccounts().get(0).deposit(500.0);
        return customer;
    }

    @Test
    public void testSetupRecurringPayment() {
        Customer customer = customerWithRecurringAccounts();

        customer.setupRecurringPayment("Rent", 0, 1, 200.0, Frequency.MONTHLY);

        assertEquals(1, customer.getRecurringPayments().size());
        assertEquals(LocalDate.now(), customer.getRecurringPayments().get(0).getNextPaymentDate());
    }

    @Test
    public void testProcessRecurringPaymentsTransfersAmountAndAdvancesNextDate() {
        Customer customer = customerWithRecurringAccounts();
        customer.setupRecurringPayment("Savings", 0, 1, 100.0, Frequency.WEEKLY);
        RecurringPayment payment = customer.getRecurringPayments().get(0);

        int processedCount = customer.processRecurringPayments();

        assertEquals(1, processedCount);
        assertEquals(400.0, customer.getAccounts().get(0).getBalance(), 0.01);
        assertEquals(100.0, customer.getAccounts().get(1).getBalance(), 0.01);
        assertEquals(LocalDate.now().plusWeeks(1), payment.getNextPaymentDate());
    }

    @Test
    public void testProcessRecurringPaymentsWithInsufficientFunds() {
        Customer customer = customerWithRecurringAccounts();
        customer.setupRecurringPayment("Large", 0, 1, 1000.0, Frequency.MONTHLY);

        try {
            customer.processRecurringPayments();
            fail("Expected IllegalArgumentException for insufficient funds.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSetupRecurringPaymentRejectsInvalidSourceIndex() {
        try {
            customerWithRecurringAccounts().setupRecurringPayment("Bad source", 99, 1, 50.0, Frequency.DAILY);
            fail("Expected IllegalArgumentException for invalid source index.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSetupRecurringPaymentRejectsInvalidTargetIndex() {
        try {
            customerWithRecurringAccounts().setupRecurringPayment("Bad target", 0, 99, 50.0, Frequency.DAILY);
            fail("Expected IllegalArgumentException for invalid target index.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSetupRecurringPaymentRejectsNullDescription() {
        Customer customer = customerWithRecurringAccounts();

        try {
            customer.setupRecurringPayment(null, 0, 1, 100.0, Frequency.WEEKLY);
            fail("Expected IllegalArgumentException for null description.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSetupRecurringPaymentRejectsZeroAmount() {
        Customer customer = customerWithRecurringAccounts();

        try {
            customer.setupRecurringPayment("Savings", 0, 1, 0.0, Frequency.MONTHLY);
            fail("Expected IllegalArgumentException for zero amount.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testSetupRecurringPaymentRejectsNegativeAmount() {
        Customer customer = customerWithRecurringAccounts();

        try {
            customer.setupRecurringPayment("Savings", 0, 1, -50.0, Frequency.MONTHLY);
            fail("Expected IllegalArgumentException for negative amount.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testCancelRecurringPayment() {
        Customer customer = customerWithRecurringAccounts();
        customer.setupRecurringPayment("Rent", 0, 1, 200.0, Frequency.MONTHLY);

        customer.cancelRecurringPayment(0);

        assertEquals(0, customer.getRecurringPayments().size());
    }

    @Test
    public void testCancelRecurringPaymentRejectsInvalidIndex() {
        Customer customer = customerWithRecurringAccounts();

        try {
            customer.cancelRecurringPayment(5);
            fail("Expected IllegalArgumentException for invalid index.");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testRecurringPaymentsListIsUnmodifiable() {
        Customer customer = customerWithRecurringAccounts();

        try {
            customer.getRecurringPayments().add(
                    new RecurringPayment("Test", 0, 1, 50.0, Frequency.DAILY, LocalDate.now()));
            fail("Expected UnsupportedOperationException.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testProcessRecurringPaymentsHandlesMultiplePaymentsInOrder() {
        Customer customer = customerWithRecurringAccounts();
        customer.setupRecurringPayment("First", 0, 1, 100.0, Frequency.WEEKLY);
        customer.setupRecurringPayment("Second", 0, 1, 50.0, Frequency.MONTHLY);

        customer.processRecurringPayments();

        assertEquals(350.0, customer.getAccounts().get(0).getBalance(), 0.01);
        assertEquals(150.0, customer.getAccounts().get(1).getBalance(), 0.01);
    }
}
