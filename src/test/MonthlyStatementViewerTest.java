package main;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MonthlyStatementViewerTest {

    private static final int CURRENT_YEAR = java.time.LocalDate.now().getYear();
    private static final int CURRENT_MONTH = java.time.LocalDate.now().getMonthValue();

    private String runViewer(BankAccount account, String input) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        MonthlyStatementViewer viewer = new MonthlyStatementViewer(scanner, account);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(output));
            viewer.viewMonthlyStatement();
        } finally {
            System.setOut(original);
        }
        return output.toString();
    }

    @Test
    public void testNoTransactionsShowsNotFoundMessage() {
        BankAccount account = new BankAccount();
        String input = CURRENT_YEAR + System.lineSeparator() + CURRENT_MONTH + System.lineSeparator();

        String output = runViewer(account, input);

        assertTrue(output.contains("No transactions found for"));
    }

    @Test
    public void testTransactionsInQueriedMonthAreDisplayed() {
        BankAccount account = new BankAccount();
        account.deposit(200.0);
        account.withdraw(50.0);
        String input = CURRENT_YEAR + System.lineSeparator() + CURRENT_MONTH + System.lineSeparator();

        String output = runViewer(account, input);

        assertTrue(output.contains("Deposited: 200.0"));
        assertTrue(output.contains("Withdrew: 50.0"));
    }

    @Test
    public void testTransactionsOutsideQueriedMonthAreExcluded() {
        BankAccount account = new BankAccount();
        account.deposit(100.0);

        int otherMonth = (CURRENT_MONTH % 12) + 1;
        int otherYear = otherMonth == 1 ? CURRENT_YEAR + 1 : CURRENT_YEAR;
        String input = otherYear + System.lineSeparator() + otherMonth + System.lineSeparator();

        String output = runViewer(account, input);

        assertFalse(output.contains("Deposited: 100.0"));
        assertTrue(output.contains("No transactions found for"));
    }

    @Test
    public void testSummaryShowsCreditsDebitsAndNetChange() {
        BankAccount account = new BankAccount();
        account.deposit(300.0);
        account.withdraw(80.0);
        String input = CURRENT_YEAR + System.lineSeparator() + CURRENT_MONTH + System.lineSeparator();

        String output = runViewer(account, input);

        assertTrue(output.contains("Total credits:"));
        assertTrue(output.contains("Total debits:"));
        assertTrue(output.contains("Net change:"));
        assertTrue(output.contains("300.0"));
        assertTrue(output.contains("80.0"));
        assertTrue(output.contains("220.0"));
    }
}
