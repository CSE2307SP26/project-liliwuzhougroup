package test;

import main.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomerRegistrationRequestTest {

    @Test
    public void testGettersReturnProvidedValues() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Nick",
                "123 Main St",
                "5551234567",
                "nick@test.com",
                "password123",
                "1234"
        );

        assertEquals("Nick", request.getName());
        assertEquals("123 Main St", request.getAddress());
        assertEquals("5551234567", request.getPhoneNumber());
        assertEquals("nick@test.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("1234", request.getPin());
    }
}
