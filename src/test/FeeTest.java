package test;

import org.junit.Test;
import main.Fee;

import java.util.Date;
import static org.junit.Assert.assertEquals;
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
            Fee fee = new Fee(-5.0, "Invalid fee", new Date());
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
    public void testSetInvalidDate() {
        // Test creating a fee with an invalid date (null)
        try {
            Fee fee = new Fee(10.0, "Invalid date fee", new Date(1000000000)); // A date in the past
            fail("Should not allow set a date before today");
        } catch (IllegalArgumentException e) {
            assertEquals("Due date cannot be in the past.", e.getMessage());
        }
    }
}
