package test;

import main.Customer;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomerTest {

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
}
