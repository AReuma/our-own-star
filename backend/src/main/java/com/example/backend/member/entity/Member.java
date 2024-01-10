package com.example.backend.member.entity;

import com.example.backend.common.TimeStamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends TimeStamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String phoneNum;

    @Enumerated(EnumType.STRING)
    private Auth role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;
}
