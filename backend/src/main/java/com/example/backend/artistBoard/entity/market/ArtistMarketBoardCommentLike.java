package com.example.backend.artistBoard.entity.market;

import com.example.backend.common.TimeStamped;
import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistMarketBoardCommentLike extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_market_comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_market_board_comment_id")
    private ArtistMarketBoardComment artistMarketBoardComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_id")
    private JoinIdol member;

    public static ArtistMarketBoardCommentLike createArtistMarketBoardCommentLike(ArtistMarketBoardComment artistMarketBoardComment, JoinIdol member){
        ArtistMarketBoardCommentLike commentLike = ArtistMarketBoardCommentLike.builder()
                .artistMarketBoardComment(artistMarketBoardComment)
                .member(member)
                .build();

        artistMarketBoardComment.increaseLikeCount();

        return commentLike;
    }
}
