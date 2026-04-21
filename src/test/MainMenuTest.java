package test;

import main.AppExit;
import main.MainMenu;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class MainMenuTest {

    @Test
    public void testDisplayOptionsShowsMainChoices() {
        MainMenu menu = new MainMenu();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.displayOptions();
        } finally {
            System.setOut(originalOutput);
        }

        String screen = output.toString();
        assertTrue(screen.contains("\u001B[H\u001B[2J"));
        assertTrue(screen.contains("Welcome to the 237 Bank App!"));
        assertTrue(screen.contains("1. Log in as customer"));
        assertTrue(screen.contains("2. Log in as admin"));
        assertTrue(screen.contains("3. Exit the app"));
    }

    @Test
    public void testExitSelectionPrintsGoodbyeMessage() {
        MainMenu menu = new MainMenu();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.processInput(3);
        } catch (AppExit.Requested e) {
            // expected
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Thank you for using the 237 Bank App!"));
    }
}
