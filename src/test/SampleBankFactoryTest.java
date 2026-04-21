package test;

import main.Bank;
import main.Customer;
import main.SampleBankFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SampleBankFactoryTest {

    @Test
    public void testCreateSampleBankProvidesDocumentedCustomerAndAccounts() {
        Bank bank = SampleBankFactory.createSampleBank();

        assertEquals(1, bank.getCustomers().size());

        Customer customer = bank.getCustomers().get(0);
        assertEquals(SampleBankFactory.SAMPLE_CUSTOMER_NAME, customer.getName());
        assertEquals(SampleBankFactory.SAMPLE_CUSTOMER_ADDRESS, customer.getAddress());
        assertEquals(SampleBankFactory.SAMPLE_CUSTOMER_PHONE, customer.getPhoneNumber());
        assertEquals(SampleBankFactory.SAMPLE_CUSTOMER_EMAIL, customer.getEmail());
        assertTrue(customer.verifyPassword(SampleBankFactory.SAMPLE_CUSTOMER_PASSWORD));
        assertTrue(customer.verifyPin(SampleBankFactory.SAMPLE_CUSTOMER_PIN));
        assertEquals(2, customer.getAccounts().size());
        assertEquals(SampleBankFactory.PRIMARY_ACCOUNT_BALANCE,
                customer.getAccounts().get(0).getBalance(), 0.001);
        assertEquals(SampleBankFactory.SECONDARY_ACCOUNT_BALANCE,
                customer.getAccounts().get(1).getBalance(), 0.001);
    }
}
