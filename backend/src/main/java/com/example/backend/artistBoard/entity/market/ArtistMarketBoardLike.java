package com.example.backend.artistBoard.entity.market;

import com.example.backend.artistBoard.entity.ArtistBoard;
import com.example.backend.common.TimeStamped;
import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistMarketBoardLike extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_market_board_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_market_board_id")
    private ArtistMarketBoard artistMarketBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_id")
    private JoinIdol member;

    public static ArtistMarketBoardLike createMarketBoardLike(ArtistMarketBoard artistMarketBoard, JoinIdol member){
        ArtistMarketBoardLike artistBoardLike = ArtistMarketBoardLike.builder()
                .artistMarketBoard(artistMarketBoard)
                .member(member)
                .build();

        artistMarketBoard.increaseLikeCount(artistBoardLike);

        return artistBoardLike;
    }
}
