package main;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MenuInputTest {

    @Test
    public void testReadSelectionRejectsMissingOptions() {
        MenuInput input = new MenuInput(new Scanner(new ByteArrayInputStream(new byte[0])));

        try {
            input.readSelection(0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("At least one selection must be available.", e.getMessage());
        }
    }

    @Test
    public void testReadPinRetriesUntilInputIsFourDigits() {
        String rawInput = String.join(System.lineSeparator(), "12", "12345", "1234")
                + System.lineSeparator();
        MenuInput input = new MenuInput(
                new Scanner(new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8)))
        );

        assertEquals("1234", input.readPin("Enter PIN: "));
    }
}
