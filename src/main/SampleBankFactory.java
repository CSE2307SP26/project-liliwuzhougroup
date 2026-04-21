package main;

public final class SampleBankFactory {
    public static final String SAMPLE_CUSTOMER_NAME = "Sample User";
    public static final String SAMPLE_CUSTOMER_ADDRESS = "123 College Ave";
    public static final String SAMPLE_CUSTOMER_PHONE = "5551234567";
    public static final String SAMPLE_CUSTOMER_EMAIL = "sample.user@237bank.com";
    public static final String SAMPLE_CUSTOMER_PASSWORD = "user123";
    public static final String SAMPLE_CUSTOMER_PIN = "1234";
    public static final double PRIMARY_ACCOUNT_BALANCE = 750.0;
    public static final double SECONDARY_ACCOUNT_BALANCE = 250.0;

    private SampleBankFactory() {
    }

    public static Bank createSampleBank() {
        Bank bank = new Bank();
        ensureSampleCustomer(bank);
        return bank;
    }

    public static Bank ensureSampleCustomer(Bank bank) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null.");
        }
        if (bank.findCustomerByEmail(SAMPLE_CUSTOMER_EMAIL) == null) {
            bank.addCustomer(createSampleCustomer());
        }
        return bank;
    }

    private static Customer createSampleCustomer() {
        Customer customer = new Customer(SAMPLE_CUSTOMER_NAME);
        customer.updatePersonalInformation(
                SAMPLE_CUSTOMER_ADDRESS,
                SAMPLE_CUSTOMER_PHONE,
                SAMPLE_CUSTOMER_EMAIL
        );
        customer.setPassword(SAMPLE_CUSTOMER_PASSWORD);
        customer.setPin(SAMPLE_CUSTOMER_PIN);
        customer.getAccounts().get(0).deposit(PRIMARY_ACCOUNT_BALANCE);
        customer.openAccount().deposit(SECONDARY_ACCOUNT_BALANCE);
        return customer;
    }
}
