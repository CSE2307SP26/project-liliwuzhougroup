package main;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CustomerProfile profile;
    private final CustomerCredentials credentials;
    private final ArrayList<BankAccount> accounts;
    private final ArrayList<RecurringPayment> recurringPayments;

    public Customer(String name) {
        this(new CustomerProfile(name), new CustomerCredentials(), null, null, true);
    }

    public Customer(String name, BankAccount account) {
        this(new CustomerProfile(name), new CustomerCredentials(), null, null, false);
        openAccount(account);
    }

    public Customer(String name, List<BankAccount> accounts) {
        this(new CustomerProfile(name), new CustomerCredentials(), accounts, null, true);
    }

    Customer(CustomerProfile profile, CustomerCredentials credentials,
             List<BankAccount> accounts, List<RecurringPayment> recurringPayments) {
        this(profile, credentials, accounts, recurringPayments, true);
    }

    private Customer(CustomerProfile profile, CustomerCredentials credentials,
                     List<BankAccount> accounts, List<RecurringPayment> recurringPayments,
                     boolean createDefaultAccount) {
        this.profile = profile;
        this.credentials = credentials;
        this.accounts = new ArrayList<>();
        this.recurringPayments = new ArrayList<>();
        if (accounts != null) {
            this.accounts.addAll(accounts);
        }
        if (recurringPayments != null) {
            this.recurringPayments.addAll(recurringPayments);
        }
        if (createDefaultAccount && this.accounts.isEmpty()) {
            this.accounts.add(new BankAccount());
        }
    }

    public String getName() {
        return profile.getName();
    }

    public String getAddress() {
        return profile.getAddress();
    }

    public String getPhoneNumber() {
        return profile.getPhoneNumber();
    }

    public String getEmail() {
        return profile.getEmail();
    }

    CustomerCredentials getCredentials() {
        return credentials;
    }

    public List<BankAccount> getAccounts() {
        return Collections.unmodifiableList(this.accounts);
    }

    public void updatePersonalInformation(String address, String phoneNumber, String email) {
        profile.updatePersonalInformation(address, phoneNumber, email);
    }

    public void setPassword(String password) {
        credentials.setPassword(password);
    }

    public void setPin(String pin) {
        credentials.setPin(pin);
    }

    public boolean verifyPassword(String password) {
        return credentials.verifyPassword(password);
    }

    public boolean verifyPin(String pin) {
        return credentials.verifyPin(pin);
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
}
