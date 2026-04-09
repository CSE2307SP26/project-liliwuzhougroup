package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final ArrayList<BankAccount> accounts;
    private final ArrayList<RecurringPayment> recurringPayments;
    private String address;
    private String phoneNumber;
    private String email;
    private String password;
    private String pin;

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
    }

    String getStoredPassword() {
        return this.password;
    }

    String getStoredPin() {
        return this.pin;
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

    public void setPassword(String password) {
        validatePersonalInfo(password, "Password");
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

    void restorePersonalInformation(String address, String phoneNumber, String email) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    void restorePassword(String password) {
        this.password = password;
    }

    void restorePin(String pin) {
        this.pin = pin;
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

    public void setupRecurringPayment(String description, int sourceIndex, int targetIndex,
                                      double amount, RecurringPayment.Frequency frequency) {
        validateRecurringPaymentAccountIndex(sourceIndex, "Source");
        validateRecurringPaymentAccountIndex(targetIndex, "Target");
        recurringPayments.add(new RecurringPayment(
                description,
                sourceIndex,
                targetIndex,
                amount,
                frequency,
                LocalDate.now()
        ));
    }

    public List<RecurringPayment> getRecurringPayments() {
        return Collections.unmodifiableList(recurringPayments);
    }

    void restoreRecurringPayment(RecurringPayment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Recurring payment cannot be null.");
        }
        recurringPayments.add(payment);
    }

    public int processRecurringPayments() {
        LocalDate processingDate = LocalDate.now();
        int processedCount = 0;
        for (RecurringPayment payment : recurringPayments) {
            if (!payment.isDue(processingDate)) {
                continue;
            }
            payment.process(accounts, processingDate);
            processedCount++;
        }
        return processedCount;
    }

    public void cancelRecurringPayment(int index) {
        if (index < 0 || index >= recurringPayments.size()) {
            throw new IllegalArgumentException("Invalid recurring payment selection.");
        }
        recurringPayments.remove(index);
    }

    private void validateRecurringPaymentAccountIndex(int accountIndex, String label) {
        if (accountIndex < 0 || accountIndex >= accounts.size()) {
            throw new IllegalArgumentException(label + " account selection is invalid.");
        }
    }

    private void validatePersonalInfo(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
    }
}
