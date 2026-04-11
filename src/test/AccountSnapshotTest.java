package main;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    private Date tomorrow() {
        return new Date(System.currentTimeMillis() + 86400000);
    }
}
