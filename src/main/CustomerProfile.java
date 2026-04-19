package main;

import java.io.Serializable;

final class CustomerProfile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private String address;
    private String phoneNumber;
    private String email;

    CustomerProfile(String name) {
        this.name = requireValue(name, "Name");
    }

    CustomerProfile(String name, String address, String phoneNumber, String email) {
        this(name);
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    String getName() {
        return name;
    }

    String getAddress() {
        return address;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    String getEmail() {
        return email;
    }

    void updatePersonalInformation(String address, String phoneNumber, String email) {
        this.address = requireValue(address, "Address");
        this.phoneNumber = requireValue(phoneNumber, "Phone number");
        this.email = requireValue(email, "Email");
    }

    private String requireValue(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
