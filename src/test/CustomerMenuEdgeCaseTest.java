package main;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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

    @Test
    public void testPayPendingFeeRequiresExistingFees() {
        Customer customer = new Customer("Ava");
        CustomerMenu menu = new CustomerMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(new byte[0])),
                customer
        );

        try {
            menu.payPendingFee(customer.getAccounts().get(0));
            fail();
        } catch (IllegalStateException e) {
            assertEquals("There are no pending fees to pay.", e.getMessage());
        }
    }

    @Test
    public void testPayPendingFeeReportsInsufficientFunds() {
        Customer customer = new Customer("Ava");
        customer.getAccounts().get(0).deposit(5.0);
        customer.getAccounts().get(0).createFee(
                new Fee(20.0, "Late fee", new java.util.Date(System.currentTimeMillis() + 86400000))
        );
        CustomerMenu menu = new CustomerMenu(
                new Bank(),
                new Scanner(new ByteArrayInputStream(("1" + System.lineSeparator()).getBytes())),
                customer
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;

        try {
            System.setOut(new PrintStream(output));
            menu.payPendingFee(customer.getAccounts().get(0));
        } finally {
            System.setOut(originalOutput);
        }

        assertTrue(output.toString().contains("Insufficient funds to pay this fee."));
    }
}
