package test;

import main.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void testReadSelectionClearsOnlyCompletedPromptLine() {
        String rawInput = "2" + System.lineSeparator();
        MenuInput input = new MenuInput(
                new Scanner(new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8)))
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            assertEquals(2, input.readSelection(3));
        } finally {
            System.setOut(originalOutput);
        }

        String printed = output.toString();
        assertTrue(printed.contains("Please make a selection: "));
        assertTrue(printed.contains("\u001B[1A\u001B[2K\r"));
        assertTrue(!printed.contains("\u001B[H\u001B[2J"));
    }

    @Test
    public void testReadPhoneNumberRetriesUntilInputContainsOnlyAllowedCharacters() {
        String rawInput = String.join(System.lineSeparator(), "555abc1234", "(555) 123-4567")
                + System.lineSeparator();
        MenuInput input = new MenuInput(
                new Scanner(new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8)))
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            assertEquals("5551234567", input.readPhoneNumber("Enter phone: "));
        } finally {
            System.setOut(originalOutput);
        }

        String printed = output.toString();
        assertTrue(printed.contains("Phone number can contain only digits, spaces, parentheses, and hyphens."));
        assertTrue(printed.contains("Enter phone: "));
    }
}
