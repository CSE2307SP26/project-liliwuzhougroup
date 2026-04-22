package main;

import java.io.Serializable;
import java.util.Date;

public final class FeeSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private final double amount;
    private final String description;
    private final Date dueDate;

    public FeeSnapshot(Fee fee) {
        if (fee == null) {
            throw new IllegalArgumentException("Fee cannot be null.");
        }
        this.amount = fee.getAmount();
        this.description = fee.getDescription();
        this.dueDate = fee.getDueDate();
    }

    public Fee toFee() {
        return new Fee(amount, description, dueDate);
    }
}
