package test;

import main.*;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CustomerSnapshotTest {

    @Test
    public void testToCustomerRestoresProfileAccountsAndPayments() {
        Customer customer = new Customer("Amy");
        customer.updatePersonalInformation("1 Oak St", "5550001111", "amy@test.com");
        customer.setPassword("pw12345");
        customer.setPin("4321");
        customer.openAccount();
        customer.setupRecurringPayment("Savings", 0, 1, 20.0, RecurringPayment.Frequency.MONTHLY);

        Customer restored = new CustomerSnapshot(customer).toCustomer();

        assertEquals("Amy", restored.getName());
        assertEquals("1 Oak St", restored.getAddress());
        assertEquals(2, restored.getAccounts().size());
        assertTrue(restored.verifyPassword("pw12345"));
        assertEquals(LocalDate.now(),
                restored.getRecurringPayments().get(0).getNextPaymentDate());
    }
}
