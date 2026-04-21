package main;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TransactionTest {

    @Test
    public void testTransactionStoresValuesAndFormatsOutput() {
        Transaction transaction = new Transaction(LocalDate.of(2026, 4, 21), "Deposited: 50.0", 50.0);

        assertEquals(LocalDate.of(2026, 4, 21), transaction.getDate());
        assertEquals("Deposited: 50.0", transaction.getDescription());
        assertEquals(50.0, transaction.getAmount(), 0.001);
        assertEquals("2026-04-21 | Deposited: 50.0 | $50.0", transaction.toString());
    }

    @Test
    public void testConstructorRejectsNullDate() {
        try {
            new Transaction(null, "Deposited: 50.0", 50.0);
            fail();
        } catch (NullPointerException e) {
            assertEquals("date cannot be null.", e.getMessage());
        }
    }

    @Test
    public void testConstructorRejectsNullDescription() {
        try {
            new Transaction(LocalDate.of(2026, 4, 21), null, 50.0);
            fail();
        } catch (NullPointerException e) {
            assertEquals("description cannot be null.", e.getMessage());
        }
    }
}
