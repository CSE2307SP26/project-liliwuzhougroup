package test;

import main.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CustomerAccessPortalTest {

    @Test
    public void testMissingCustomerCanRegisterThroughPortal() {
        Bank bank = new Bank();
        String input = String.join(System.lineSeparator(),
                "1",
                "new-user@test.com",
                "1",
                "New User",
                "123 Main St",
                "5551234567",
                "password123",
                "2",
                "6",
                "3"
        ) + System.lineSeparator();

        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        new CustomerAccessPortal(bank, scanner).run();

        Customer createdCustomer = bank.findCustomerByEmail("new-user@test.com");
        assertNotNull(createdCustomer);
        assertEquals("New User", createdCustomer.getName());
        assertEquals("123 Main St", createdCustomer.getAddress());
        assertEquals("5551234567", createdCustomer.getPhoneNumber());
        assertTrue(createdCustomer.verifyPassword("password123"));
    }
}
