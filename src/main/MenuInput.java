package main;

import java.util.Scanner;

final class MenuInput {
    private final Scanner keyboardInput;

    MenuInput(Scanner keyboardInput) {
        this.keyboardInput = keyboardInput;
    }

    int readSelection(int max) {
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

    double readPositiveAmount(String prompt) {
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

    void prepareForTextInput() {
        keyboardInput.skip("\\R?");
    }

    String readRequiredText(String prompt) {
        String value = "";
        while (value.trim().isEmpty()) {
            System.out.print(prompt);
            value = keyboardInput.nextLine();
        }
        return value.trim();
    }

    String readPin(String prompt) {
        String pin = "";
        while (!pin.matches("\\d{4}")) {
            System.out.print(prompt);
            pin = keyboardInput.nextLine().trim();
        }
        return pin;
    }
}
