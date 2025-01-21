package projekt.zespolowy.zero_waste.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentRequest {
    private String productName;
    private double amount;
}

