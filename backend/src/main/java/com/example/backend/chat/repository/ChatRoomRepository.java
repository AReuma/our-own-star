package com.example.backend.chat.repository;

import com.example.backend.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findChatRoomByRoomId(String roomId);
    @Query("select ch from ChatRoom ch where ch.roomId =: roomId and ch.messageSender.nickname =: sender or ch.messageReceiver.nickname =: sender")
    Optional<ChatRoom> findByRoomIdAndMessageSender(String roomId, String sender);
}
