package com.example.backend.chat.service;

import com.example.backend.chat.dto.ChatMessageDTO;
import com.example.backend.chat.dto.ChatMessageResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface RedisChatService {
    void saveMessage(ChatMessageDTO message);

    ResponseEntity<List<ChatMessageResponse>> getMessage(String roomId);

    ResponseEntity<Boolean> getOldMessage(String roomId, Integer page);

    Integer getMessageTotal(String roomId);

    Map<String, String> checkLatestMessage(String chatRoomId);
}
