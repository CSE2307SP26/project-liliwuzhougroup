package main;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private double balance;
    private String transactionHistory;
    private ArrayList<Fee> fees;
    private boolean frozen;
    private double maxWithdrawAmount;
    private List<Transaction> transactions;

    public BankAccount() {
        this.balance = 0;
        this.transactionHistory = "";
        this.fees = new ArrayList<>();
        this.frozen = false;
        this.maxWithdrawAmount = Double.MAX_VALUE;
        this.transactions = new ArrayList<>();
    }

    public BankAccount(double balance, String transactionHistory) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.fees = new ArrayList<>();
        this.frozen = false;
        this.maxWithdrawAmount = Double.MAX_VALUE;
        this.transactions = new ArrayList<>();
    }

    public BankAccount(double balance, String transactionHistory, boolean frozen) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.fees = new ArrayList<>();
        this.frozen = frozen;
        this.maxWithdrawAmount = Double.MAX_VALUE;
        this.transactions = new ArrayList<>();
    }

    public BankAccount(double balance, String transactionHistory, boolean frozen, double maxWithdrawAmount) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.fees = new ArrayList<>();
        this.frozen = frozen;
        this.maxWithdrawAmount = maxWithdrawAmount <= 0 ? Double.MAX_VALUE : maxWithdrawAmount;
        this.transactions = new ArrayList<>();
    }

    public BankAccount(double balance, String transactionHistory, double maxWithdrawAmount) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.fees = new ArrayList<>();
        this.frozen = false;
        if (maxWithdrawAmount <= 0) {
            this.maxWithdrawAmount = Double.MAX_VALUE;
        } else {
            this.maxWithdrawAmount = maxWithdrawAmount;
        }
        this.transactions = new ArrayList<>();
    }

    public void deposit(double amount) {
        ensureAccountIsActive();
        if (amount > 0) {
            this.balance += amount;
            recordTransaction("Deposited: " + amount, amount);
        } else {
            throw new IllegalArgumentException("Deposit amount must be greater than 0.");
        }
    }

    public void addInterest(double interestAmount) {
        if (interestAmount > 0) {
            this.balance += interestAmount;
            recordTransaction("Interest added: " + interestAmount, interestAmount);
        } else {
            throw new IllegalArgumentException("Interest amount must be greater than 0.");
        }
    }

    public void collectFee(double feeAmount) {
        if (feeAmount > 0) {
            this.balance -= feeAmount;
            recordTransaction("Fee collected: " + feeAmount, -feeAmount);
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
        recordTransaction("Account frozen.", 0.0);
    }

    public void unfreezeAccount() {
        if (!this.frozen) {
            throw new IllegalStateException("This account is not frozen.");
        }
        this.frozen = false;
        recordTransaction("Account unfrozen.", 0.0);
    }

    public double getMaxWithdrawAmount() {
        return this.maxWithdrawAmount;
    }

    public void setMaxWithdrawAmount(double maxWithdrawAmount) {
        if (maxWithdrawAmount <= 0) {
            throw new IllegalArgumentException("Maximum withdrawal amount must be greater than 0.");
        }

        this.maxWithdrawAmount = maxWithdrawAmount;
        recordTransaction("Maximum withdrawal amount set to: " + maxWithdrawAmount, 0.0);
    }

    public void withdraw(double amount) {
        ensureAccountIsActive();

        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than 0.");
        }

        if (amount > maxWithdrawAmount) {
            throw new IllegalArgumentException("Withdrawal amount exceeds the maximum amount");
        }
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds.");
        }
        balance -= amount;
        recordTransaction("Withdrew: " + amount, -amount);
    }

    public void transferMoney(BankAccount targetAccount, double amount) {
        ensureAccountIsActive();

        if (targetAccount == null) {
            throw new IllegalArgumentException("Source and target accounts cannot be null.");
        }
        if (targetAccount == this) {
            throw new IllegalArgumentException("Source and target accounts must be different.");
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

    public void createFee(Fee fee) {
        if (fee == null) {
            throw new IllegalArgumentException("Fee cannot be null.");
        }
        this.fees.add(fee);
    }

    public void payFee(int feeIndex) {
        ensureAccountIsActive();
        if (fees.isEmpty()) {
            throw new IllegalStateException("There are no pending fees to pay.");
        }
        if (feeIndex < 0 || feeIndex >= fees.size()) {
            throw new IllegalArgumentException("Invalid fee selection.");
        }

        Fee fee = fees.get(feeIndex);
        if (fee.getAmount() > balance) {
            throw new IllegalArgumentException("Insufficient funds to pay this fee.");
        }

        balance -= fee.getAmount();
        fees.remove(feeIndex);
        recordTransaction("Paid fee: " + fee.getAmount() + " for " + fee.getDescription(), -fee.getAmount());
    }

    public List<Fee> getRemainingFees() {
        return new ArrayList<>(this.fees);
    }

    public List<Transaction> getTransactions() {
        return new ArrayList<>(this.transactions);
    }

    public List<Transaction> getTransactionsByYearMonth(int year, int month) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : this.transactions) {
            if (t.getDate().getYear() == year && t.getDate().getMonthValue() == month) {
                result.add(t);
            }
        }
        return result;
    }

    void addTransactionRecord(Transaction t) {
        if (t == null) {
            throw new IllegalArgumentException("Transaction cannot be null.");
        }
        this.transactions.add(t);
    }

    private void recordTransaction(String description, double amount) {
        this.transactionHistory += description + "\n";
        this.transactions.add(new Transaction(LocalDate.now(), description, amount));
    }

    private void ensureAccountIsActive() {
        if (this.frozen) {
            throw new IllegalStateException("This account is frozen. Transactions are not allowed.");
        }
    }
}
