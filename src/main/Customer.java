package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final ArrayList<BankAccount> accounts;
    private String password;
    private String pin;

    public Customer(String name) {
        this.name = name;
        this.accounts = new ArrayList<>();
        openAccount();
    }

    public Customer(String name, BankAccount account) {
        this.name = name;
        this.accounts = new ArrayList<>();
        openAccount(account);
    }

    public Customer(String name, List<BankAccount> accounts) {
        this.name = name;
        this.accounts = new ArrayList<>();
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
}
