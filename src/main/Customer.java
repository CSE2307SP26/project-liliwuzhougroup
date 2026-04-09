package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    public static class RecurringPayment implements Serializable {
        private static final long serialVersionUID = 1L;

        public enum Frequency { DAILY, WEEKLY, MONTHLY }

        final String description;
        final int sourceAccountIndex;
        final int targetAccountIndex;
        final double amount;
        final Frequency frequency;

        public RecurringPayment(String description, int sourceIndex, int targetIndex,
                                double amount, Frequency frequency) {
            this.description = description;
            this.sourceAccountIndex = sourceIndex;
            this.targetAccountIndex = targetIndex;
            this.amount = amount;
            this.frequency = frequency;
        }

        @Override
        public String toString() {
            return description + " | $" + amount + " | " + frequency
                    + " | Acct #" + (sourceAccountIndex + 1) + " -> #" + (targetAccountIndex + 1);
        }
    }

    private final String name;
    private final ArrayList<BankAccount> accounts;
    private String address;
    private String phoneNumber;
    private String email;
    private String password;
    private String pin;
    private final ArrayList<RecurringPayment> recurringPayments;

    public Customer(String name) {
        this.name = name;
        this.accounts = new ArrayList<>();
        this.recurringPayments = new ArrayList<>();
        openAccount();
    }

    public Customer(String name, BankAccount account) {
        this.name = name;
        this.accounts = new ArrayList<>();
        this.recurringPayments = new ArrayList<>();
        openAccount(account);
    }

    public Customer(String name, List<BankAccount> accounts) {
        this.name = name;
        this.accounts = new ArrayList<>();
        this.recurringPayments = new ArrayList<>();
        if (accounts == null || accounts.isEmpty()) {
            openAccount();
            return;
        }
        for (BankAccount account : accounts) {
            openAccount(account);
        }
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public String getEmail() {
        return this.email;
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        this.password = password;
    }

    public void setPin(String pin) {
        if (pin == null || !pin.matches("\\d{4}")) {
            throw new IllegalArgumentException("PIN must be exactly 4 digits.");
        }
        this.pin = pin;
    }

    public boolean verifyPassword(String password) {
        if (this.password == null) {
            return false;
        }
        return this.password.equals(password);
    }

    public boolean verifyPin(String pin) {
        if (this.pin == null) {
            return false;
        }
        return this.pin.equals(pin);
    }

    public List<BankAccount> getAccounts() {
        return Collections.unmodifiableList(this.accounts);
    }

    public void updatePersonalInformation(String address, String phoneNumber, String email) {
        validatePersonalInfo(address, "Address");
        validatePersonalInfo(phoneNumber, "Phone number");
        validatePersonalInfo(email, "Email");

        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public BankAccount openAccount() {
        BankAccount account = new BankAccount();
        this.accounts.add(account);
        return account;
    }

    public void openAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        this.accounts.add(account);
    }

    public void closeAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        if (account.getBalance() != 0) {
            throw new IllegalStateException("Cannot close account with non-zero balance.");
        }
        this.accounts.remove(account);
    }

    private void validatePersonalInfo(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    public void setupRecurringPayment(String description, int sourceIndex, int targetIndex,
                                      double amount, RecurringPayment.Frequency frequency) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be blank.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (sourceIndex < 0 || targetIndex < 0) {
            throw new IllegalArgumentException("Account indices cannot be negative.");
        }
        recurringPayments.add(new RecurringPayment(description, sourceIndex, targetIndex, amount, frequency));
    }

    public List<RecurringPayment> getRecurringPayments() {
        return Collections.unmodifiableList(recurringPayments);
    }

    public void processRecurringPayments() {
        for (RecurringPayment payment : recurringPayments) {
            accounts.get(payment.sourceAccountIndex).transferMoney(
                    accounts.get(payment.targetAccountIndex), payment.amount);
        }
    }

    public void cancelRecurringPayment(int index) {
        recurringPayments.remove(index);
    }
}
