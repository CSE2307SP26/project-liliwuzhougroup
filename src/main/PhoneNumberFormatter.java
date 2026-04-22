package main;

public final class PhoneNumberFormatter {
    private PhoneNumberFormatter() {
    }

    public static String normalizeRequired(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty.");
        }

        String trimmed = phoneNumber.trim();
        if (!trimmed.matches("[0-9()\\-\\s]+")) {
            throw new IllegalArgumentException(
                    "Phone number can contain only digits, spaces, parentheses, and hyphens."
            );
        }

        String digitsOnly = trimmed.replaceAll("\\D", "");
        if (digitsOnly.length() == 11 && digitsOnly.startsWith("1")) {
            digitsOnly = digitsOnly.substring(1);
        }
        if (digitsOnly.length() != 10) {
            throw new IllegalArgumentException("Phone number must contain exactly 10 digits.");
        }
        return digitsOnly;
    }

    public static String normalizeOptional(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return phoneNumber;
        }
        return normalizeRequired(phoneNumber);
    }
}
