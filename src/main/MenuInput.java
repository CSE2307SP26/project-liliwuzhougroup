package main;

import java.util.Scanner;

public final class MenuInput {
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
        return pin;
    }
}
