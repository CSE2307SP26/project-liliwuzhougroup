package main;

import java.io.Serializable;

public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private double balance;
    private String transactionHistory;

    public BankAccount() {
        this.balance = 0;
        this.transactionHistory = "";
    }

    public BankAccount(double balance, String transactionHistory) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
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

    //collect fee from the account, this will be used by the bank to collect fees for transactions
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
    //withdraw
    public void withdraw(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0.");
        }if (amount > balance) {
            throw new IllegalArgumentException("You are overdrafting your account.");
        }
        balance -= amount;
        this.transactionHistory += "Withdrew: " + amount + "\n";
    }
}
