package projekt.zespolowy.zero_waste.controller.exception;

import lombok.Getter;

@Getter
public class RefundNotFoundException extends RuntimeException {

    private final Long refundId;

    public RefundNotFoundException(Long refundId) {
        super("Refund not found with ID: " + refundId);
        this.refundId = refundId;
    }
}
