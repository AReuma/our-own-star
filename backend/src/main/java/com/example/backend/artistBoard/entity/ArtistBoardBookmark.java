package com.example.backend.artistBoard.entity;

import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistBoardBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_board_bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_board_id")
    private ArtistBoard artistBoard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_id")
    private JoinIdol member;

    public static ArtistBoardBookmark createBoardBookmark(ArtistBoard artistBoard, JoinIdol member){
        ArtistBoardBookmark artistBoardBookmark = ArtistBoardBookmark.builder()
                .artistBoard(artistBoard)
                .member(member)
                .build();

        artistBoard.increaseBookmarkCount();

        return artistBoardBookmark;
    }
}
