package test;

import main.PhoneNumberFormatter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PhoneNumberFormatterTest {

    @Test
    public void testNormalizeRequiredAcceptsCommonPhoneFormats() {
        assertEquals("5551234567", PhoneNumberFormatter.normalizeRequired("(555) 123-4567"));
        assertEquals("5551234567", PhoneNumberFormatter.normalizeRequired("1 555 123 4567"));
    }

    @Test
    public void testNormalizeRequiredRejectsUnsupportedCharacters() {
        try {
            PhoneNumberFormatter.normalizeRequired("555-ABC-1234");
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals(
                    "Phone number can contain only digits, spaces, parentheses, and hyphens.",
                    e.getMessage()
            );
        }
    }

    @Test
    public void testNormalizeOptionalPreservesBlankInputAndNormalizesNumbers() {
        assertEquals("", PhoneNumberFormatter.normalizeOptional(""));
        assertEquals("5551234567", PhoneNumberFormatter.normalizeOptional("555.123.4567".replace('.', '-')));
    }
}
