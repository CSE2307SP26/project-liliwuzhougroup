package test;

import main.BankAccount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class BankAccountTest {

    @Test
    public void testDeposit() {
        BankAccount testAccount = new BankAccount();
        testAccount.deposit(50);
        assertEquals(50, testAccount.getBalance(), 0.01);
    }

    @Test
    public void testInvalidDeposit() {
        BankAccount testAccount = new BankAccount();
        try {
            testAccount.deposit(-50);
            fail();
        } catch (IllegalArgumentException e) {
            //do nothing, test passes
        }
    }

    @Test
    public void testCreateAccount() {
        BankAccount account = new BankAccount();
        assertEquals(0, account.getBalance(), 0.01);
    }
    @Test
    public void testInitialBalance() {
        BankAccount account = new BankAccount();
        assertEquals(0, account.getBalance(), 0.01);
    }

    @Test
    public void testBalanceAfterDeposit() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        assertEquals(100, account.getBalance(), 0.01);
    }

    @Test
    public void testWithdrawal() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        account.withdraw(30);
        assertEquals(70, account.getBalance(), 0.01);
    }

    @Test
    public void testInvalidWithdrawal() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        try {
            account.withdraw(-30);
            fail();

        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Invalid withdrawal amount");
        }
    }

    @Test
    public void testOverdraft() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        try {
            account.withdraw(150);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Invalid withdrawal amount");
        }
    }

    @Test
    public void testZeroWithdrawal() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        try {            
            account.withdraw(0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Invalid withdrawal amount");
        }
    }
}
