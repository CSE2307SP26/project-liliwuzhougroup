package main;

public final class CustomerRegistrationService {
    private final Bank bank;

    public CustomerRegistrationService(Bank bank) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null.");
        }
        this.bank = bank;
    }

    public Customer register(CustomerRegistrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration request cannot be null.");
        }
        validateUniqueEmail(request.getEmail());
        Customer customer = new Customer(request.getName());
        customer.updatePersonalInformation(
                request.getAddress(),
                request.getPhoneNumber(),
                request.getEmail()
        );
        customer.setPassword(request.getPassword());
        applyPin(customer, request.getPin());
        bank.addCustomer(customer);
        return customer;
    }

    private void validateUniqueEmail(String email) {
        if (bank.findCustomerByEmail(email) != null) {
            throw new IllegalArgumentException("A customer with that email already exists.");
        }
    }

    private void applyPin(Customer customer, String pin) {
        if (pin == null || pin.trim().isEmpty()) {
            return;
        }
        customer.setPin(pin.trim());
    }
}
