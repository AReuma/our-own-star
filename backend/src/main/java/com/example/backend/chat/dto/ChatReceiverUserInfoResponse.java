package com.example.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatReceiverUserInfoResponse {

    private String receiver;
    private String receiverProfile;
}
