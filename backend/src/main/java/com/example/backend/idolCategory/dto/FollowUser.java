package com.example.backend.idolCategory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FollowUser {

    @NotBlank
    private String artist;

    @NotBlank
    private String followUser;
}
