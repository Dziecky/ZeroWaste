package projekt.zespolowy.zero_waste.services.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.chat.ChatRoom;
import projekt.zespolowy.zero_waste.repository.ChatRoomRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<User> getFriends(User loggedUser) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByUser1OrUser2(loggedUser, loggedUser);

        List<User> friends = new ArrayList<>();

        for (ChatRoom chatRoom : chatRooms) {
            if (chatRoom.getUser1().getId().equals(loggedUser.getId())) {
                friends.add(chatRoom.getUser2());
            } else {
                friends.add(chatRoom.getUser1());
            }
        }

        friends.add(loggedUser);

        friends.sort(Comparator.comparingInt(User::getTotalPoints).reversed());

        return friends;
    }
}
