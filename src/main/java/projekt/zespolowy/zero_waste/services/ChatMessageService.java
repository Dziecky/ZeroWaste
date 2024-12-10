package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.dto.chat.ChatMessageDTO;
import projekt.zespolowy.zero_waste.dto.chat.ChatRoomDTO;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.chat.ChatMessage;
import projekt.zespolowy.zero_waste.entity.chat.ChatRoom;
import projekt.zespolowy.zero_waste.repository.ChatMessageRepository;
import projekt.zespolowy.zero_waste.repository.ChatRoomRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    public void saveMessage(ChatMessageDTO chatMessageDTO, User sender, User receiver) {

        ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(sender, receiver);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(sender);
        chatMessage.setReceiver(receiver);
        chatMessage.setContent(chatMessageDTO.getContent());
        chatMessage.setTimestamp(LocalDateTime.now());

        chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessageDTO> getMessagesByChatRoom(Long chatRoomId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomId(chatRoomId);

        return messages.stream()
                .map(msg -> new ChatMessageDTO(
                        msg.getSender().getId().toString(),
                        msg.getReceiver().getId().toString(),
                        msg.getContent(),
                        msg.getTimestamp().toString()
                ))
                .collect(Collectors.toList());
    }

    public List<ChatRoomDTO> getAllChatRoomsForUser(User user) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllFetchUsers();

        return chatRooms.stream()
                .filter(room -> room.getUser1().getId().equals(user.getId()) || room.getUser2().getId().equals(user.getId()))
                .map(room -> new ChatRoomDTO(room.getId(), room.getUser1().getId(), room.getUser2().getId(), room.getUser1().getUsername(), room.getUser2().getUsername()))
                .collect(Collectors.toList());
    }
}
