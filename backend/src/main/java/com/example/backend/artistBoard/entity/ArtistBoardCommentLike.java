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
public class ArtistBoardCommentLike extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_board_comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_board_comment_id")
    private ArtistBoardComment artistBoardComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_id")
    private JoinIdol member;

    public static ArtistBoardCommentLike createBoardCommentLike(ArtistBoardComment artistBoardComment, JoinIdol member){
        ArtistBoardCommentLike artistBoardLike = ArtistBoardCommentLike.builder()
                .artistBoardComment(artistBoardComment)
                .member(member)
                .build();

        artistBoardComment.increaseLikeCount();

        return artistBoardLike;
    }
}
