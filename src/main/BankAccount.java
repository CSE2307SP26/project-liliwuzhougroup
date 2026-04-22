package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BankAccount implements Serializable {

    private static final long serialVersionUID = 1L;

    private double balance;
    private String transactionHistory;
    private ArrayList<Fee> fees;
    private boolean frozen;
    private double maxWithdrawAmount;
    private double lowBalanceThreshold;

    public BankAccount() {
        this.balance = 0;
        this.transactionHistory = "";
        this.fees = new ArrayList<>();
        this.frozen = false;
        this.maxWithdrawAmount = Double.MAX_VALUE;
        this.lowBalanceThreshold = 0;
    }

    public BankAccount(double balance, String transactionHistory) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.fees = new ArrayList<>();
        this.frozen = false;
        this.maxWithdrawAmount = Double.MAX_VALUE;
        this.lowBalanceThreshold = 0;
    }

    public BankAccount(double balance, String transactionHistory, boolean frozen) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.fees = new ArrayList<>();
        this.frozen = frozen;
        this.maxWithdrawAmount = Double.MAX_VALUE;
        this.lowBalanceThreshold = 0;
    }

    public BankAccount(double balance, String transactionHistory, boolean frozen, double maxWithdrawAmount) {
        this.balance = balance;
        this.transactionHistory = transactionHistory == null ? "" : transactionHistory;
        this.fees = new ArrayList<>();
        this.frozen = frozen;
        this.maxWithdrawAmount = maxWithdrawAmount <= 0 ? Double.MAX_VALUE : maxWithdrawAmount;
        this.lowBalanceThreshold = 0;
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
        this.lowBalanceThreshold = 0;
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

    public double getMaxWithdrawAmount() {
        return this.maxWithdrawAmount;
    }

    public void setMaxWithdrawAmount(double maxWithdrawAmount) {
        if (maxWithdrawAmount <= 0) {
            throw new IllegalArgumentException("Maximum withdrawal amount must be greater than 0.");
        }

        this.maxWithdrawAmount = maxWithdrawAmount;
        this.transactionHistory += "Maximum withdrawal amount set to: " + maxWithdrawAmount + "\n";
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
        this.transactionHistory += "Withdrew: " + amount + "\n";
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
        this.transactionHistory += "Paid fee: " + fee.getAmount() + " for " + fee.getDescription() + "\n";
    }

    public List<Fee> getRemainingFees() {
        return new ArrayList<>(this.fees);
    }

    public double getLowBalanceThreshold() {
        return this.lowBalanceThreshold;
    }

    public void setLowBalanceThreshold(double threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Low balance threshold cannot be negative.");
        }
        this.lowBalanceThreshold = threshold;
    }

    public boolean wouldTriggerLowBalanceWarning(double amount) {
        return lowBalanceThreshold > 0 && (balance - amount) < lowBalanceThreshold;
    }

    private void ensureAccountIsActive() {
        if (this.frozen) {
            throw new IllegalStateException("This account is frozen. Transactions are not allowed.");
        }
    }
}
