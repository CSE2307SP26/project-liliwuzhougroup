package main;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class FeeSnapshotTest {

    @Test
    public void testToFeeRestoresAmountDescriptionAndDate() {
        Date dueDate = new Date(System.currentTimeMillis() + 86400000);
        Fee fee = new Fee(8.5, "Late fee", dueDate);

        Fee restored = new FeeSnapshot(fee).toFee();

        assertEquals(8.5, restored.getAmount(), 0.01);
        assertEquals("Late fee", restored.getDescription());
        assertEquals(dueDate, restored.getDueDate());
    }
}
