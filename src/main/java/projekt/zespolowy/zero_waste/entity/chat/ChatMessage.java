package projekt.zespolowy.zero_waste.entity.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import projekt.zespolowy.zero_waste.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom; // The chat room where this message belongs

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // Sender of the message

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // Receiver of the message

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
