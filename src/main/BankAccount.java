package main;

import java.io.Serializable;

public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private double balance;
    private String transactionHistory;
    private boolean frozen;

    public BankAccount() {
        this.balance = 0;
        this.transactionHistory = "";
        this.frozen = false;
    }

    public BankAccount(double balance, String transactionHistory) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.frozen = false;
    }

    public BankAccount(double balance, String transactionHistory, boolean frozen) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.frozen = frozen;
    }

    public void deposit(double amount) {
        ensureAccountIsActive();
        if (amount > 0) {
            this.balance += amount;
            this.transactionHistory += "Deposited: " + amount + "\n";
        } else {
            throw new IllegalArgumentException("Deposit amount must be greater than 0.");
        }
    }

    public void addInterest(double interestAmount) {
        if (interestAmount > 0) {
            this.balance += interestAmount;
            this.transactionHistory += "Interest added: " + interestAmount + "\n";
        } else {
            throw new IllegalArgumentException("Interest amount must be greater than 0.");
        }
    }

    public void collectFee(double feeAmount) {
        if (feeAmount > 0) {
            this.balance -= feeAmount;
            this.transactionHistory += "Fee collected: " + feeAmount + "\n";
        } else {
            throw new IllegalArgumentException("Fee amount must be greater than 0.");
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public String getTransactionHistory() {
        return this.transactionHistory;
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    public void freezeAccount() {
        if (this.frozen) {
            throw new IllegalStateException("This account is already frozen.");
        }
        this.frozen = true;
        this.transactionHistory += "Account frozen.\n";
    }

    public void unfreezeAccount() {
        if (!this.frozen) {
            throw new IllegalStateException("This account is not frozen.");
        }
        this.frozen = false;
        this.transactionHistory += "Account unfrozen.\n";
    }

    public void withdraw(double amount) {
        ensureAccountIsActive();

        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0.");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("You are overdrafting your account.");
        }

        balance -= amount;
        this.transactionHistory += "Withdrew: " + amount + "\n";
    }

    public void transferMoney(BankAccount targetAccount, double amount) {
        ensureAccountIsActive();

        if (targetAccount == null) {
            throw new IllegalArgumentException("Target account cannot be null.");
        }
        if (targetAccount.isFrozen()) {
            throw new IllegalStateException("You cannot transfer money into a frozen account.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (this.balance < amount) {
            throw new IllegalArgumentException("Insufficient funds in source account.");
        }

        this.withdraw(amount);
        targetAccount.deposit(amount);
    }

    private void ensureAccountIsActive() {
        if (this.frozen) {
            throw new IllegalStateException("This account is frozen. Transactions are not allowed.");
        }
    }
}