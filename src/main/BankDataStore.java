package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class BankDataStore {
    private static final String DATA_FILE = "bank-data.dat";

    private BankDataStore() {
    }

    public static Bank loadBank() {
        return loadBank(new File(DATA_FILE), true);
    }

    public static Bank loadBank(File file) {
        return loadBank(file, false);
    }

    public static Bank loadBank(File file, boolean ensureSampleCustomer) {
        if (file == null || !file.exists()) {
            Bank bank = new Bank();
            return ensureSampleCustomer ? SampleBankFactory.ensureSampleCustomer(bank) : bank;
        }
        Bank bank = readBank(file);
        return ensureSampleCustomer ? SampleBankFactory.ensureSampleCustomer(bank) : bank;
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
}
