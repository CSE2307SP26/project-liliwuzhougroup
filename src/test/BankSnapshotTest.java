package main;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BankSnapshotTest {

    @Test
    public void testToBankRestoresCustomersAndRecurringPayments() {
        Bank bank = new Bank();
        Customer customer = new Customer("Nick");
        bank.addCustomer(customer);

        customer.setPassword("secret123");
        customer.setPin("1234");
        customer.updatePersonalInformation("123 Main", "5551234567", "nick@test.com");
        customer.openAccount();
        customer.setupRecurringPayment("Weekly", 0, 1, 50.0, RecurringPayment.Frequency.WEEKLY);
        customer.getAccounts().get(0).createFee(createFee());

        Bank restored = new BankSnapshot(bank).toBank();

        assertEquals(1, restored.getCustomers().size());
        assertEquals("Nick", restored.getCustomers().get(0).getName());
        assertTrue(restored.getCustomers().get(0).verifyPassword("secret123"));
        assertEquals(LocalDate.now(),
                restored.getCustomers().get(0).getRecurringPayments().get(0).getNextPaymentDate());
    }

    private Fee createFee() {
        return new Fee(10.0, "Service fee", tomorrow());
    }

    private Date tomorrow() {
        return new Date(System.currentTimeMillis() + 86400000);
    }
}
