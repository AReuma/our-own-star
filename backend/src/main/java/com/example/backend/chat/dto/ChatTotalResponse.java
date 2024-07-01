package com.example.backend.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatTotalResponse {

    private Integer totalPage;
    private Integer totalMessageCount;
}
