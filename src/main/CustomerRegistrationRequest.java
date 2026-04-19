package main;

final class CustomerRegistrationRequest {
    private final String name;
    private final String address;
    private final String phoneNumber;
    private final String email;
    private final String password;
    private final String pin;

    CustomerRegistrationRequest(String name, String address, String phoneNumber,
                                String email, String password, String pin) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.pin = pin;
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

    String getPassword() {
        return password;
    }

    String getPin() {
        return pin;
    }
}
