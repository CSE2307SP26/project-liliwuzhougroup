package test;

import main.BankAccount;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class BankAccountEdgeCaseTest {

    @Test
    public void testWithdrawRejectsAmountAboveConfiguredMaximum() {
        BankAccount account = new BankAccount();
        account.deposit(200.0);
        account.setMaxWithdrawAmount(50.0);

        try {
            account.withdraw(75.0);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Withdrawal amount exceeds the maximum amount", e.getMessage());
        }
    }
}
