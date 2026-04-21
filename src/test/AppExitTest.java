package test;

import main.AppExit;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class AppExitTest {

    @Test
    public void testRequestPrintsGoodbyeMessageAndThrowsRequestedException() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            AppExit.request();
        } catch (AppExit.Requested e) {
            assertTrue(output.toString().contains("Thank you for using the 237 Bank App!"));
            return;
        } finally {
            System.setOut(originalOutput);
        }

        org.junit.Assert.fail("Expected app exit to be requested.");
    }
}
