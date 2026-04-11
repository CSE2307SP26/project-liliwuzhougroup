package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class BankDataStore {
    private static final String DATA_FILE = "bank-data.dat";

    private BankDataStore() {
    }

    public static Bank loadBank() {
        return loadBank(new File(DATA_FILE));
    }

    public static Bank loadBank(File file) {
        if (file == null || !file.exists()) {
            return new Bank();
        }
        return readBank(file);
    }

    public static void saveBank(Bank bank) {
        saveBank(bank, new File(DATA_FILE));
    }

    public static void saveBank(Bank bank, File file) {
        validateSaveRequest(bank, file);
        prepareFile(file);
        writeBank(bank, file);
    }

    private static Bank readBank(File file) {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {
            return readSnapshot(input).toBank();
        } catch (IOException e) {
            return new Bank();
        } catch (ClassNotFoundException e) {
            return new Bank();
        }
    }

    private static BankSnapshot readSnapshot(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        Object snapshot = input.readObject();
        if (snapshot instanceof BankSnapshot) {
            return (BankSnapshot) snapshot;
        }
        throw new IOException("Unexpected bank data format.");
    }

    private static void writeBank(Bank bank, File file) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))) {
            output.writeObject(new BankSnapshot(bank));
        } catch (IOException e) {
            throw new RuntimeException("Unable to save bank data.", e);
        }
    }

    private static void validateSaveRequest(Bank bank, File file) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null.");
        }
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }
    }

    private static void prepareFile(File file) {
        File parent = file.getParentFile();
        if (parent == null || parent.exists()) {
            return;
        }
        if (!parent.mkdirs()) {
            throw new RuntimeException("Unable to create bank data directory.");
        }
    }

    private static final class BankSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private final List<CustomerSnapshot> customers;

        private BankSnapshot(Bank bank) {
            this.customers = toCustomerSnapshots(bank.getCustomers());
        }

        private Bank toBank() {
            Bank bank = new Bank();
            for (CustomerSnapshot customer : customers) {
                bank.addCustomer(customer.toCustomer());
            }
            return bank;
        }
    }

    private static final class CustomerSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private final String address;
        private final String phoneNumber;
        private final String email;
        private final String password;
        private final String pin;
        private final List<AccountSnapshot> accounts;
        private final List<RecurringPaymentSnapshot> recurringPayments;

        private CustomerSnapshot(Customer customer) {
            this.name = customer.getName();
            this.address = customer.getAddress();
            this.phoneNumber = customer.getPhoneNumber();
            this.email = customer.getEmail();
            this.password = customer.getStoredPassword();
            this.pin = customer.getStoredPin();
            this.accounts = toAccountSnapshots(customer.getAccounts());
            this.recurringPayments = toPaymentSnapshots(customer.getRecurringPayments());
        }

        private Customer toCustomer() {
            Customer customer = new Customer(name, toAccounts(accounts));
            customer.restorePersonalInformation(address, phoneNumber, email);
            customer.restorePassword(password);
            customer.restorePin(pin);
            restoreRecurringPayments(customer);
            return customer;
        }

        private void restoreRecurringPayments(Customer customer) {
            for (RecurringPaymentSnapshot payment : recurringPayments) {
                customer.restoreRecurringPayment(payment.toRecurringPayment());
            }
        }
    }

    private static final class AccountSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private final double balance;
        private final String transactionHistory;
        private final boolean frozen;
        private final double maxWithdrawAmount;
        private final List<FeeSnapshot> fees;

        private AccountSnapshot(BankAccount account) {
            this.balance = account.getBalance();
            this.transactionHistory = account.getTransactionHistory();
            this.frozen = account.isFrozen();
            this.maxWithdrawAmount = account.getMaxWithdrawAmount();
            this.fees = toFeeSnapshots(account.getRemainingFees());
        }

        private BankAccount toAccount() {
            BankAccount account = new BankAccount(
                    balance,
                    transactionHistory,
                    frozen,
                    maxWithdrawAmount
            );
            for (FeeSnapshot fee : fees) {
                account.createFee(fee.toFee());
            }
            return account;
        }
    }

    private static final class FeeSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private final double amount;
        private final String description;
        private final Date dueDate;

        private FeeSnapshot(Fee fee) {
            this.amount = fee.getAmount();
            this.description = fee.getDescription();
            this.dueDate = fee.getDueDate();
        }

        private Fee toFee() {
            return new Fee(amount, description, dueDate);
        }
    }

    private static final class RecurringPaymentSnapshot implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String description;
        private final int sourceAccountIndex;
        private final int targetAccountIndex;
        private final double amount;
        private final RecurringPayment.Frequency frequency;
        private final java.time.LocalDate nextPaymentDate;

        private RecurringPaymentSnapshot(RecurringPayment payment) {
            this.description = payment.getDescription();
            this.sourceAccountIndex = payment.getSourceAccountIndex();
            this.targetAccountIndex = payment.getTargetAccountIndex();
            this.amount = payment.getAmount();
            this.frequency = payment.getFrequency();
            this.nextPaymentDate = payment.getNextPaymentDate();
        }

        private RecurringPayment toRecurringPayment() {
            return new RecurringPayment(
                    description,
                    sourceAccountIndex,
                    targetAccountIndex,
                    amount,
                    frequency,
                    nextPaymentDate
            );
        }
    }

    private static List<CustomerSnapshot> toCustomerSnapshots(List<Customer> customers) {
        List<CustomerSnapshot> snapshots = new ArrayList<CustomerSnapshot>();
        for (Customer customer : customers) {
            snapshots.add(new CustomerSnapshot(customer));
        }
        return snapshots;
    }

    private static List<AccountSnapshot> toAccountSnapshots(List<BankAccount> accounts) {
        List<AccountSnapshot> snapshots = new ArrayList<AccountSnapshot>();
        for (BankAccount account : accounts) {
            snapshots.add(new AccountSnapshot(account));
        }
        return snapshots;
    }

    private static List<FeeSnapshot> toFeeSnapshots(List<Fee> fees) {
        List<FeeSnapshot> snapshots = new ArrayList<FeeSnapshot>();
        for (Fee fee : fees) {
            snapshots.add(new FeeSnapshot(fee));
        }
        return snapshots;
    }

    private static List<RecurringPaymentSnapshot> toPaymentSnapshots(
            List<RecurringPayment> payments) {
        List<RecurringPaymentSnapshot> snapshots =
                new ArrayList<RecurringPaymentSnapshot>();
        for (RecurringPayment payment : payments) {
            snapshots.add(new RecurringPaymentSnapshot(payment));
        }
        return snapshots;
    }

    private static List<BankAccount> toAccounts(List<AccountSnapshot> snapshots) {
        List<BankAccount> accounts = new ArrayList<BankAccount>();
        for (AccountSnapshot snapshot : snapshots) {
            accounts.add(snapshot.toAccount());
        }
        return accounts;
    }
}