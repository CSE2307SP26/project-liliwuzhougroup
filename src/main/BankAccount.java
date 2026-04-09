package main;

import java.io.Serializable;

public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private double balance;
    private String transactionHistory;
    private double maxWithdrawAmount;

    public BankAccount() {
        this.balance = 0;
        this.transactionHistory = "";
        this.maxWithdrawAmount = Double.MAX_VALUE;
    }

    public BankAccount(double balance, String transactionHistory) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.maxWithdrawAmount = Double.MAX_VALUE;
    }

    // add constructor with max withdraw amount
    public BankAccount(double balance, String transactionHistory, double maxWithdrawAmount) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;

        if (maxWithdrawAmount <= 0) {
            this.maxWithdrawAmount = Double.MAX_VALUE;
        } else {
            this.maxWithdrawAmount = maxWithdrawAmount;
        }
    }

    public void deposit(double amount) {
        if(amount > 0) {
            this.balance += amount;
            this.transactionHistory += "Deposited: " + amount + "\n";
        } else {
            throw new IllegalArgumentException();
        }
    }
    
    public void addInterest(double interestAmount) {
        if (interestAmount > 0) {
            this.balance += interestAmount;
            this.transactionHistory += "Interest added: " + interestAmount + "\n";
        } else {
            throw new IllegalArgumentException();
        }
    }

    // collect fee from the account
    public void collectFee(double feeAmount) {
        if (feeAmount > 0) {
            this.balance -= feeAmount;
            this.transactionHistory += "Fee collected: " + feeAmount + "\n";
        } else {
            throw new IllegalArgumentException();
        }
    }

    public double getBalance() {
        return this.balance;
    }

    public String getTransactionHistory() {
        return this.transactionHistory;
    }


    public double getMaxWithdrawAmount() {
        return this.maxWithdrawAmount;
    }

    // set a max amount first
    public void setMaxWithdrawAmount(double maxWithdrawAmount) {
        if (maxWithdrawAmount <= 0) {
            throw new IllegalArgumentException("Maximum withdrawal amount must be greater than 0.");
        }

        this.maxWithdrawAmount = maxWithdrawAmount;
        this.transactionHistory += "Maximum withdrawal amount set to: " + maxWithdrawAmount + "\n";
    }

    // withdraw
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0.");
        }

        if (amount > maxWithdrawAmount) {
            throw new IllegalArgumentException("Withdrawal amount exceeds the maximum amount");
        }
        // not enought balance
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        balance -= amount;
        this.transactionHistory += "Withdrew: " + amount + "\n";
    }

    public void transferMoney(BankAccount targetAccount, double amount) {
        if (targetAccount == null) {
            throw new IllegalArgumentException("Source and target accounts cannot be null.");
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
}