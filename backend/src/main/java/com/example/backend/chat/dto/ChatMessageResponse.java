package com.example.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private String sender;
    private String senderProfile;
    private String message;
    private LocalDateTime creatDate;
    private long dateLongType;
}
