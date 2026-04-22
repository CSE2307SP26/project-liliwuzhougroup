package test;

import main.AIHelpAssistant;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AIHelpAssistantTest {

    private AIHelpAssistant assistant;

    @Before
    public void setUp() {
        assistant = new AIHelpAssistant();
    }

    @Test
    public void testTransferQuestionReturnsTransferHelp() {
        String response = assistant.getResponse("How do I transfer money?");
        assertTrue(response.contains("Transfer money from this account"));
    }

    @Test
    public void testRecurringPaymentQuestionReturnsRecurringHelp() {
        String response = assistant.getResponse("How do I manage recurring payments?");
        assertTrue(response.contains("Manage recurring payments"));
    }

    @Test
    public void testFeeQuestionReturnsFeeHelp() {
        String response = assistant.getResponse("How do I pay a fee?");
        assertTrue(response.contains("Pay a pending fee"));
    }

    @Test
    public void testPasswordQuestionReturnsPasswordHelp() {
        String response = assistant.getResponse("How do I change my password?");
        assertTrue(response.contains("Set password or PIN"));
    }

    @Test
    public void testPinQuestionReturnsPasswordHelp() {
        String response = assistant.getResponse("I forgot my PIN");
        assertTrue(response.contains("Set password or PIN"));
    }

    @Test
    public void testPersonalInformationQuestionReturnsPersonalInfoHelp() {
        String response = assistant.getResponse("How do I update my personal information?");
        assertTrue(response.contains("Update personal information"));
    }

    @Test
    public void testWithdrawalLimitQuestionReturnsWithdrawalHelp() {
        String response = assistant.getResponse("How do I set a withdrawal limit?");
        assertTrue(response.contains("Set maximum withdrawal amount"));
    }

    @Test
    public void testTransactionHistoryQuestionReturnsHistoryHelp() {
        String response = assistant.getResponse("How do I check my transaction history?");
        assertTrue(response.contains("Check transaction history"));
    }

    @Test
    public void testBalanceQuestionReturnsBalanceHelp() {
        String response = assistant.getResponse("How do I check my balance?");
        assertTrue(response.contains("Check account balance"));
    }

    @Test
    public void testUnrecognizedQuestionReturnsFallbackMessage() {
        String response = assistant.getResponse("What is the meaning of life?");
        assertTrue(response.contains("I'm not sure how to help with that"));
    }

    @Test
    public void testFallbackMessageListsSupportedTopics() {
        String response = assistant.getResponse("hello");
        assertTrue(response.contains("transferring money"));
        assertTrue(response.contains("fees"));
        assertTrue(response.contains("account balance"));
    }

    @Test
    public void testMixedCapitalizationIsHandled() {
        String response = assistant.getResponse("TRANSFER MONEY");
        assertTrue(response.contains("Transfer money from this account"));
    }

    @Test
    public void testPendingTransactionsRoutesToTransactionHistoryNotFee() {
        String response = assistant.getResponse("How do I see my pending transactions?");
        assertTrue(response.contains("Check transaction history"));
        assertFalse(response.contains("Pay a pending fee"));
    }

    @Test
    public void testNullQuestionIsRejected() {
        try {
            assistant.getResponse(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Question cannot be null.", e.getMessage());
        }
    }
}
