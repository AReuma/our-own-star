package com.example.backend.artistBoard.entity;

import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistFollow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_follow_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private JoinIdol follower;  // 팔로우 신청하는 사람

    @ManyToOne
    @JoinColumn(name = "following_id")
    private JoinIdol following; // 팔로우 받은 사람

    public static ArtistFollow createArtistBoard(JoinIdol follower, JoinIdol following){
        return ArtistFollow.builder()
                .follower(follower)
                .following(following)
                .build();
    }

}