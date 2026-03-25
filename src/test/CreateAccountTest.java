package test;

import main.BankAccount;
import main.CreateAccount;
import org.junit.Test;
import static org.junit.Assert.*;

public class CreateNewAccountTest {

    @Test
    public void testCreateNewAccount() {
        CreateAccount ca = new CreateAccount();
        BankAccount account = ca.execute();

        assertNotNull(account);                 
        assertEquals(0, account.getBalance(), 0.01);}
}