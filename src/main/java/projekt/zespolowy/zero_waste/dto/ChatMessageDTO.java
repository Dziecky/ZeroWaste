package projekt.zespolowy.zero_waste.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private String sender; // UUID of sender
    private String receiver; // UUID of receiver
    private String content;
    private String timestamp; // Optional for frontend display
}
