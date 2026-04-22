package main;

import java.util.Scanner;

public final class MenuInput {
    private static final String CLEAR_COMPLETED_INPUT_LINE = "\u001B[1A\u001B[2K\r";

    private final Scanner keyboardInput;

    public MenuInput(Scanner keyboardInput) {
        this.keyboardInput = keyboardInput;
    }

    public int readSelection(int max) {
        if (max < 1) {
            throw new IllegalArgumentException("At least one selection must be available.");
        }
        int selection = -1;
        while (selection < 1 || selection > max) {
            System.out.print("Please make a selection: ");
            if (keyboardInput.hasNextInt()) {
                selection = keyboardInput.nextInt();
            } else {
                keyboardInput.next();
            }
        }
        clearCompletedInputLine();
        return selection;
    }

    public double readPositiveAmount(String prompt) {
        double amount = -1;
        while (amount <= 0) {
            System.out.print(prompt);
            if (keyboardInput.hasNextDouble()) {
                amount = keyboardInput.nextDouble();
            } else {
                keyboardInput.next();
            }
        }
        clearCompletedInputLine();
        return amount;
    }

    public double readNonNegativeAmount(String prompt) {
        double amount = -1;
        while (amount < 0) {
            System.out.print(prompt);
            if (keyboardInput.hasNextDouble()) {
                amount = keyboardInput.nextDouble();
            } else {
                keyboardInput.next();
            }
        }
        clearCompletedInputLine();
        return amount;
    }

    public void prepareForTextInput() {
        keyboardInput.skip("\\R?");
    }

    public String readRequiredText(String prompt) {
        String value = "";
        while (value.trim().isEmpty()) {
            System.out.print(prompt);
            value = keyboardInput.nextLine();
        }
        clearCompletedInputLine();
        return value.trim();
    }

    public String readPin(String prompt) {
        String pin = "";
        while (!pin.matches("\\d{4}")) {
            System.out.print(prompt);
            pin = keyboardInput.nextLine().trim();
            if (!pin.matches("\\d{4}")) {
                System.out.println("PIN must be exactly 4 digits.");
            }
        }
        clearCompletedInputLine();
        return pin;
    }

    public String readPhoneNumber(String prompt) {
        while (true) {
            System.out.print(prompt);
            String phoneNumber = keyboardInput.nextLine();
            try {
                clearCompletedInputLine();
                return PhoneNumberFormatter.normalizeRequired(phoneNumber);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void clearCompletedInputLine() {
        System.out.print(CLEAR_COMPLETED_INPUT_LINE);
        System.out.flush();
    }
}
