package com.example.backend.member.dto.mypage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private String nickname;
    private String profileImg;
    private String userInfo;
    private Integer follower;
    private Integer following;
}
