package main;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class BankDataStore {
    private static final String DATA_FILE = "bank-data.ser";

    private BankDataStore() {
    }

    public static Bank loadBank() {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            Object loadedObject = input.readObject();
            if (loadedObject instanceof Bank) {
                return (Bank) loadedObject;
            }
        } catch (IOException | ClassNotFoundException ignored) {
            // Fall back to empty bank when file doesn't exist or data is invalid.
        }
        return new Bank();
    }

    public static void saveBank(Bank bank) {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            output.writeObject(bank);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save bank data.", e);
        }
    }
}
