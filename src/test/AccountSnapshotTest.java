package test;

import main.*;
import org.junit.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AccountSnapshotTest {

    @Test
    public void testToAccountRestoresBalanceFlagsHistoryAndFees() {
        BankAccount account = new BankAccount();
        account.deposit(75.0);
        account.setMaxWithdrawAmount(40.0);
        account.createFee(new Fee(12.0, "Maintenance", tomorrow()));

        BankAccount restored = new AccountSnapshot(account).toAccount();

        assertEquals(75.0, restored.getBalance(), 0.01);
        assertFalse(restored.isFrozen());
        assertEquals(40.0, restored.getMaxWithdrawAmount(), 0.01);
        assertTrue(restored.getTransactionHistory().contains("Deposited: 75.0"));
        assertEquals("Maintenance", restored.getRemainingFees().get(0).getDescription());
    }

    @Test
    public void testToAccountRestoresStructuredTransactions() {
        BankAccount account = new BankAccount();
        account.deposit(75.0);
        account.withdraw(10.0);

        BankAccount restored = new AccountSnapshot(account).toAccount();

        assertEquals(
                2,
                restored.getTransactionsByYearMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue()).size()
        );
    }

    @Test
    public void testToAccountHandlesLegacySnapshotsWithoutTransactions() throws Exception {
        BankAccount account = new BankAccount();
        account.deposit(75.0);
        AccountSnapshot snapshot = new AccountSnapshot(account);
        clearTransactions(snapshot);

        BankAccount restored = snapshot.toAccount();

        assertNotNull(restored);
        assertTrue(restored.getTransactionHistory().contains("Deposited: 75.0"));
        assertTrue(restored.getTransactions().isEmpty());
    }

    private void clearTransactions(AccountSnapshot snapshot) throws Exception {
        Field transactionsField = AccountSnapshot.class.getDeclaredField("transactions");
        transactionsField.setAccessible(true);
        transactionsField.set(snapshot, null);
    }

    private Date tomorrow() {
        return new Date(System.currentTimeMillis() + 86400000);
    }
}
