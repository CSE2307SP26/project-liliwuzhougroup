package test;

import main.BankAccount;
import main.Customer;
import org.junit.Test;

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
}
