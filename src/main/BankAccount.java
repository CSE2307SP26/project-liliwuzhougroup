package main;

public class BankAccount {

    private double balance;
    private String transactionHistory;

    public BankAccount() {
        this.balance = 0;
        this.transactionHistory = "";
    }

    public void deposit(double amount) {
        if(amount > 0) {
            this.balance += amount;
            this.transactionHistory += "Deposited: " + amount + "\n";
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
    }
}
