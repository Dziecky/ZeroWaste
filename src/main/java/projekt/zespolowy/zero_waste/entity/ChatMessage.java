package projekt.zespolowy.zero_waste.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatMessage {
    private String sender;    // Sender's username or ID
    private String receiver;  // Receiver's username or ID
    private String content;   // Message content
    private String timestamp; // Optional: timestamp of the message

}
