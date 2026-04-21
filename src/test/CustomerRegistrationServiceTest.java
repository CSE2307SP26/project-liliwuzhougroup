package test;

import main.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomerRegistrationServiceTest {

    @Test
    public void testRegisterCreatesCustomerWithProfileCredentialsAndAccount() {
        Bank bank = new Bank();
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Ava Doe",
                "12 River Rd",
                "(555) 111-2222",
                "ava@test.com",
                "password123",
                "1234"
        );

        Customer customer = new CustomerRegistrationService(bank).register(request);

        assertEquals(1, bank.getCustomers().size());
        assertEquals("Ava Doe", customer.getName());
        assertEquals("12 River Rd", customer.getAddress());
        assertEquals("5551112222", customer.getPhoneNumber());
        assertEquals("ava@test.com", customer.getEmail());
        assertTrue(customer.verifyPassword("password123"));
        assertTrue(customer.verifyPin("1234"));
        assertEquals(1, customer.getAccounts().size());
    }

    @Test
    public void testRegisterRejectsInvalidPhoneNumber() {
        Bank bank = new Bank();
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Ava Doe",
                "12 River Rd",
                "555-2222",
                "ava@test.com",
                "password123",
                null
        );

        try {
            new CustomerRegistrationService(bank).register(request);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Phone number must contain exactly 10 digits.", e.getMessage());
        }
    }

    @Test
    public void testRegisterRejectsDuplicateEmailIgnoringCase() {
        Bank bank = new Bank();
        Customer existingCustomer = new Customer("Ava Doe");
        existingCustomer.updatePersonalInformation("12 River Rd", "5551112222", "AVA@test.com");
        bank.addCustomer(existingCustomer);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Other User",
                "45 Oak Ave",
                "5553334444",
                "ava@test.com",
                "password123",
                null
        );

        try {
            new CustomerRegistrationService(bank).register(request);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("A customer with that email already exists.", e.getMessage());
        }
    }
}
