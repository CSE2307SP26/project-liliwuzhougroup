package test;

import main.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CustomerAccountSelectorTest {

    @Test
    public void testSelectAccountRejectsWhenNoAccountsAreAvailable() {
        Customer customer = new Customer("No Accounts") {
            @Override
            public java.util.List<BankAccount> getAccounts() {
                return Collections.emptyList();
            }
        };
        CustomerAccountSelector selector = new CustomerAccountSelector(
                new MenuInput(new Scanner(new ByteArrayInputStream(new byte[0])))
        );

        try {
            selector.selectAccount(customer, "withdraw from");
            fail();
        } catch (IllegalStateException e) {
            assertEquals("No accounts available to withdraw from.", e.getMessage());
        }
    }

    @Test
    public void testSelectAccountDisplaysChoicesAndReturnsSelectedAccount() {
        Customer customer = new Customer("Nick");
        customer.getAccounts().get(0).deposit(10.0);
        BankAccount secondAccount = customer.openAccount();
        secondAccount.deposit(25.0);
        secondAccount.createFee(new Fee(15.0, "Monthly fee", tomorrow()));

        String rawInput = "2" + System.lineSeparator();
        CustomerAccountSelector selector = new CustomerAccountSelector(
                new MenuInput(new Scanner(new ByteArrayInputStream(rawInput.getBytes(StandardCharsets.UTF_8))))
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream original = System.out;
        try {
            System.setOut(new PrintStream(output));
            BankAccount selected = selector.selectAccount(customer, "review");

            assertSame(secondAccount, selected);
        } finally {
            System.setOut(original);
        }

        String printed = output.toString();
        assertTrue(printed.contains("Select account to review:"));
        assertTrue(printed.contains("2. Account #2"));
        assertTrue(printed.contains("You have 15.0 due for Monthly fee"));
    }

    private Date tomorrow() {
        return new Date(System.currentTimeMillis() + 86400000);
    }
}
