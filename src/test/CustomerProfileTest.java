package main;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CustomerProfileTest {

    @Test
    public void testConstructorStoresInitialValues() {
        CustomerProfile profile = new CustomerProfile("Nick", "123 Main St", "5551234567", "nick@test.com");

        assertEquals("Nick", profile.getName());
        assertEquals("123 Main St", profile.getAddress());
        assertEquals("5551234567", profile.getPhoneNumber());
        assertEquals("nick@test.com", profile.getEmail());
    }

    @Test
    public void testConstructorRejectsBlankName() {
        try {
            new CustomerProfile("   ");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Name cannot be empty.", e.getMessage());
        }
    }

    @Test
    public void testUpdatePersonalInformationTrimsValues() {
        CustomerProfile profile = new CustomerProfile("Nick");

        profile.updatePersonalInformation(" 123 Main St ", " 5551234567 ", " nick@test.com ");

        assertEquals("123 Main St", profile.getAddress());
        assertEquals("5551234567", profile.getPhoneNumber());
        assertEquals("nick@test.com", profile.getEmail());
    }

    @Test
    public void testUpdatePersonalInformationRejectsBlankEmail() {
        CustomerProfile profile = new CustomerProfile("Nick");

        try {
            profile.updatePersonalInformation("123 Main St", "5551234567", " ");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Email cannot be empty.", e.getMessage());
        }
    }
}
