package test;

import org.junit.Test;
import main.Fee;

import java.util.Date;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FeeTest {
    @Test
    public void testFeeCreation() {
        // Test creating a fee with valid parameters
        try {
            Fee fee = new Fee(10.0, "Monthly maintenance fee", new Date());
            assertEquals(10.0, fee.getAmount(), 0.001);
            assertEquals("Monthly maintenance fee", fee.getDescription());
            assertNotNull(fee.getDueDate());
        } catch (IllegalArgumentException e) {
            fail("Should not throw an exception for valid fee creation");
        }
    }

    @Test
    public void testFeeCreationWithNegativeAmount() {
        // Test creating a fee with a negative amount
        try {
            new Fee(-5.0, "Invalid fee", new Date());
            fail("Should throw an exception for negative fee amount");
        } catch (IllegalArgumentException e) {
            assertEquals("Fee amount cannot be negative.", e.getMessage());
        }
    }

    @Test
    public void testFeeCreationWithZeroAmount() {
        // Test creating a fee with zero amount
        try {
            Fee fee = new Fee(0.0, "No fee", new Date());
            assertEquals(0.0, fee.getAmount(), 0.001);
            assertEquals("No fee", fee.getDescription());
            assertNotNull(fee.getDueDate());
        } catch (IllegalArgumentException e) {
            fail("Should not throw an exception for zero fee amount");
        }
    }

    @Test
    public void testFeeDueDate() {
        // Test that the due date is set correctly
        try {
            Date dueDate = new Date();
            Fee fee = new Fee(15.0, "Late payment fee", dueDate);
            assertEquals(dueDate, fee.getDueDate());
        } catch (IllegalArgumentException e) {
            fail("Should not throw an exception for valid fee creation");
        }
    }

    @Test
    public void testFeeCopiesProvidedDueDate() {
        Date dueDate = new Date(System.currentTimeMillis() + 86400000);
        Fee fee = new Fee(15.0, "Late payment fee", dueDate);

        dueDate.setTime(dueDate.getTime() + 86400000);

        assertNotEquals(dueDate, fee.getDueDate());
    }

    @Test
    public void testFeeReturnsDefensiveCopyOfDueDate() {
        Fee fee = new Fee(15.0, "Late payment fee", new Date(System.currentTimeMillis() + 86400000));

        Date returnedDueDate = fee.getDueDate();
        returnedDueDate.setTime(returnedDueDate.getTime() + 86400000);

        assertNotEquals(returnedDueDate, fee.getDueDate());
    }

    @Test
    public void testFeeCreationWithPastDate() {
        try {
            new Fee(10.0, "Invalid date fee", new Date(1000000000));
            fail("Should not allow set a date before today");
        } catch (IllegalArgumentException e) {
            assertEquals("Due date cannot be in the past.", e.getMessage());
        }
    }

    @Test
    public void testFeeCreationRejectsNullDate() {
        try {
            new Fee(10.0, "Missing date fee", null);
            fail("Should require a due date");
        } catch (IllegalArgumentException e) {
            assertEquals("Due date is required.", e.getMessage());
        }
    }

    @Test
    public void testFeeCreationRejectsBlankDescription() {
        try {
            new Fee(10.0, "   ", new Date(System.currentTimeMillis() + 86400000));
            fail("Should require a description");
        } catch (IllegalArgumentException e) {
            assertEquals("Fee description cannot be blank.", e.getMessage());
        }
    }
}
