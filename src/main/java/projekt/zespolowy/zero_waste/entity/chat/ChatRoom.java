package projekt.zespolowy.zero_waste.entity.chat;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import projekt.zespolowy.zero_waste.entity.User;

import java.util.List;

@Entity
@Table(name = "chat_room")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user1;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user2;
}
