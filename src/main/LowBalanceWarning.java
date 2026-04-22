package main;

import java.util.Scanner;

public class LowBalanceWarning {

    private final MenuInput io;

    public LowBalanceWarning(Scanner scanner) {
        this.io = new MenuInput(scanner);
    }

    public boolean confirmIfNeeded(BankAccount account, double amount) {
        if (!account.wouldTriggerLowBalanceWarning(amount)) {
            return true;
        }
        double resultingBalance = account.getBalance() - amount;
        System.out.println("Warning: This will leave your balance at $" + resultingBalance
                + ", below your low balance threshold of $" + account.getLowBalanceThreshold() + ".");
        System.out.println("1. Yes");
        System.out.println("2. No");
        return io.readSelection(2) == 1;
    }

    public void setThreshold(BankAccount account) {
        io.prepareForTextInput();
        double threshold = io.readNonNegativeAmount("Enter low balance threshold (0 to disable): ");
        account.setLowBalanceThreshold(threshold);
        System.out.println("Low balance threshold updated.");
    }
}
