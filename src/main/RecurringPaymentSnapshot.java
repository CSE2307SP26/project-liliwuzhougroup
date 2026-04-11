package main;

import java.io.Serializable;
import java.time.LocalDate;

final class RecurringPaymentSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String description;
    private final int sourceAccountIndex;
    private final int targetAccountIndex;
    private final double amount;
    private final RecurringPayment.Frequency frequency;
    private final LocalDate nextPaymentDate;

    RecurringPaymentSnapshot(RecurringPayment payment) {
        this.description = payment.getDescription();
        this.sourceAccountIndex = payment.getSourceAccountIndex();
        this.targetAccountIndex = payment.getTargetAccountIndex();
        this.amount = payment.getAmount();
        this.frequency = payment.getFrequency();
        this.nextPaymentDate = payment.getNextPaymentDate();
    }

    RecurringPayment toRecurringPayment() {
        return new RecurringPayment(
                description,
                sourceAccountIndex,
                targetAccountIndex,
                amount,
                frequency,
                nextPaymentDate
        );
    }
}
