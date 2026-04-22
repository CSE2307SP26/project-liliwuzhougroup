package test;

import main.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomerCredentialsTest {

    @Test
    public void testConstructorStoresInitialValues() {
        CustomerCredentials credentials = new CustomerCredentials("secret", "1234");

        assertEquals("secret", credentials.getStoredPassword());
        assertEquals("1234", credentials.getStoredPin());
    }

    @Test
    public void testSetPasswordTrimsAndVerifies() {
        CustomerCredentials credentials = new CustomerCredentials();
        credentials.setPassword("  secret  ");

        assertEquals("secret", credentials.getStoredPassword());
        assertTrue(credentials.verifyPassword("secret"));
        assertFalse(credentials.verifyPassword("wrong"));
    }

    @Test
    public void testSetPasswordRejectsBlankValues() {
        CustomerCredentials credentials = new CustomerCredentials();

        try {
            credentials.setPassword("   ");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Password cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testSetPinStoresAndVerifies() {
        CustomerCredentials credentials = new CustomerCredentials();
        credentials.setPin("4321");

        assertEquals("4321", credentials.getStoredPin());
        assertTrue(credentials.verifyPin("4321"));
        assertFalse(credentials.verifyPin("1234"));
    }

    @Test
    public void testSetPinRejectsInvalidValues() {
        CustomerCredentials credentials = new CustomerCredentials();

        try {
            credentials.setPin("12a4");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("PIN must be exactly 4 digits.", e.getMessage());
        }
    }
}
