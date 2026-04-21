package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

final class AccountSnapshot implements Serializable {
    private static final long serialVersionUID = 2L;

    private final double balance;
    private final String transactionHistory;
    private final boolean frozen;
    private final double maxWithdrawAmount;
    private final List<FeeSnapshot> fees;
    private final List<Transaction> transactions;

    AccountSnapshot(BankAccount account) {
        this.balance = account.getBalance();
        this.transactionHistory = account.getTransactionHistory();
        this.frozen = account.isFrozen();
        this.maxWithdrawAmount = account.getMaxWithdrawAmount();
        this.fees = toFeeSnapshots(account.getRemainingFees());
        this.transactions = new ArrayList<>(account.getTransactions());
    }

    BankAccount toAccount() {
        BankAccount account = new BankAccount(
                balance,
                transactionHistory,
                frozen,
                maxWithdrawAmount
        );
        restoreFees(account);
        restoreTransactions(account);
        return account;
    }

    private void restoreFees(BankAccount account) {
        for (FeeSnapshot fee : fees) {
            account.createFee(fee.toFee());
        }
    }

    private void restoreTransactions(BankAccount account) {
        for (Transaction t : transactions) {
            account.addTransactionRecord(t);
        }
    }

    private List<FeeSnapshot> toFeeSnapshots(List<Fee> source) {
        List<FeeSnapshot> snapshots = new ArrayList<FeeSnapshot>();
        for (Fee fee : source) {
            snapshots.add(new FeeSnapshot(fee));
        }
        return snapshots;
    }
}
