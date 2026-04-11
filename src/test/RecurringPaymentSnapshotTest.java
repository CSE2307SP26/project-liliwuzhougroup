package main;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class RecurringPaymentSnapshotTest {

    @Test
    public void testToRecurringPaymentRestoresAllValues() {
        LocalDate nextDate = LocalDate.now().plusDays(2);
        RecurringPayment payment = new RecurringPayment(
                "Rent",
                0,
                1,
                400.0,
                RecurringPayment.Frequency.MONTHLY,
                nextDate
        );

        RecurringPayment restored =
                new RecurringPaymentSnapshot(payment).toRecurringPayment();

        assertEquals("Rent", restored.getDescription());
        assertEquals(0, restored.getSourceAccountIndex());
        assertEquals(1, restored.getTargetAccountIndex());
        assertEquals(400.0, restored.getAmount(), 0.01);
        assertEquals(RecurringPayment.Frequency.MONTHLY, restored.getFrequency());
        assertEquals(nextDate, restored.getNextPaymentDate());
    }
}
