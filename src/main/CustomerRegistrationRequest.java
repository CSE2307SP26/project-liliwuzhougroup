package main;

public final class CustomerRegistrationRequest {
    private final String name;
    private final String address;
    private final String phoneNumber;
    private final String email;
    private final String password;
    private final String pin;

    public CustomerRegistrationRequest(String name, String address, String phoneNumber,
                                String email, String password, String pin) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.pin = pin;
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

    public String getPassword() {
        return password;
    }

    public String getPin() {
        return pin;
    }
}
