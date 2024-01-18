package com.example.backend.member.entity;

import com.example.backend.common.TimeStamped;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends TimeStamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Email
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String nickname;

    @NotBlank
    private String phoneNum;

    @Enumerated(EnumType.STRING)
    private Auth role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    public static Member createMember(String username, String password, String nickname, String phoneNum, SocialType socialType){

        return Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .phoneNum(phoneNum)
                .role(Auth.ROLE_USER)
                .socialType(socialType)
                .build();
    }
}
