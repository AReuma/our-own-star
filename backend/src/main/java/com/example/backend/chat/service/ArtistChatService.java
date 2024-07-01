package com.example.backend.chat.service;

import com.example.backend.chat.dto.ChatReceiverUserInfoResponse;
import com.example.backend.chat.dto.ChatRoomListResponse;
import com.example.backend.chat.dto.ChatTotalResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ArtistChatService {
    ResponseEntity<String> checkAlreadySub(String artist, String messageSender, String messageReceiver);

    ResponseEntity<ChatTotalResponse> getMessageTotalPage(String artist, String roomId);

    ResponseEntity<ChatReceiverUserInfoResponse> getReceiverUserInfo(String artist, String roomId, String username);

    ResponseEntity<List<ChatRoomListResponse>> getChatRoomList(String artist, String username);
}
