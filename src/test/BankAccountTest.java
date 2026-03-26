package test;

import main.BankAccount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
            // do nothing, test passes
        }
    }

    // added test for collect fee method
    @Test
    public void testCollectFee() {
        BankAccount account = new BankAccount();
        account.deposit(100.0);
        account.collectFee(10.0);
        assertEquals(90.0, account.getBalance(), 0.001);
    }

    @Test
    public void testInvalidCollectFee() {
        BankAccount account = new BankAccount();
        assertThrows(IllegalArgumentException.class, () -> account.collectFee(-10.0));
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
    
    //transaction-history test
    @Test
    public void testTransactionHistoryStartsEmpty() {
    BankAccount account = new BankAccount();
    assertEquals("", account.getTransactionHistory());
    }

    @Test
    public void testTransactionHistoryAfterDeposit() {
    BankAccount account = new BankAccount();
    account.deposit(100);
    assertEquals("Deposited: 100.0\n", account.getTransactionHistory());
    }

    @Test
    public void testTransactionHistoryAfterMultipleDeposits() {
    BankAccount account = new BankAccount();
    account.deposit(100);
    account.deposit(50);
    assertEquals("Deposited: 100.0\nDeposited: 50.0\n", account.getTransactionHistory());
    }


 //withdrawTest
    @ Test
    public void testWithdraw() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        account.withdraw(40);
        assertEquals(60, account.getBalance(), 0.01);
    }

    @Test
    public void testWithdrawEntireBalance() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        account.withdraw(100);
        assertEquals(0, account.getBalance(), 0.01);
    }

    @Test
    public void testInvalidWithdrawNegativeAmount() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        try {
            account.withdraw(-50);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testInvalidWithdrawTooMuch() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        try {
            account.withdraw(150);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testInvalidWithdrawZero() {
        BankAccount account = new BankAccount();
        account.deposit(100);
        try {
            account.withdraw(0);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }
}
