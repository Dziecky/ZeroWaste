package projekt.zespolowy.zero_waste.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {
    private Long id;       // Chat Room ID
    private Long user1Id;  // First user's ID
    private Long user2Id;  // Second user's ID
}
