package com.example.backend.chat.repository;

import com.example.backend.chat.entity.ChatMessage;
import com.example.backend.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}
