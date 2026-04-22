package test;

import main.Bank;
import main.BankAccount;
import main.BankDataStore;
import main.Customer;
import main.Fee;
import main.RecurringPayment;
import main.SampleBankFactory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BankDataStoreTest {

    @Test
    public void testSaveAndLoadBankPreservesCustomerData() throws IOException {
        File tempFile = File.createTempFile("bank-data-store", ".dat");
        tempFile.deleteOnExit();

        Bank bank = new Bank();
        Customer customer = new Customer("Nick");
        bank.addCustomer(customer);

        customer.updatePersonalInformation("123 Main St", "5551234567", "nick@test.com");
        customer.setPassword("securePass123");
        customer.setPin("1234");

        BankAccount firstAccount = customer.getAccounts().get(0);
        firstAccount.deposit(300.0);
        Date dueDate = new Date(System.currentTimeMillis() + 86400000);
        firstAccount.createFee(new Fee(15.0, "Monthly fee", dueDate));

        BankAccount secondAccount = customer.openAccount();
        secondAccount.deposit(40.0);
        secondAccount.setMaxWithdrawAmount(25.0);
        secondAccount.freezeAccount();

        customer.openAccount();
        customer.setupRecurringPayment("Weekly savings", 0, 2, 75.0, RecurringPayment.Frequency.WEEKLY);

        BankDataStore.saveBank(bank, tempFile);
        Bank loadedBank = BankDataStore.loadBank(tempFile);

        assertEquals(1, loadedBank.getCustomers().size());

        Customer loadedCustomer = loadedBank.getCustomers().get(0);
        assertEquals("Nick", loadedCustomer.getName());
        assertEquals("123 Main St", loadedCustomer.getAddress());
        assertEquals("5551234567", loadedCustomer.getPhoneNumber());
        assertEquals("nick@test.com", loadedCustomer.getEmail());
        assertTrue(loadedCustomer.verifyPassword("securePass123"));
        assertTrue(loadedCustomer.verifyPin("1234"));
        assertEquals(3, loadedCustomer.getAccounts().size());

        BankAccount loadedFirstAccount = loadedCustomer.getAccounts().get(0);
        assertEquals(300.0, loadedFirstAccount.getBalance(), 0.01);
        assertFalse(loadedFirstAccount.isFrozen());
        assertEquals(1, loadedFirstAccount.getRemainingFees().size());
        assertEquals("Monthly fee", loadedFirstAccount.getRemainingFees().get(0).getDescription());
        assertEquals(dueDate, loadedFirstAccount.getRemainingFees().get(0).getDueDate());
        assertTrue(loadedFirstAccount.getTransactionHistory().contains("Deposited: 300.0"));
        assertEquals(
                1,
                loadedFirstAccount.getTransactionsByYearMonth(LocalDate.now().getYear(), LocalDate.now().getMonthValue())
                        .size()
        );

        BankAccount loadedSecondAccount = loadedCustomer.getAccounts().get(1);
        assertTrue(loadedSecondAccount.isFrozen());
        assertEquals(25.0, loadedSecondAccount.getMaxWithdrawAmount(), 0.01);
        assertTrue(loadedSecondAccount.getTransactionHistory().contains("Account frozen."));

        assertEquals(1, loadedCustomer.getRecurringPayments().size());
        RecurringPayment loadedPayment = loadedCustomer.getRecurringPayments().get(0);
        assertEquals("Weekly savings", loadedPayment.getDescription());
        assertEquals(0, loadedPayment.getSourceAccountIndex());
        assertEquals(2, loadedPayment.getTargetAccountIndex());
        assertEquals(75.0, loadedPayment.getAmount(), 0.01);
        assertEquals(RecurringPayment.Frequency.WEEKLY, loadedPayment.getFrequency());
        assertEquals(LocalDate.now(), loadedPayment.getNextPaymentDate());
    }

    @Test
    public void testLoadBankReturnsEmptyBankWhenFileDoesNotExist() {
        File missingFile = new File("out/does-not-exist-" + System.nanoTime() + ".dat");

        Bank bank = BankDataStore.loadBank(missingFile);

        assertNotNull(bank);
        assertTrue(bank.getCustomers().isEmpty());
    }

    @Test
    public void testDefaultLoadBankAddsSampleCustomerWhenSavedDataDoesNotContainIt() throws IOException {
        File dataFile = File.createTempFile("bank-data-store-default", ".dat");
        dataFile.deleteOnExit();

        BankDataStore.saveBank(new Bank(), dataFile);

        Bank bank = BankDataStore.loadBank(dataFile, true);
        Customer sampleCustomer = bank.findCustomerByEmail(SampleBankFactory.SAMPLE_CUSTOMER_EMAIL);

        assertNotNull(sampleCustomer);
        assertEquals(1, bank.getCustomers().size());
        assertEquals(SampleBankFactory.SAMPLE_CUSTOMER_NAME, sampleCustomer.getName());
        assertEquals(2, sampleCustomer.getAccounts().size());
    }
}
