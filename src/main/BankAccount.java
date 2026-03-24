package main;

public class BankAccount {

    private double balance;

    public BankAccount() {
        this.balance = 0;
    }

    public void deposit(double amount) {
        if(amount > 0) {
            this.balance += amount;
        } else {
            throw new IllegalArgumentException();
        }
    }

    //collect fee from the account, this will be used by the bank to collect fees for transactions
    public void collectFee(double feeAmount) {
    if (feeAmount > 0) {
        this.balance -= feeAmount;
    } else {
        throw new IllegalArgumentException();
    }
}

    public double getBalance() {
        return this.balance;
    }
}
