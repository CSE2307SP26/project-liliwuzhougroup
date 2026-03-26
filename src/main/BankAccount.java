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

    public double getBalance() {
        return this.balance;
    }

    //withdraw
    public void withdraw(double amount) {
    if (amount <= 0) {
        throw new IllegalArgumentException();
    }
    if (amount > balance) {
        throw new IllegalArgumentException();
    }
    balance -= amount;
    }
}