package test;

import main.Bank;
import main.Customer;
import main.CustomerMenu;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CustomerMenuTest {

    @Test
    public void testCannotAccessAdminOperations() {
        CustomerMenu menu = new CustomerMenu(new Bank(), new Customer("User"));
        assertFalse(menu.canRunAdminOperations());
    }

    @Test
    public void testCanAccessCustomerOperations() {
        CustomerMenu menu = new CustomerMenu(new Bank(), new Customer("User"));
        assertTrue(menu.canRunCustomerOperations());
    }
}
