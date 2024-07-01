package com.example.backend.idolCategory.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JoinIdolCategoryUserInfoDTO {

    private String nickname;
    private Boolean isFirst;
    private String userProfileImg;
}
