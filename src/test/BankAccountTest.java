package test;

import main.BankAccount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

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
    public void testAddInterest() {
        BankAccount account = new BankAccount();
        account.deposit(100.0);
        account.addInterest(10.0);
        assertEquals(110.0, account.getBalance(), 0.001);
    }

    @Test
    public void testInvalidAddInterest() {
        BankAccount account = new BankAccount();
        assertThrows(IllegalArgumentException.class, () -> account.addInterest(-10.0));
    }
}

