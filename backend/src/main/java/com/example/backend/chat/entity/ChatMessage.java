package com.example.backend.chat.entity;

import com.example.backend.common.TimeStamped;
import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long ChatMessageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_id")
    private JoinIdol messageSender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Lob
    private String message;

    private Long createDate;

    public static ChatMessage createChatMessage(JoinIdol messageSender, ChatRoom chatRoom, String message, Long date){
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .messageSender(messageSender)
                .message(message)
                .createDate(date)
                .build();

        chatRoom.addMessage(chatMessage);

        return chatMessage;
    }

}
