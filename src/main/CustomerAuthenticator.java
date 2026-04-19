package main;

final class CustomerAuthenticator {
    private final Bank bank;

    CustomerAuthenticator(Bank bank) {
        this.bank = bank;
    }

    Customer findCustomer(String email) {
        return bank.findCustomerByEmail(email);
    }

    boolean isPasswordValid(Customer customer, String password) {
        return customer != null && customer.verifyPassword(password);
    }

    boolean isPinValid(Customer customer, String pin) {
        return customer != null && customer.verifyPin(pin);
    }
}
