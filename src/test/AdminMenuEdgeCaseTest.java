package test;

import main.AdminMenu;
import main.Bank;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.assertTrue;

public class AdminMenuEdgeCaseTest {

    @Test
    public void testCollectFeeReportsWhenNoCustomersExist() {
        AdminMenu menu = new AdminMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(new byte[0])),
                null
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.processInput(8);
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("No customers available to choose account to collect fee from."));
    }
}
