package main;

import java.util.Scanner;

final class CustomerAccessPortal {
    private static final int LOGIN_SELECTION = 1;
    private static final int REGISTER_SELECTION = 2;
    private static final int BACK_SELECTION = 3;
    private static final int PASSWORD_SELECTION = 1;
    private static final int MAX_SELECTION = 3;

    private final Bank bank;
    private final Scanner keyboardInput;
    private final MenuInput io;
    private final CustomerAuthenticator authenticator;
    private final CustomerRegistrationService registrationService;

    CustomerAccessPortal(Bank bank, Scanner keyboardInput) {
        this.bank = bank;
        this.keyboardInput = keyboardInput;
        this.io = new MenuInput(keyboardInput);
        this.authenticator = new CustomerAuthenticator(bank);
        this.registrationService = new CustomerRegistrationService(bank);
    }

    void run() {
        int selection = -1;
        while (selection != BACK_SELECTION) {
            displayOptions();
            selection = io.readSelection(MAX_SELECTION);
            processSelection(selection);
        }
    }

    private void displayOptions() {
        System.out.println("Customer Access:");
        System.out.println("1. Log in");
        System.out.println("2. Create a new account");
        System.out.println("3. Back to main menu");
    }

    private void processSelection(int selection) {
        try {
            if (selection == LOGIN_SELECTION) {
                loginCustomer();
                return;
            }
            if (selection == REGISTER_SELECTION) {
                registerCustomer(null);
                return;
            }
            System.out.println("Returning to main menu.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loginCustomer() {
        io.prepareForTextInput();
        String email = io.readRequiredText("Enter your email: ");
        Customer customer = authenticator.findCustomer(email);
        if (customer == null) {
            offerRegistration(email);
            return;
        }
        if (authenticateCustomer(customer, promptLoginMethod())) {
            openCustomerMenu(customer);
            return;
        }
        System.out.println("Invalid login credentials.");
    }

    private int promptLoginMethod() {
        System.out.println("1. Log in with password");
        System.out.println("2. Log in with PIN");
        return io.readSelection(2);
    }

    private boolean authenticateCustomer(Customer customer, int method) {
        io.prepareForTextInput();
        if (method == PASSWORD_SELECTION) {
            return authenticator.isPasswordValid(
                    customer,
                    io.readRequiredText("Enter your password: ")
            );
        }
        return authenticator.isPinValid(customer, io.readPin("Enter your 4-digit PIN: "));
    }

    private void offerRegistration(String email) {
        System.out.println("No customer was found with that email.");
        System.out.println("1. Create a new account");
        System.out.println("2. Back");
        if (io.readSelection(2) == 1) {
            registerCustomer(email);
        }
    }

    private void registerCustomer(String email) {
        Customer customer = registrationService.register(buildRegistrationRequest(email));
        System.out.println("Account created successfully.");
        openCustomerMenu(customer);
    }

    private CustomerRegistrationRequest buildRegistrationRequest(String email) {
        io.prepareForTextInput();
        String name = io.readRequiredText("Enter your full name: ");
        String address = io.readRequiredText("Enter your address: ");
        String phoneNumber = io.readRequiredText("Enter your phone number: ");
        String selectedEmail = email == null ? io.readRequiredText("Enter your email: ") : email.trim();
        String password = io.readRequiredText("Create a password: ");
        return new CustomerRegistrationRequest(
                name,
                address,
                phoneNumber,
                selectedEmail,
                password,
                readOptionalPin()
        );
    }

    private String readOptionalPin() {
        System.out.println("Would you like to set a 4-digit PIN?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        if (io.readSelection(2) != 1) {
            return null;
        }
        io.prepareForTextInput();
        return io.readPin("Enter your 4-digit PIN: ");
    }

    private void openCustomerMenu(Customer customer) {
        new CustomerMenu(bank, keyboardInput, customer).run();
    }
}
