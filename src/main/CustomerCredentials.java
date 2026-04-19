package main;

import java.io.Serializable;

final class CustomerCredentials implements Serializable {
    private static final long serialVersionUID = 1L;

    private String password;
    private String pin;

    CustomerCredentials() {
    }

    CustomerCredentials(String password, String pin) {
        this.password = password;
        this.pin = pin;
    }

    void setPassword(String password) {
        this.password = requireValue(password, "Password");
    }

    void setPin(String pin) {
        if (pin == null || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be exactly 4 digits.");
        }
        this.pin = pin;
    }

    boolean verifyPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    boolean verifyPin(String pin) {
        return this.pin != null && this.pin.equals(pin);
    }

    String getStoredPassword() {
        return password;
    }

    String getStoredPin() {
        return pin;
    }

    private String requireValue(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
