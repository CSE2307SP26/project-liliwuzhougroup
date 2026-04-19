package test;

import main.Customer;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class CustomerEdgeCaseTest {

    @Test
    public void testCustomerRejectsBlankName() {
        try {
            new Customer("   ");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Name cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testCustomerWithEmptyAccountsStillGetsDefaultAccount() {
        Customer customer = new Customer("Ava", Collections.emptyList());

        assertEquals(1, customer.getAccounts().size());
    }

    @Test
    public void testUnsetCredentialsDoNotAuthenticate() {
        Customer customer = new Customer("Ava");

        assertFalse(customer.verifyPassword("password123"));
        assertFalse(customer.verifyPin("1234"));
    }
}
