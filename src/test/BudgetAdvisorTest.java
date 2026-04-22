package test;

import main.BankAccount;
import main.BudgetAdvisor;
import main.Customer;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class BudgetAdvisorTest {

    @Test
    public void testMoreWithdrawalsThanDepositsWarnsAboutSpending() {
        Customer customer = new Customer("Test");
        BankAccount account = customer.getAccounts().get(0);
        account.deposit(200.0);
        account.deposit(100.0);
        account.withdraw(50.0);
        account.withdraw(60.0);
        account.withdraw(70.0);
        // 2 deposits, 3 withdrawals

        String advice = new BudgetAdvisor(customer).generateAdvice();
        assertTrue(advice.contains("Warning"));
        assertTrue(advice.contains("withdrawals more often than deposits"));
    }

    @Test
    public void testMoreDepositsThanWithdrawalsGivesPositiveFeedback() {
        Customer customer = new Customer("Test");
        BankAccount account = customer.getAccounts().get(0);
        account.deposit(100.0);
        account.deposit(100.0);
        account.deposit(100.0);
        account.withdraw(50.0);
        // 3 deposits, 1 withdrawal

        String advice = new BudgetAdvisor(customer).generateAdvice();
        assertTrue(advice.contains("Good habit"));
    }

    @Test
    public void testLowBalanceSuggestsSavingMore() {
        Customer customer = new Customer("Test");
        BankAccount account = customer.getAccounts().get(0);
        account.deposit(50.0);
        // balance $50 < LOW_BALANCE_THRESHOLD ($100)

        String advice = new BudgetAdvisor(customer).generateAdvice();
        assertTrue(advice.contains("balance is low"));
    }

    @Test
    public void testHealthyBalanceGivesPositiveFeedback() {
        Customer customer = new Customer("Test");
        BankAccount account = customer.getAccounts().get(0);
        account.deposit(600.0);
        // balance $600 >= HEALTHY_BALANCE_THRESHOLD ($500)

        String advice = new BudgetAdvisor(customer).generateAdvice();
        assertTrue(advice.contains("looks healthy"));
    }

    @Test
    public void testTotalWithdrawnExceedsTotalDepositedAddsWarning() {
        // Seed a balance via the constructor so there is no deposit transaction,
        // then withdraw more than we deposit to trigger the net-outflow warning.
        BankAccount account = new BankAccount(200.0, "");
        Customer customer = new Customer("Test", account);
        account.deposit(50.0);
        account.withdraw(80.0);
        // Total deposited: $50, total withdrawn: $80 - net outflow warning expected

        String advice = new BudgetAdvisor(customer).generateAdvice();
        assertTrue(advice.contains("withdrawn more than you have deposited"));
    }

    @Test
    public void testNoTransactionsShowsNoTransactionMessage() {
        Customer customer = new Customer("Test");
        // Default account starts with no transactions

        String advice = new BudgetAdvisor(customer).generateAdvice();
        assertTrue(advice.contains("No transactions found"));
    }

    @Test
    public void testAdviceSummarizesBalanceAndTotals() {
        Customer customer = new Customer("Test");
        BankAccount account = customer.getAccounts().get(0);
        account.deposit(300.0);
        account.withdraw(100.0);

        String advice = new BudgetAdvisor(customer).generateAdvice();
        assertTrue(advice.contains("Total balance across all accounts"));
        assertTrue(advice.contains("Total deposited"));
        assertTrue(advice.contains("Total withdrawn"));
    }

    @Test
    public void testNoAccountsShowsNoAccountsMessage() {
        Customer customer = new Customer("Test");
        customer.closeAccount(customer.getAccounts().get(0));
        // Customer now has no accounts

        String advice = new BudgetAdvisor(customer).generateAdvice();
        assertTrue(advice.contains("no accounts"));
    }
}
