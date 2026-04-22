package test;

import main.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
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
                "(555) 123-4567",
                "password123",
                "2",
                "7",
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

    @Test
    public void testRegistrationRetriesWhenPhoneNumberContainsLetters() {
        Bank bank = new Bank();
        String input = String.join(System.lineSeparator(),
                "1",
                "retry-user@test.com",
                "1",
                "Retry User",
                "123 Main St",
                "555-ABC-1234",
                "(555) 123-4567",
                "password123",
                "2",
                "7",
                "3"
        ) + System.lineSeparator();

        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            new CustomerAccessPortal(bank, scanner).run();
        } finally {
            System.setOut(originalOutput);
        }

        Customer createdCustomer = bank.findCustomerByEmail("retry-user@test.com");
        assertNotNull(createdCustomer);
        assertEquals("5551234567", createdCustomer.getPhoneNumber());
        assertTrue(output.toString().contains(
                "Phone number can contain only digits, spaces, parentheses, and hyphens."
        ));
    }

    @Test
    public void testPortalExitSelectionRequestsAppExit() {
        Bank bank = new Bank();
        String input = "4" + System.lineSeparator();
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            new CustomerAccessPortal(bank, scanner).run();
        } catch (MainMenu.ExitRequested e) {
            String printed = output.toString();
            assertTrue(printed.contains("\u001B[H\u001B[2J"));
            assertTrue(printed.contains("4. Exit the app"));
            assertTrue(printed.contains("Thank you for using the 237 Bank App!"));
            return;
        } finally {
            System.setOut(originalOutput);
        }

        org.junit.Assert.fail("Expected app exit to be requested.");
    }

    @Test
    public void testExistingCustomerCanLogInWithPassword() {
        Bank bank = SampleBankFactory.createSampleBank();
        String input = String.join(System.lineSeparator(),
                "1",
                SampleBankFactory.SAMPLE_CUSTOMER_EMAIL,
                "1",
                SampleBankFactory.SAMPLE_CUSTOMER_PASSWORD,
                "8",
                "3"
        ) + System.lineSeparator();

        String output = runPortal(bank, input);

        assertTrue(output.contains("Account Dashboard:"));
        assertTrue(output.contains("Returning to customer access."));
    }

    @Test
    public void testExistingCustomerCanLogInWithPin() {
        Bank bank = SampleBankFactory.createSampleBank();
        String input = String.join(System.lineSeparator(),
                "1",
                SampleBankFactory.SAMPLE_CUSTOMER_EMAIL,
                "2",
                SampleBankFactory.SAMPLE_CUSTOMER_PIN,
                "8",
                "3"
        ) + System.lineSeparator();

        String output = runPortal(bank, input);

        assertTrue(output.contains("Account Dashboard:"));
        assertTrue(output.contains("Returning to customer access."));
    }

    @Test
    public void testExistingCustomerSeesMessageWhenCredentialsAreInvalid() {
        Bank bank = SampleBankFactory.createSampleBank();
        String input = String.join(System.lineSeparator(),
                "1",
                SampleBankFactory.SAMPLE_CUSTOMER_EMAIL,
                "1",
                "wrong-password",
                "3"
        ) + System.lineSeparator();

        String output = runPortal(bank, input);

        assertTrue(output.contains("Invalid login credentials."));
    }

    private String runPortal(Bank bank, String input) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            new CustomerAccessPortal(bank, scanner).run();
        } finally {
            System.setOut(originalOutput);
        }

        return output.toString();
    }
}
