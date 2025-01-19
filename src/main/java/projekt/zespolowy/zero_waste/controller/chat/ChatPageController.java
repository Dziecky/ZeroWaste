package projekt.zespolowy.zero_waste.controller.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.chat.ChatRoom;
import projekt.zespolowy.zero_waste.services.UserService;
import projekt.zespolowy.zero_waste.services.chat.ChatRoomService;

import java.security.Principal;

@Controller
public class ChatPageController {

    private final UserService userService;

    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatPageController(UserService userService, ChatRoomService chatRoomService) {
        this.userService = userService;
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/chat")
    public String chatPage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("username", user.getUsername());
        return "/chat/chat";
    }

    @GetMapping("/chat-room")
    public String chatRoomPage(Model model, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        model.addAttribute("username", user.getUsername());
        return "/chat/chat-room";
    }

    @GetMapping("/chat-room/{productOwnerUsername}")
    public String chatRoomPageWithProductOwner(
            Model model,
            Principal principal,
            @PathVariable String productOwnerUsername
    ) {
        User sender = userService.findByUsername(principal.getName());
        User receiver = userService.findByUsername(productOwnerUsername);
        ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(sender, receiver);

        model.addAttribute("chatRoomId", chatRoom.getId());
        model.addAttribute("user1Id", sender.getId());
        model.addAttribute("user1Name", sender.getUsername());
        model.addAttribute("user2Id", receiver.getId());
        model.addAttribute("user2Name", receiver.getUsername());

        return "/chat/chat-room";
    }

}
