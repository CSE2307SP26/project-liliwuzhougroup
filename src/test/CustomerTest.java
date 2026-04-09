package test;

import main.Customer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
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
}
