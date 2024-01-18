package com.example.backend.member.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "emailCode", timeToLive = 180)    // 3분 지나면 없애라
public class EmailCertificationResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -214490344996507077L;

    @Id
    private String email;

    private String code;
}
