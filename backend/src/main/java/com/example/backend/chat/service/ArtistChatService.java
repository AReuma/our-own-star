package com.example.backend.chat.service;

import com.example.backend.chat.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ArtistChatService {
    ResponseEntity<String> checkAlreadySub(String artist, String messageSender, String messageReceiver);

    ResponseEntity<ChatTotalResponse> getMessageTotalPage(String artist, String roomId);

    ResponseEntity<ChatReceiverUserInfoResponse> getReceiverUserInfo(String artist, String roomId, String username);

    ResponseEntity<List<ChatRoomListResponse>> getChatRoomList(String artist, String username);

    void saveMessage(ChatMessageDTO message);

    ResponseEntity<List<ChatMessageResponse>> getMessage(String roomId, String artist);
}
