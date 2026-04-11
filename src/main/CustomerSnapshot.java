package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

final class CustomerSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String address;
    private final String phoneNumber;
    private final String email;
    private final String password;
    private final String pin;
    private final List<AccountSnapshot> accounts;
    private final List<RecurringPaymentSnapshot> recurringPayments;

    CustomerSnapshot(Customer customer) {
        this.name = customer.getName();
        this.address = customer.getAddress();
        this.phoneNumber = customer.getPhoneNumber();
        this.email = customer.getEmail();
        this.password = customer.getStoredPassword();
        this.pin = customer.getStoredPin();
        this.accounts = toAccountSnapshots(customer.getAccounts());
        this.recurringPayments = toPaymentSnapshots(customer.getRecurringPayments());
    }

    Customer toCustomer() {
        Customer customer = new Customer(name, toAccounts());
        restoreProfile(customer);
        restoreRecurringPayments(customer);
        return customer;
    }

    private void restoreProfile(Customer customer) {
        customer.restorePersonalInformation(address, phoneNumber, email);
        customer.restorePassword(password);
        customer.restorePin(pin);
    }

    private void restoreRecurringPayments(Customer customer) {
        for (RecurringPaymentSnapshot payment : recurringPayments) {
            customer.restoreRecurringPayment(payment.toRecurringPayment());
        }
    }

    private List<AccountSnapshot> toAccountSnapshots(List<BankAccount> source) {
        List<AccountSnapshot> snapshots = new ArrayList<AccountSnapshot>();
        for (BankAccount account : source) {
            snapshots.add(new AccountSnapshot(account));
        }
        return snapshots;
    }

    private List<RecurringPaymentSnapshot> toPaymentSnapshots(
            List<RecurringPayment> source) {
        List<RecurringPaymentSnapshot> snapshots =
                new ArrayList<RecurringPaymentSnapshot>();
        for (RecurringPayment payment : source) {
            snapshots.add(new RecurringPaymentSnapshot(payment));
        }
        return snapshots;
    }

    private List<BankAccount> toAccounts() {
        List<BankAccount> restored = new ArrayList<BankAccount>();
        for (AccountSnapshot account : accounts) {
            restored.add(account.toAccount());
        }
        return restored;
    }
}
