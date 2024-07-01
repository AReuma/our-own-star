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
public class ArtistBoardCommentBookmark extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_board_comment_bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_board_comment_id")
    private ArtistBoardComment artistBoardComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_id")
    private JoinIdol member;

    public static ArtistBoardCommentBookmark createBoardCommentBookmark(ArtistBoardComment artistBoardComment, JoinIdol member){
        ArtistBoardCommentBookmark artistBoardLike = ArtistBoardCommentBookmark.builder()
                .artistBoardComment(artistBoardComment)
                .member(member)
                .build();

        artistBoardComment.increaseBookmarkCount();

        return artistBoardLike;
    }
}
