package main;

public class TransferMoney {
    public void transferMoney(BankAccount sourceAccount, BankAccount targetAccount, double amount) {
        if (sourceAccount == null || targetAccount == null) {
            throw new IllegalArgumentException("Source and target accounts cannot be null.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }
        if (sourceAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds in source account.");
        }
        sourceAccount.withdraw(amount);
        targetAccount.deposit(amount);
    }
}
