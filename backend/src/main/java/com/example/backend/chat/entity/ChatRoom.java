package com.example.backend.chat.entity;

import com.example.backend.artistBoard.entity.ArtistBoardComment;
import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long ChatRoomId;

    @Column(unique = true)
    private String roomId; // UUID를 문자열로 저장할 필드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_sender_id")
    private JoinIdol messageSender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_receiver_id")
    private JoinIdol messageReceiver;

    @OneToMany(mappedBy = "chatRoom")
    @Builder.Default
    private List<ChatMessage> messageList = new ArrayList<>();

    public void addMessage(ChatMessage chatMessage){
        this.messageList.add(chatMessage);
    }

    public static ChatRoom createChatRoom(String roomId, JoinIdol messageSender, JoinIdol messageReceiver){
        return ChatRoom.builder()
                .roomId(roomId)
                .messageSender(messageSender)
                .messageReceiver(messageReceiver)
                .build();
    }
}
