package com.example.backend.artistBoard.entity.market;

import com.example.backend.common.TimeStamped;
import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistMarketBoardCommentBookmark extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_market_comment_bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_market_board_comment_id")
    private ArtistMarketBoardComment artistMarketBoardComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_id")
    private JoinIdol member;

    public static ArtistMarketBoardCommentBookmark createArtistMarketBoardCommentBookmark(ArtistMarketBoardComment artistMarketBoardComment, JoinIdol member){
        ArtistMarketBoardCommentBookmark commentLike = ArtistMarketBoardCommentBookmark.builder()
                .artistMarketBoardComment(artistMarketBoardComment)
                .member(member)
                .build();

        artistMarketBoardComment.increaseBookmarkCount();

        return commentLike;
    }
}
