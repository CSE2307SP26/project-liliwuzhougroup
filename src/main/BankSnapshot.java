package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

final class BankSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<CustomerSnapshot> customers;

    BankSnapshot(Bank bank) {
        this.customers = toCustomerSnapshots(bank.getCustomers());
    }

    Bank toBank() {
        Bank bank = new Bank();
        for (CustomerSnapshot customer : customers) {
            bank.addCustomer(customer.toCustomer());
        }
        return bank;
    }

    private List<CustomerSnapshot> toCustomerSnapshots(List<Customer> source) {
        List<CustomerSnapshot> snapshots = new ArrayList<CustomerSnapshot>();
        for (Customer customer : source) {
            snapshots.add(new CustomerSnapshot(customer));
        }
        return snapshots;
    }
}
