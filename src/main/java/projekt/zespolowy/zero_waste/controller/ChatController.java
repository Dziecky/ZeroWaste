package projekt.zespolowy.zero_waste.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import projekt.zespolowy.zero_waste.entity.ChatMessage;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void processMessage(ChatMessage chatMessage) {
        System.out.println("Received message: " + chatMessage);

        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiver(),
                "/queue/messages",
                chatMessage
        );
    }
}
