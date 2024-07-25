package com.example.backend.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    @NotBlank
    private String sender;

    @NotBlank
    private String content;

    @NotBlank
    private String room;

    @NotBlank
    private String senderProfile;

    private LocalDateTime creatDate;

    private String artist;

    public void setCreatDate(LocalDateTime creatDate) {
        this.creatDate = creatDate;
    }
}
