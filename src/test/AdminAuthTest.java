package test;

import main.AdminAuth;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AdminAuthTest {

    @Test
    public void testValidPassword() {
        assertTrue(AdminAuth.isValidPassword("admin123"));
    }

    @Test
    public void testInvalidPassword() {
        assertFalse(AdminAuth.isValidPassword("wrong"));
    }
}
