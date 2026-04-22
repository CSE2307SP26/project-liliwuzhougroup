package main;

public class AIHelpAssistant {

    public String getResponse(String question) {
        String q = question.toLowerCase();

        if (contains(q, "transfer", "send money", "move money")) {
            return "To transfer money: open an account and select 'Transfer money from this account'. You need at least two accounts.";
        }
        if (contains(q, "recurring", "automatic payment", "scheduled payment")) {
            return "To manage recurring payments: go to your Account Dashboard and select 'Manage recurring payments'.";
        }
        if (contains(q, "fee", "charge")) {
            return "To pay a pending fee: open an account and select 'Pay a pending fee'. Pending fees are shown at the top of the account menu.";
        }
        if (contains(q, "password", "pin", "security")) {
            return "To change your password or PIN: go to your Account Dashboard and select 'Set password or PIN'.";
        }
        if (contains(q, "address", "phone", "email", "personal", "information", "contact")) {
            return "To update your personal information: go to your Account Dashboard and select 'Update personal information'.";
        }
        if (contains(q, "maximum", "withdrawal limit", "limit", "withdraw limit")) {
            return "To set a maximum withdrawal amount: open an account and select 'Set maximum withdrawal amount'.";
        }
        if (contains(q, "transaction", "history", "statement")) {
            return "To view transaction history: open an account and select 'Check transaction history'. For a monthly breakdown, select 'View monthly statement'.";
        }
        if (contains(q, "balance", "how much")) {
            return "To check your balance: open an account and select 'Check account balance'.";
        }

        return "I'm not sure how to help with that. I can assist with: "
                + "transferring money, recurring payments, fees, password or PIN, "
                + "personal information, withdrawal limits, transaction history, and account balance.";
    }

    private boolean contains(String question, String... keywords) {
        for (String keyword : keywords) {
            if (question.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
