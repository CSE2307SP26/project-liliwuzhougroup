package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private double balance;
    private String transactionHistory;
    private ArrayList<Fee> fees;

    public BankAccount() {
        this.balance = 0;
        this.transactionHistory = "";
        this.fees = new ArrayList<>();
    }

    public BankAccount(double balance, String transactionHistory) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.fees = new ArrayList<>();
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

    public void createFee(Fee fee) {
        if (fee == null) {
            throw new IllegalArgumentException("Fee cannot be null.");
        }
        this.fees.add(fee);
    }

    public List<Fee> getRemainingFees() {
        return new ArrayList<>(this.fees);
    }


}
