package test;

import main.AdminAuth;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class AdminAuthEdgeCaseTest {

    @Test
    public void testNullPasswordIsRejected() {
        assertFalse(AdminAuth.isValidPassword(null));
    }
}
