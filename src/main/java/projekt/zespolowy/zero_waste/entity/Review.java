package projekt.zespolowy.zero_waste.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "rating", nullable = false)
    private int rating; // Assuming a scale from 1 to

    // Do zrobienia kiedy będzie strona Usera
//    @Column(name = "about_user_id", nullable = false)
//    private Long aboutUserId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
