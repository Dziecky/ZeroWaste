package projekt.zespolowy.zero_waste.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {

    Long orderId;

    String productName;

    double quantity;

    double price;

    String imageUrl;

    String ownerName;

    LocalDateTime createdAt;
}
