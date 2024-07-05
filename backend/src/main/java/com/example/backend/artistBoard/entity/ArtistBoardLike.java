package com.example.backend.artistBoard.entity;

import com.example.backend.common.TimeStamped;
import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistBoardLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_board_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_board_id")
    private ArtistBoard artistBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_id")
    private JoinIdol member;

    public static ArtistBoardLike createBoardLike(ArtistBoard artistBoard, JoinIdol member){
        ArtistBoardLike artistBoardLike = ArtistBoardLike.builder()
                .artistBoard(artistBoard)
                .member(member)
                .build();

        artistBoard.increaseLikeCount(artistBoardLike);

        return artistBoardLike;
    }
}
