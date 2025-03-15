package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.Data;
import projekt.zespolowy.zero_waste.entity.enums.RefundStatus;

import java.time.LocalDateTime;

@Entity
@Data
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    private String refundReason;
    private Double refundAmount;
    private LocalDateTime requestDate;
    private RefundStatus status;
}
