package main;

public final class CustomerAuthenticator {
    private final Bank bank;

    public CustomerAuthenticator(Bank bank) {
        this.bank = bank;
    }

    public Customer findCustomer(String email) {
        return bank.findCustomerByEmail(email);
    }

    public boolean isPasswordValid(Customer customer, String password) {
        return customer != null && customer.verifyPassword(password);
    }

    public boolean isPinValid(Customer customer, String pin) {
        return customer != null && customer.verifyPin(pin);
    }
}
