package main;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CustomerMenuEdgeCaseTest {

    @Test
    public void testTransferRequiresAtLeastTwoAccounts() {
        CustomerMenu menu = new CustomerMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(new byte[0])),
                new Customer("Ava")
        );

        try {
            menu.transferBetweenAccounts();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("At least two accounts are required to transfer money.", e.getMessage());
        }
    }
}
