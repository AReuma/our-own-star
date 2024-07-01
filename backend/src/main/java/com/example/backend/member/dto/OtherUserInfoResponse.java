package com.example.backend.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OtherUserInfoResponse {

    private String nickname;
    private String profileImg;
    private String userInfo;
    private Integer follower;
    private Integer following;
    private boolean isFollow;
}
