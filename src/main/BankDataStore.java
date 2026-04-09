package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class BankDataStore {
    private static final String DATA_FILE = "bank-data.json";

    private BankDataStore() {
    }

    public static Bank loadBank() {
        return loadBank(new File(DATA_FILE));
    }

    public static Bank loadBank(File file) {
        if (file == null || !file.exists()) {
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

        return parseBankFromJson(jsonBuilder.toString());
    }

    public static void saveBank(Bank bank) {
        saveBank(bank, new File(DATA_FILE));
    }

    public static void saveBank(Bank bank, File file) {
        if (bank == null) {
            throw new IllegalArgumentException("Bank cannot be null.");
        }
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }

        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
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
        String name = extractNullableStringByKey(customerJson, "name");
        if (name == null) {
            return null;
        }

        List<BankAccount> accounts = parseAccounts(extractArrayByKey(customerJson, "accounts"));
        Customer customer = accounts.isEmpty() ? new Customer(name) : new Customer(name, accounts);

        customer.restorePersonalInformation(
                extractNullableStringByKey(customerJson, "address"),
                extractNullableStringByKey(customerJson, "phoneNumber"),
                extractNullableStringByKey(customerJson, "email")
        );
        customer.restorePassword(extractNullableStringByKey(customerJson, "password"));
        customer.restorePin(extractNullableStringByKey(customerJson, "pin"));

        List<RecurringPayment> payments = parseRecurringPayments(extractArrayByKey(customerJson, "recurringPayments"));
        for (int i = 0; i < payments.size(); i++) {
            customer.restoreRecurringPayment(payments.get(i));
        }

        return customer;
    }

    private static List<BankAccount> parseAccounts(String accountsArray) {
        List<BankAccount> accounts = new ArrayList<BankAccount>();
        if (accountsArray == null) {
            return accounts;
        }

        List<String> accountObjects = splitTopLevelObjects(accountsArray);
        for (int i = 0; i < accountObjects.size(); i++) {
            BankAccount account = parseAccount(accountObjects.get(i));
            if (account != null) {
                accounts.add(account);
            }
        }
        return accounts;
    }

    private static BankAccount parseAccount(String accountJson) {
        Double balance = extractDoubleByKey(accountJson, "balance");
        String transactionHistory = extractNullableStringByKey(accountJson, "transactionHistory");
        Boolean frozen = extractBooleanByKey(accountJson, "frozen");
        Double maxWithdrawAmount = extractDoubleByKey(accountJson, "maxWithdrawAmount");

        if (balance == null) {
            return null;
        }
        if (transactionHistory == null) {
            transactionHistory = "";
        }
        if (frozen == null) {
            frozen = false;
        }
        if (maxWithdrawAmount == null || maxWithdrawAmount <= 0) {
            maxWithdrawAmount = Double.MAX_VALUE;
        }

        BankAccount account = new BankAccount(
                balance.doubleValue(),
                transactionHistory,
                frozen.booleanValue(),
                maxWithdrawAmount.doubleValue()
        );

        List<Fee> fees = parseFees(extractArrayByKey(accountJson, "fees"));
        for (int i = 0; i < fees.size(); i++) {
            account.createFee(fees.get(i));
        }

        return account;
    }

    private static List<Fee> parseFees(String feesArray) {
        List<Fee> fees = new ArrayList<Fee>();
        if (feesArray == null) {
            return fees;
        }

        List<String> feeObjects = splitTopLevelObjects(feesArray);
        for (int i = 0; i < feeObjects.size(); i++) {
            Fee fee = parseFee(feeObjects.get(i));
            if (fee != null) {
                fees.add(fee);
            }
        }
        return fees;
    }

    private static Fee parseFee(String feeJson) {
        Double amount = extractDoubleByKey(feeJson, "amount");
        String description = extractNullableStringByKey(feeJson, "description");
        Long dueDateMillis = extractLongByKey(feeJson, "dueDateMillis");

        if (amount == null || description == null || dueDateMillis == null) {
            return null;
        }

        try {
            return new Fee(amount.doubleValue(), description, new Date(dueDateMillis.longValue()));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static List<RecurringPayment> parseRecurringPayments(String recurringPaymentsArray) {
        List<RecurringPayment> payments = new ArrayList<RecurringPayment>();
        if (recurringPaymentsArray == null) {
            return payments;
        }

        List<String> paymentObjects = splitTopLevelObjects(recurringPaymentsArray);
        for (int i = 0; i < paymentObjects.size(); i++) {
            RecurringPayment payment = parseRecurringPayment(paymentObjects.get(i));
            if (payment != null) {
                payments.add(payment);
            }
        }
        return payments;
    }

    private static RecurringPayment parseRecurringPayment(String paymentJson) {
        String description = extractNullableStringByKey(paymentJson, "description");
        Integer sourceAccountIndex = extractIntegerByKey(paymentJson, "sourceAccountIndex");
        Integer targetAccountIndex = extractIntegerByKey(paymentJson, "targetAccountIndex");
        Double amount = extractDoubleByKey(paymentJson, "amount");
        String frequencyText = extractNullableStringByKey(paymentJson, "frequency");
        String nextPaymentDateText = extractNullableStringByKey(paymentJson, "nextPaymentDate");

        if (description == null
                || sourceAccountIndex == null
                || targetAccountIndex == null
                || amount == null
                || frequencyText == null
                || nextPaymentDateText == null) {
            return null;
        }

        try {
            return new RecurringPayment(
                    description,
                    sourceAccountIndex.intValue(),
                    targetAccountIndex.intValue(),
                    amount.doubleValue(),
                    RecurringPayment.Frequency.valueOf(frequencyText),
                    LocalDate.parse(nextPaymentDateText)
            );
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String buildBankJson(Bank bank) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");
        builder.append("  \"customers\": [\n");

        List<Customer> customers = bank.getCustomers();
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            builder.append("    {\n");
            appendNullableStringField(builder, "      ", "name", customer.getName(), true);
            appendNullableStringField(builder, "      ", "address", customer.getAddress(), true);
            appendNullableStringField(builder, "      ", "phoneNumber", customer.getPhoneNumber(), true);
            appendNullableStringField(builder, "      ", "email", customer.getEmail(), true);
            appendNullableStringField(builder, "      ", "password", customer.getStoredPassword(), true);
            appendNullableStringField(builder, "      ", "pin", customer.getStoredPin(), true);
            builder.append("      \"accounts\": [\n");

            List<BankAccount> accounts = customer.getAccounts();
            for (int j = 0; j < accounts.size(); j++) {
                BankAccount account = accounts.get(j);
                builder.append("        {\n");
                builder.append("          \"balance\": ").append(account.getBalance()).append(",\n");
                appendNullableStringField(builder, "          ", "transactionHistory", account.getTransactionHistory(), true);
                builder.append("          \"frozen\": ").append(account.isFrozen()).append(",\n");
                builder.append("          \"maxWithdrawAmount\": ").append(account.getMaxWithdrawAmount()).append(",\n");
                builder.append("          \"fees\": [\n");

                List<Fee> fees = account.getRemainingFees();
                for (int k = 0; k < fees.size(); k++) {
                    Fee fee = fees.get(k);
                    builder.append("            {\n");
                    builder.append("              \"amount\": ").append(fee.getAmount()).append(",\n");
                    appendNullableStringField(builder, "              ", "description", fee.getDescription(), true);
                    builder.append("              \"dueDateMillis\": ").append(fee.getDueDate().getTime()).append("\n");
                    builder.append("            }");
                    if (k < fees.size() - 1) {
                        builder.append(",");
                    }
                    builder.append("\n");
                }

                builder.append("          ]\n");
                builder.append("        }");
                if (j < accounts.size() - 1) {
                    builder.append(",");
                }
                builder.append("\n");
            }

            builder.append("      ],\n");
            builder.append("      \"recurringPayments\": [\n");

            List<RecurringPayment> payments = customer.getRecurringPayments();
            for (int j = 0; j < payments.size(); j++) {
                RecurringPayment payment = payments.get(j);
                builder.append("        {\n");
                appendNullableStringField(builder, "          ", "description", payment.getDescription(), true);
                builder.append("          \"sourceAccountIndex\": ").append(payment.getSourceAccountIndex()).append(",\n");
                builder.append("          \"targetAccountIndex\": ").append(payment.getTargetAccountIndex()).append(",\n");
                builder.append("          \"amount\": ").append(payment.getAmount()).append(",\n");
                appendNullableStringField(builder, "          ", "frequency", payment.getFrequency().name(), true);
                appendNullableStringField(builder, "          ", "nextPaymentDate", payment.getNextPaymentDate().toString(), false);
                builder.append("        }");
                if (j < payments.size() - 1) {
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

    private static void appendNullableStringField(
            StringBuilder builder,
            String indent,
            String key,
            String value,
            boolean addComma
    ) {
        builder.append(indent).append("\"").append(key).append("\": ");
        if (value == null) {
            builder.append("null");
        } else {
            builder.append("\"").append(escapeJson(value)).append("\"");
        }
        if (addComma) {
            builder.append(",");
        }
        builder.append("\n");
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

    private static String extractNullableStringByKey(String json, String key) {
        String literal = extractLiteralByKey(json, key);
        if ("null".equals(literal)) {
            return null;
        }
        return extractStringByKey(json, key);
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

    private static String extractLiteralByKey(String json, String key) {
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

        if (start >= json.length() || json.charAt(start) == '"' || json.charAt(start) == '[' || json.charAt(start) == '{') {
            return null;
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

        return json.substring(start, end);
    }

    private static Double extractDoubleByKey(String json, String key) {
        String valueText = extractLiteralByKey(json, key);
        if (valueText == null) {
            return null;
        }

        try {
            return Double.valueOf(valueText);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Integer extractIntegerByKey(String json, String key) {
        String valueText = extractLiteralByKey(json, key);
        if (valueText == null) {
            return null;
        }

        try {
            return Integer.valueOf(valueText);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long extractLongByKey(String json, String key) {
        String valueText = extractLiteralByKey(json, key);
        if (valueText == null) {
            return null;
        }

        try {
            return Long.valueOf(valueText);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Boolean extractBooleanByKey(String json, String key) {
        String valueText = extractLiteralByKey(json, key);
        if ("true".equals(valueText)) {
            return Boolean.TRUE;
        }
        if ("false".equals(valueText)) {
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