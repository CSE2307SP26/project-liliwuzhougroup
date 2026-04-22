package main;

import java.util.List;

public class BudgetAdvisor {

    private static final double LOW_BALANCE_THRESHOLD = 100.0;
    private static final double HEALTHY_BALANCE_THRESHOLD = 500.0;

    private final Customer customer;

    public BudgetAdvisor(Customer customer) {
        this.customer = customer;
    }

    public String generateAdvice() {
        List<BankAccount> accounts = customer.getAccounts();
        if (accounts.isEmpty()) {
            return "You have no accounts. Open an account to start tracking your finances.";
        }

        double totalBalance = 0;
        int depositCount = 0;
        int withdrawalCount = 0;
        double totalDeposited = 0;
        double totalWithdrawn = 0;

        for (BankAccount account : accounts) {
            totalBalance += account.getBalance();
            for (Transaction t : account.getTransactions()) {
                if (t.getAmount() > 0) {
                    depositCount++;
                    totalDeposited += t.getAmount();
                } else if (t.getAmount() < 0) {
                    withdrawalCount++;
                    totalWithdrawn += Math.abs(t.getAmount());
                }
            }
        }

        StringBuilder advice = new StringBuilder();
        advice.append("=== AI Budget Advice ===\n");
        advice.append(String.format("Total balance across all accounts: $%.2f%n", totalBalance));
        advice.append(String.format("Total deposited: $%.2f | Total withdrawn: $%.2f%n", totalDeposited, totalWithdrawn));

        if (depositCount == 0 && withdrawalCount == 0) {
            advice.append("No transactions found. Start making deposits to track your finances.");
            return advice.toString();
        }

        if (withdrawalCount > depositCount) {
            advice.append("Warning: You are making withdrawals more often than deposits. Consider reducing spending.\n");
        } else if (depositCount > withdrawalCount) {
            advice.append("Good habit: You deposit more often than you withdraw. Keep it up!\n");
        } else {
            advice.append("Your deposits and withdrawals are balanced in frequency.\n");
        }

        if (totalBalance < LOW_BALANCE_THRESHOLD) {
            advice.append("Your overall balance is low. Consider saving more before making large purchases.\n");
        } else if (totalBalance >= HEALTHY_BALANCE_THRESHOLD) {
            advice.append("Your overall balance looks healthy. Great financial discipline!\n");
        } else {
            advice.append("Your balance is moderate. Keep building your savings.\n");
        }

        if (totalWithdrawn > totalDeposited) {
            advice.append("Heads up: You have withdrawn more than you have deposited. Review your spending habits.\n");
        }

        return advice.toString();
    }
}
