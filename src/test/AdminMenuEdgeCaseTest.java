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

    @Test
    public void testCreatePendingFeeRepromptsUntilDateIsValid() {
        Bank bank = new Bank();
        bank.addCustomer(new main.Customer("Ava"));
        String rawInput = String.join(System.lineSeparator(),
                "1",
                "1",
                "Late fee",
                "not-a-date",
                "2099-05-01",
                "15"
        ) + System.lineSeparator();
        AdminMenu menu = new AdminMenu(
                bank,
                new Scanner(new ByteArrayInputStream(rawInput.getBytes())),
                null
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.createPendingFee();
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Invalid date format. Please use yyyy-MM-dd."));
        assertTrue(bank.getCustomers().get(0).getAccounts().get(0).getRemainingFees().size() == 1);
    }
}
