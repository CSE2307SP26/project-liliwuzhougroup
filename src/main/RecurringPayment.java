package main;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class RecurringPayment implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Frequency { DAILY, WEEKLY, MONTHLY }

    private final String description;
    private final int sourceAccountIndex;
    private final int targetAccountIndex;
    private final double amount;
    private final Frequency frequency;
    private LocalDate nextPaymentDate;

    public RecurringPayment(String description, int sourceAccountIndex, int targetAccountIndex,
                            double amount, Frequency frequency, LocalDate nextPaymentDate) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be blank.");
        }
        if (sourceAccountIndex < 0 || targetAccountIndex < 0) {
            throw new IllegalArgumentException("Account indices cannot be negative.");
        }
        if (sourceAccountIndex == targetAccountIndex) {
            throw new IllegalArgumentException("Source and target accounts must be different.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (frequency == null) {
            throw new IllegalArgumentException("Frequency is required.");
        }
        if (nextPaymentDate == null) {
            throw new IllegalArgumentException("Next payment date is required.");
        }

        this.description = description.trim();
        this.sourceAccountIndex = sourceAccountIndex;
        this.targetAccountIndex = targetAccountIndex;
        this.amount = amount;
        this.frequency = frequency;
        this.nextPaymentDate = nextPaymentDate;
    }

    public String getDescription() {
        return description;
    }

    public int getSourceAccountIndex() {
        return sourceAccountIndex;
    }

    public int getTargetAccountIndex() {
        return targetAccountIndex;
    }

    public double getAmount() {
        return amount;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public LocalDate getNextPaymentDate() {
        return nextPaymentDate;
    }

    public boolean isDue(LocalDate processingDate) {
        if (processingDate == null) {
            throw new IllegalArgumentException("Processing date is required.");
        }
        return !nextPaymentDate.isAfter(processingDate);
    }

    public void process(List<BankAccount> accounts, LocalDate processingDate) {
        if (accounts == null) {
            throw new IllegalArgumentException("Accounts cannot be null.");
        }
        if (!isDue(processingDate)) {
            throw new IllegalStateException("Payment is not due yet.");
        }

        BankAccount sourceAccount = getAccount(accounts, sourceAccountIndex, "Source");
        BankAccount targetAccount = getAccount(accounts, targetAccountIndex, "Target");
        sourceAccount.transferMoney(targetAccount, amount);
        advanceNextPaymentDate(processingDate);
    }

    private BankAccount getAccount(List<BankAccount> accounts, int accountIndex, String label) {
        if (accountIndex >= accounts.size()) {
            throw new IllegalArgumentException(label + " account no longer exists.");
        }
        return accounts.get(accountIndex);
    }

    private void advanceNextPaymentDate(LocalDate processingDate) {
        while (!nextPaymentDate.isAfter(processingDate)) {
            switch (frequency) {
                case DAILY:
                    nextPaymentDate = nextPaymentDate.plusDays(1);
                    break;
                case WEEKLY:
                    nextPaymentDate = nextPaymentDate.plusWeeks(1);
                    break;
                case MONTHLY:
                    nextPaymentDate = nextPaymentDate.plusMonths(1);
                    break;
                default:
                    throw new IllegalStateException("Unsupported frequency.");
            }
        }
    }

    @Override
    public String toString() {
        return description + " | $" + amount + " | " + frequency
                + " | Acct #" + (sourceAccountIndex + 1) + " -> #" + (targetAccountIndex + 1)
                + " | Next payment: " + nextPaymentDate;
    }
}
