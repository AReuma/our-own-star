package com.example.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomListResponse {

    private String nickname;
    private String userProfile;
    private String latestMessage;
    private LocalDateTime createdDate;
    private String roomId;
}
