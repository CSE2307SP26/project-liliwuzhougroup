package main;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CustomerAuthenticatorTest {

    @Test
    public void testFindCustomerByEmailSupportsTrimmedLookup() {
        Bank bank = new Bank();
        Customer customer = new Customer("Ava");
        customer.updatePersonalInformation("12 River Rd", "5551112222", "ava@test.com");
        bank.addCustomer(customer);

        CustomerAuthenticator authenticator = new CustomerAuthenticator(bank);

        assertSame(customer, authenticator.findCustomer("  ava@test.com  "));
    }

    @Test
    public void testValidatePasswordAndPinCredentials() {
        Bank bank = new Bank();
        Customer customer = new Customer("Ava");
        customer.updatePersonalInformation("12 River Rd", "5551112222", "ava@test.com");
        customer.setPassword("password123");
        customer.setPin("1234");
        bank.addCustomer(customer);

        CustomerAuthenticator authenticator = new CustomerAuthenticator(bank);

        assertTrue(authenticator.isPasswordValid(customer, "password123"));
        assertTrue(authenticator.isPinValid(customer, "1234"));
        assertFalse(authenticator.isPasswordValid(customer, "wrong-password"));
        assertFalse(authenticator.isPinValid(customer, "9999"));
    }
}
