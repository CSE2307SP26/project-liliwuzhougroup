package main;

import java.io.Serializable;

public final class CustomerProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private String address;
    private String phoneNumber;
    private String email;

    public CustomerProfile(String name) {
        this.name = requireValue(name, "Name");
    }

    public CustomerProfile(String name, String address, String phoneNumber, String email) {
        this(name);
        this.address = address;
        this.phoneNumber = PhoneNumberFormatter.normalizeOptional(phoneNumber);
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void updatePersonalInformation(String address, String phoneNumber, String email) {
        this.address = requireValue(address, "Address");
        this.phoneNumber = PhoneNumberFormatter.normalizeRequired(phoneNumber);
        this.email = requireValue(email, "Email");
    }

    private String requireValue(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
