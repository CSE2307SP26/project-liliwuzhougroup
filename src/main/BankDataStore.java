package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class BankDataStore {
    private static final String DATA_FILE = "bank-data.json";

    private BankDataStore() {
    }

    public static Bank loadBank() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new Bank();
        }

        BufferedReader reader = null;
        StringBuilder jsonBuilder = new StringBuilder();

        try {
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                jsonBuilder.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            return new Bank();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        String json = jsonBuilder.toString();
        return parseBankFromJson(json);
    }

    public static void saveBank(Bank bank) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null.");
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(DATA_FILE);
            writer.write(buildBankJson(bank));
        } catch (IOException e) {
            throw new RuntimeException("Unable to save bank data.", e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                
                }
            }
        }
    }

    private static Bank parseBankFromJson(String json) {
        Bank bank = new Bank();
        if (json == null) {
            return bank;
        }

        String customersArray = extractArrayByKey(json, "customers");
        if (customersArray == null) {
            return bank;
        }

        List<String> customerObjects = splitTopLevelObjects(customersArray);
        for (int i = 0; i < customerObjects.size(); i++) {
            Customer customer = parseCustomer(customerObjects.get(i));
            if (customer != null) {
                bank.addCustomer(customer);
            }
        }

        return bank;
    }

    private static Customer parseCustomer(String customerJson) {
        String name = extractStringByKey(customerJson, "name");
        if (name == null) {
            return null;
        }

        String accountsArray = extractArrayByKey(customerJson, "accounts");
        if (accountsArray == null) {
            return new Customer(name);
        }

        List<String> accountObjects = splitTopLevelObjects(accountsArray);
        List<BankAccount> accounts = new ArrayList<BankAccount>();
        for (int i = 0; i < accountObjects.size(); i++) {
            BankAccount account = parseAccount(accountObjects.get(i));
            if (account != null) {
                accounts.add(account);
            }
        }

        if (accounts.isEmpty()) {
            return new Customer(name);
        }

        return new Customer(name, accounts);
    }

    private static BankAccount parseAccount(String accountJson) {
        Double balance = extractDoubleByKey(accountJson, "balance");
        String transactionHistory = extractStringByKey(accountJson, "transactionHistory");

        // read frozen 
        Boolean frozen = extractBooleanByKey(accountJson, "frozen");

        if (balance == null) {
            return null;
        }
        if (transactionHistory == null) {
            transactionHistory = "";
        }

       
        if (frozen == null) {
            frozen = false;
        }

    
        return new BankAccount(balance.doubleValue(), transactionHistory, frozen.booleanValue());
    }

    private static String buildBankJson(Bank bank) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"customers\": [\n");

        List<Customer> customers = bank.getCustomers();
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            builder.append("    {\n");
            builder.append("      \"name\": \"").append(escapeJson(customer.getName())).append("\",\n");
            builder.append("      \"accounts\": [\n");

            List<BankAccount> accounts = customer.getAccounts();
            for (int j = 0; j < accounts.size(); j++) {
                BankAccount account = accounts.get(j);
                builder.append("        {\n");
                builder.append("          \"balance\": ").append(account.getBalance()).append(",\n");
                builder.append("          \"transactionHistory\": \"")
                        .append(escapeJson(account.getTransactionHistory()))
                        .append("\",\n");

                // frozen status
                builder.append("          \"frozen\": ").append(account.isFrozen()).append("\n");

                builder.append("        }");
                if (j < accounts.size() - 1) {
                    builder.append(",");
                }
                builder.append("\n");
            }

            builder.append("      ]\n");
            builder.append("    }");
            if (i < customers.size() - 1) {
                builder.append(",");
            }
            builder.append("\n");
        }

        builder.append("  ]\n");
        builder.append("}\n");
        return builder.toString();
    }

    private static String extractArrayByKey(String json, String key) {
        String keyText = "\"" + key + "\"";
        int keyIndex = json.indexOf(keyText);
        if (keyIndex < 0) {
            return null;
        }

        int startIndex = json.indexOf("[", keyIndex);
        if (startIndex < 0) {
            return null;
        }

        int endIndex = findMatchingBracket(json, startIndex);
        if (endIndex < 0) {
            return null;
        }

        return json.substring(startIndex + 1, endIndex);
    }

    private static int findMatchingBracket(String text, int openIndex) {
        int depth = 0;
        boolean insideString = false;

        for (int i = openIndex; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == '"' && (i == 0 || text.charAt(i - 1) != '\\')) {
                insideString = !insideString;
            }
            if (insideString) {
                continue;
            }

            if (current == '[') {
                depth++;
            } else if (current == ']') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    private static List<String> splitTopLevelObjects(String arrayText) {
        List<String> objects = new ArrayList<String>();
        int depth = 0;
        int objectStart = -1;
        boolean insideString = false;

        for (int i = 0; i < arrayText.length(); i++) {
            char current = arrayText.charAt(i);
            if (current == '"' && (i == 0 || arrayText.charAt(i - 1) != '\\')) {
                insideString = !insideString;
            }
            if (insideString) {
                continue;
            }

            if (current == '{') {
                if (depth == 0) {
                    objectStart = i;
                }
                depth++;
            } else if (current == '}') {
                depth--;
                if (depth == 0 && objectStart >= 0) {
                    objects.add(arrayText.substring(objectStart, i + 1));
                    objectStart = -1;
                }
            }
        }

        return objects;
    }

    private static String extractStringByKey(String json, String key) {
        String keyText = "\"" + key + "\"";
        int keyIndex = json.indexOf(keyText);
        if (keyIndex < 0) {
            return null;
        }

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex < 0) {
            return null;
        }

        int firstQuote = json.indexOf("\"", colonIndex + 1);
        if (firstQuote < 0) {
            return null;
        }

        int secondQuote = firstQuote + 1;
        while (secondQuote < json.length()) {
            char current = json.charAt(secondQuote);
            if (current == '"' && json.charAt(secondQuote - 1) != '\\') {
                break;
            }
            secondQuote++;
        }

        if (secondQuote >= json.length()) {
            return null;
        }

        String escaped = json.substring(firstQuote + 1, secondQuote);
        return unescapeJson(escaped);
    }

    private static Double extractDoubleByKey(String json, String key) {
        String keyText = "\"" + key + "\"";
        int keyIndex = json.indexOf(keyText);
        if (keyIndex < 0) {
            return null;
        }

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex < 0) {
            return null;
        }

        int start = colonIndex + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        int end = start;
        while (end < json.length()) {
            char current = json.charAt(end);
            if (current == ',' || current == '}' || Character.isWhitespace(current)) {
                break;
            }
            end++;
        }

        if (start >= end) {
            return null;
        }

        try {
            return Double.valueOf(json.substring(start, end));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // NEW: helper method to read true/false values like "frozen": true
    private static Boolean extractBooleanByKey(String json, String key) {
        String keyText = "\"" + key + "\"";
        int keyIndex = json.indexOf(keyText);
        if (keyIndex < 0) {
            return null;
        }

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex < 0) {
            return null;
        }

        int start = colonIndex + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }

        if (json.startsWith("true", start)) {
            return Boolean.TRUE;
        }
        if (json.startsWith("false", start)) {
            return Boolean.FALSE;
        }

        return null;
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value;
        escaped = escaped.replace("\\", "\\\\");
        escaped = escaped.replace("\"", "\\\"");
        escaped = escaped.replace("\n", "\\n");
        escaped = escaped.replace("\r", "\\r");
        escaped = escaped.replace("\t", "\\t");
        return escaped;
    }

    private static String unescapeJson(String value) {
        String unescaped = value;
        unescaped = unescaped.replace("\\n", "\n");
        unescaped = unescaped.replace("\\r", "\r");
        unescaped = unescaped.replace("\\t", "\t");
        unescaped = unescaped.replace("\\\"", "\"");
        unescaped = unescaped.replace("\\\\", "\\");
        return unescaped;
    }
}