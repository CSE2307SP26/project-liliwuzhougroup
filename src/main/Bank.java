package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bank implements Serializable {
    private static final long serialVersionUID = 1L;

    private final ArrayList<Customer> customers;

    public Bank() {
        this.customers = new ArrayList<>();
    }

    public void addCustomer(Customer customer) {
        this.customers.add(customer);
    }

    public void removeCustomer(Customer customer) {
        this.customers.remove(customer);
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public void collectFee(BankAccount account, double feeAmount) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        account.collectFee(feeAmount);
    }

    public void addInterest(BankAccount account, double interestAmount) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        account.addInterest(interestAmount);
    }
}
