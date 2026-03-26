package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import main.BankAccount;
import main.TransferMoney;

public class TransferMoneyTest {

    @Test
    public void testTransferMoney() {
        BankAccount sourceAccount = new BankAccount();
        BankAccount targetAccount = new BankAccount();
        TransferMoney transfer = new TransferMoney();
        sourceAccount.deposit(100);
        transfer.transferMoney(sourceAccount, targetAccount, 50);
        assertEquals(50, sourceAccount.getBalance(), 0.01);
        assertEquals(50, targetAccount.getBalance(), 0.01);
    }

    @Test
    public void testInvalidTransfer() {
        BankAccount sourceAccount = new BankAccount();
        BankAccount targetAccount = new BankAccount();
        TransferMoney transfer = new TransferMoney();
        sourceAccount.deposit(100);
        try {
            transfer.transferMoney(sourceAccount, targetAccount, -50);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Transfer amount must be positive.");
        }
    }

    @Test
    public void testInsufficientFunds() {
        BankAccount sourceAccount = new BankAccount();
        BankAccount targetAccount = new BankAccount();
        TransferMoney transfer = new TransferMoney();
        sourceAccount.deposit(100);
        try {
            transfer.transferMoney(sourceAccount, targetAccount, 150);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Insufficient funds in source account.");
        }
    }

    @Test
    public void testNullAccounts() {
        BankAccount sourceAccount = new BankAccount();
        TransferMoney transfer = new TransferMoney();
        sourceAccount.deposit(100);
        try {
            transfer.transferMoney(sourceAccount, null, 50);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Source and target accounts cannot be null.");
        }
    }

    @Test
    public void testSameAccountTransfer() {
        BankAccount sourceAccount = new BankAccount();
        TransferMoney transfer = new TransferMoney();
        sourceAccount.deposit(100);
        try {
            transfer.transferMoney(sourceAccount, sourceAccount, 50);
            transfer.transferMoney(sourceAccount, sourceAccount, 50);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testZeroAmountTransfer() {
        BankAccount sourceAccount = new BankAccount();
        BankAccount targetAccount = new BankAccount();
        TransferMoney transfer = new TransferMoney();
        sourceAccount.deposit(100);
        try {
            transfer.transferMoney(sourceAccount, targetAccount, 0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), "Transfer amount must be positive.");
        }
    }
}
