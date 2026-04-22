package main;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

final class AccountSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double balance;
    private final String transactionHistory;
    private final boolean frozen;
    private final double maxWithdrawAmount;
    private final List<FeeSnapshot> fees;
    private final double lowBalanceThreshold;

    AccountSnapshot(BankAccount account) {
        this.balance = account.getBalance();
        this.transactionHistory = account.getTransactionHistory();
        this.frozen = account.isFrozen();
        this.maxWithdrawAmount = account.getMaxWithdrawAmount();
        this.fees = toFeeSnapshots(account.getRemainingFees());
        this.lowBalanceThreshold = account.getLowBalanceThreshold();
    }

    BankAccount toAccount() {
        BankAccount account = new BankAccount(
                balance,
                transactionHistory,
                frozen,
                maxWithdrawAmount
        );
        restoreFees(account);
        if (lowBalanceThreshold > 0) {
            account.setLowBalanceThreshold(lowBalanceThreshold);
        }
        return account;
    }

    private void restoreFees(BankAccount account) {
        for (FeeSnapshot fee : fees) {
            account.createFee(fee.toFee());
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
