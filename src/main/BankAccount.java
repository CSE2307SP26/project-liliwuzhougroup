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
        throw new IllegalArgumentException("Withdrawal amount must be greater than 0.");
    }
    if (amount > balance) {
        throw new IllegalArgumentException("You are overdrafting your account.");
    }
    balance -= amount;
    }
}
