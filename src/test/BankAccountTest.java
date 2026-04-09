package test;

import main.BankAccount;
import main.Fee;
import org.junit.Test;
import java.util.Date;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertFalse;

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

    @Test
    public void testZeroDepositIsRejected() {
        BankAccount account = new BankAccount();
        try {
            account.deposit(0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Deposit amount must be greater than 0.", e.getMessage());
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
        try {
            account.collectFee(-10.0);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
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

    // transaction-history test
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

    // withdrawTest
    @Test
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

    @Test
    public void testAddInterest() {
        BankAccount account = new BankAccount();
        account.deposit(100.0);
        account.addInterest(10.0);
        assertEquals(110.0, account.getBalance(), 0.001);
    }

    @Test
    public void testInvalidAddInterest() {
        BankAccount account = new BankAccount();
        try {
            account.addInterest(-10.0);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testTransferMoney() {
        BankAccount sourceAccount = new BankAccount();
        BankAccount targetAccount = new BankAccount();
        sourceAccount.deposit(100);
        sourceAccount.transferMoney(targetAccount, 50);
        assertEquals(50, sourceAccount.getBalance(), 0.01);
        assertEquals(50, targetAccount.getBalance(), 0.01);
    }

    @Test
    public void testInvalidTransfer() {
        BankAccount sourceAccount = new BankAccount();
        BankAccount targetAccount = new BankAccount();
        sourceAccount.deposit(100);
        try {
            sourceAccount.transferMoney(targetAccount, -50);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Transfer amount must be positive.", e.getMessage());
        }
    }

    @Test
    public void testInsufficientFunds() {
        BankAccount sourceAccount = new BankAccount();
        BankAccount targetAccount = new BankAccount();
        sourceAccount.deposit(100);
        try {
            sourceAccount.transferMoney(targetAccount, 150);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Insufficient funds in source account.", e.getMessage());
        }
    }

    @Test
    public void testNullAccounts() {
        BankAccount sourceAccount = new BankAccount();
        sourceAccount.deposit(100);
        try {
            sourceAccount.transferMoney(null, 50);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Source and target accounts cannot be null.", e.getMessage());
        }
    }

    @Test
    public void testSameAccountTransfer() {
        BankAccount sourceAccount = new BankAccount();
        sourceAccount.deposit(100);
        try {
            sourceAccount.transferMoney(sourceAccount, 50);
            sourceAccount.transferMoney(sourceAccount, 50);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testZeroAmountTransfer() {
        BankAccount sourceAccount = new BankAccount();
        BankAccount targetAccount = new BankAccount();
        sourceAccount.deposit(100);
        try {
            sourceAccount.transferMoney(targetAccount, 0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Transfer amount must be positive.", e.getMessage());
        }
    }

    @Test
    public void testTransferToFrozenAccountIsRejected() {
        BankAccount sourceAccount = new BankAccount();
        BankAccount targetAccount = new BankAccount();
        sourceAccount.deposit(100);
        targetAccount.freezeAccount();

        try {
            sourceAccount.transferMoney(targetAccount, 25);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("You cannot transfer money into a frozen account.", e.getMessage());
        }
    }
    @Test
    public void testGetRemainingFeesStartsEmpty() {
        BankAccount account = new BankAccount();
        List<Fee> fees = account.getRemainingFees();
        assertTrue(fees.isEmpty());
    }

    @Test
    public void testGetRemainingFeesAfterCreateFee() {
        BankAccount account = new BankAccount();
        Fee fee = new Fee(25.0, "Late fee", new Date(System.currentTimeMillis() + 86400000));
        account.createFee(fee);

        List<Fee> fees = account.getRemainingFees();
        assertEquals(1, fees.size());
        assertEquals(25.0, fees.get(0).getAmount(), 0.001);
        assertEquals("Late fee", fees.get(0).getDescription());
    }

    @Test
    public void testCreateFeeRejectsNullFee() {
        BankAccount account = new BankAccount();

        try {
            account.createFee(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Fee cannot be null.", e.getMessage());
        }
    }

    @Test
    public void testGetRemainingFeesReturnsCopy() {
        BankAccount account = new BankAccount();
        Fee fee = new Fee(25.0, "Late fee", new Date(System.currentTimeMillis() + 86400000));
        account.createFee(fee);

        List<Fee> fees = account.getRemainingFees();
        fees.clear();

        assertEquals(1, account.getRemainingFees().size());
    }

    // test for freeze and unfreeze account
    @Test
    public void testFreezeAccountBlocksDepositAndWithdraw() {
        BankAccount account = new BankAccount();
        account.freezeAccount();

        assertTrue(account.isFrozen());

        try {
            account.deposit(50);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("This account is frozen. Transactions are not allowed.", e.getMessage());
        }

        try {
            account.withdraw(20);
            fail();
        } catch (IllegalStateException e) {
            assertEquals("This account is frozen. Transactions are not allowed.", e.getMessage());
        }
    }

    // unfreeze account test
    @Test
    public void testUnfreezeAccountAllowsTransactionsAgain() {
        BankAccount account = new BankAccount();
        account.freezeAccount();
        account.unfreezeAccount();

        assertFalse(account.isFrozen());

        account.deposit(100);
        account.withdraw(40);

        assertEquals(60, account.getBalance(), 0.01);
    }

    @Test
    public void testSetMaxWithdrawAmount() {
        BankAccount account = new BankAccount();
        account.setMaxWithdrawAmount(200);
        assertEquals(200, account.getMaxWithdrawAmount(), 0.01);
    }

    @Test
    public void testSetMaxWithdrawAmountRejectsZero() {
        BankAccount account = new BankAccount();

        try {
            account.setMaxWithdrawAmount(0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Maximum withdrawal amount must be greater than 0.", e.getMessage());
        }
    }

    @Test
    public void testWithdrawExceedsMaxWithdrawAmount() {
        BankAccount account = new BankAccount();
        account.deposit(500);
        account.setMaxWithdrawAmount(100);

        try {
            account.withdraw(150);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Withdrawal amount exceeds the maximum amount", e.getMessage());
        }
    }

    @Test
    public void testWithdrawWithinMaxWithdrawAmount() {
        BankAccount account = new BankAccount();
        account.deposit(500);
        account.setMaxWithdrawAmount(100);

        account.withdraw(80);

        assertEquals(420, account.getBalance(), 0.01);
    }
}
