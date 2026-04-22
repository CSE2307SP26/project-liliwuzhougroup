package main;

import java.io.Serializable;

public final class CustomerCredentials implements Serializable {
    private static final long serialVersionUID = 1L;

    private String password;
    private String pin;

    public CustomerCredentials() {
    }

    public CustomerCredentials(String password, String pin) {
        this.password = password;
        this.pin = pin;
    }

    public void setPassword(String password) {
        this.password = requireValue(password, "Password");
    }

    public void setPin(String pin) {
        if (pin == null || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be exactly 4 digits.");
        }
        this.pin = pin;
    }

    public boolean verifyPassword(String password) {
        return this.password != null && this.password.equals(password);
    }

    public boolean verifyPin(String pin) {
        return this.pin != null && this.pin.equals(pin);
    }

    public String getStoredPassword() {
        return password;
    }

    public String getStoredPin() {
        return pin;
    }

    private String requireValue(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
