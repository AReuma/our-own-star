package com.example.backend.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JoinIdolMemberProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "join_idol_member_profile_id")
    private Long id;

    //private String idolCategory;

    private String imgUrl;


    @Transient
    private static final List<String> nicknames = Arrays.asList("basic/basic-account-profile-blue.png", "basic/basic-account-profile-pink.png", "basic/basic-account-profile-skyblue.png");

    public static String generateRandomProfileImage() {
        Random random = new Random();
        int randomIndex = random.nextInt(nicknames.size());
        return nicknames.get(randomIndex);
    }

    public static JoinIdolMemberProfile createJoinIdolMemberProfile(){
        return JoinIdolMemberProfile.builder().imgUrl(generateRandomProfileImage()).build();
    }
}
