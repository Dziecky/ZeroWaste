package projekt.zespolowy.zero_waste.services.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.chat.ChatRoom;
import projekt.zespolowy.zero_waste.repository.ChatRoomRepository;

@Service
public class ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public ChatRoom getOrCreateChatRoom(User sender, User receiver) {
        return chatRoomRepository.findByUsers(sender.getId(), receiver.getId()).orElseGet(() -> {
            ChatRoom chatRoom = new ChatRoom();
            chatRoom.setUser1(sender);
            chatRoom.setUser2(receiver);
            return chatRoomRepository.save(chatRoom);
        });
    }
}
