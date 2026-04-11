package main;

import java.io.Serializable;
import java.util.Date;

final class FeeSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double amount;
    private final String description;
    private final Date dueDate;

    FeeSnapshot(Fee fee) {
        this.amount = fee.getAmount();
        this.description = fee.getDescription();
        this.dueDate = fee.getDueDate();
    }

    Fee toFee() {
        return new Fee(amount, description, dueDate);
    }
}
