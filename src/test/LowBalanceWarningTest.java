package main;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LowBalanceWarningTest {

    private LowBalanceWarning warningWithInput(String input) {
        return new LowBalanceWarning(
                new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))));
    }

    private String captureOutput(Runnable action) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(output));
            action.run();
        } finally {
            System.setOut(original);
        }
        return output.toString();
    }

    @Test
    public void noWarningWhenThresholdNotSet() {
        BankAccount account = new BankAccount(200.0, "");
        LowBalanceWarning warning = warningWithInput("");

        boolean result = warning.confirmIfNeeded(account, 150.0);

        assertTrue(result);
    }

    @Test
    public void noWarningWhenBalanceStaysAboveThreshold() {
        BankAccount account = new BankAccount(500.0, "");
        account.setLowBalanceThreshold(100.0);
        LowBalanceWarning warning = warningWithInput("");

        boolean result = warning.confirmIfNeeded(account, 200.0);

        assertTrue(result);
    }

    @Test
    public void warningShownWhenBalanceWouldDropBelowThreshold() {
        BankAccount account = new BankAccount(200.0, "");
        account.setLowBalanceThreshold(100.0);
        LowBalanceWarning warning = warningWithInput("1" + System.lineSeparator());

        String output = captureOutput(() -> warning.confirmIfNeeded(account, 150.0));

        assertTrue(output.contains("Warning"));
        assertTrue(output.contains("50.0"));
        assertTrue(output.contains("100.0"));
    }

    @Test
    public void customerCanCancelOnWarning() {
        BankAccount account = new BankAccount(200.0, "");
        account.setLowBalanceThreshold(100.0);
        LowBalanceWarning warning = warningWithInput("2" + System.lineSeparator());

        boolean result = warning.confirmIfNeeded(account, 150.0);

        assertFalse(result);
    }

    @Test
    public void customerCanProceedOnWarning() {
        BankAccount account = new BankAccount(200.0, "");
        account.setLowBalanceThreshold(100.0);
        LowBalanceWarning warning = warningWithInput("1" + System.lineSeparator());

        boolean result = warning.confirmIfNeeded(account, 150.0);

        assertTrue(result);
    }

    @Test
    public void warningTriggeredOnTransferBelowThreshold() {
        BankAccount account = new BankAccount(200.0, "");
        account.setLowBalanceThreshold(100.0);
        LowBalanceWarning warning = warningWithInput("2" + System.lineSeparator());

        String output = captureOutput(() -> warning.confirmIfNeeded(account, 150.0));

        assertTrue(output.contains("Warning"));
    }

    @Test
    public void setThresholdUpdatesAccount() {
        BankAccount account = new BankAccount(500.0, "");
        LowBalanceWarning warning = warningWithInput("150.0" + System.lineSeparator());

        warning.setThreshold(account);

        assertEquals(150.0, account.getLowBalanceThreshold(), 0.001);
    }

    @Test
    public void setThresholdToZeroDisablesWarning() {
        BankAccount account = new BankAccount(200.0, "");
        account.setLowBalanceThreshold(100.0);
        LowBalanceWarning warning = warningWithInput("0" + System.lineSeparator());

        warning.setThreshold(account);

        assertFalse(account.wouldTriggerLowBalanceWarning(190.0));
    }
}
