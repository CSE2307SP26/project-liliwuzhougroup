package test;

import main.BankAccount;
import main.CheckBalance;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class CheckBalanceTest {

    @Test
    public void testDisplayInitialBalance() {
        BankAccount account = new BankAccount();
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(output));
        try {
            CheckBalance.display(account);
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("Your balance is: 0.0", output.toString().trim());
    }

    @Test
    public void testDisplayBalanceAfterDeposit() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        System.setOut(new PrintStream(output));
        try {
            CheckBalance.display(account);
        } finally {
            System.setOut(originalOut);
        }

        assertEquals("Your balance is: 100.0", output.toString().trim());
    }
}
