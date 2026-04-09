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

    public void freezeAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        account.freezeAccount();
    }

    public void unfreezeAccount(BankAccount account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }
        account.unfreezeAccount();
    }

    public String getAllCustomersHistory() {
        StringBuilder historyBuilder = new StringBuilder();
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            historyBuilder.append("Customer: ").append(customer.getName()).append("\n");
            List<BankAccount> accounts = customer.getAccounts();
            for (int j = 0; j < accounts.size(); j++) {
                BankAccount account = accounts.get(j);
                historyBuilder.append("  Account #").append(j + 1).append(":\n");
                String history = account.getTransactionHistory();
                if (history.isEmpty()) {
                    historyBuilder.append("    No transactions yet.\n");
                } else {
                    historyBuilder.append("    ").append(history.replace("\n", "\n    ")).append("\n");
                }
            }
        }
        return historyBuilder.toString().trim();
    }
}