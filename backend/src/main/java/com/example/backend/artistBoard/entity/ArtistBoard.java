package com.example.backend.artistBoard.entity;

import com.example.backend.common.TimeStamped;
import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistBoard extends TimeStamped {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_board_id")
    private Long id;

    @Lob
    private String content;

    @JoinColumn(name = "join_idol_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private JoinIdol writer;

    /*@JoinColumn(name = "idol_category_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private IdolCategory idolCategory;*/

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Builder.Default
    private Integer likeCount = 0;

    @Builder.Default
    private Integer bookmarkCount = 0;

    @Builder.Default
    @OneToMany(mappedBy = "artistBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtistBoardImage> imageUrlList = new ArrayList<>();

    @JoinColumn(name = "artist_board_vote_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ArtistBoardVote artistBoardVote;

    @OneToMany(mappedBy = "artistBoard", cascade = CascadeType.ALL)
    private List<ArtistBoardLike> artistBoardLikes = new ArrayList<>();

    @OneToMany(mappedBy = "artistBoard", cascade = CascadeType.ALL)
    private List<ArtistBoardComment> artistBoardComments = new ArrayList<>();

    public void addImage(ArtistBoardImage image) {
        this.imageUrlList.add(image);
    }

    public void addVote(ArtistBoardVote vote) {
        this.artistBoardVote = vote;
    }

    public void addArtistBoardComment(ArtistBoardComment artistBoardComment){
        this.artistBoardComments.add(artistBoardComment);
    }

    public void decreaseLikeCount(){
        if (this.likeCount > 0) {
            likeCount--;
        }
    }

    public void increaseLikeCount(ArtistBoardLike artistBoardLike){
        this.artistBoardLikes.add(artistBoardLike);
        likeCount ++;
    }

    public void decreaseBookmarkCount(){
        if (this.bookmarkCount > 0) {
            bookmarkCount--;
        }
    }

    public void increaseBookmarkCount(){
        bookmarkCount ++;
    }

    public static ArtistBoard createArtistBoard(String content, JoinIdol joinIdol, BoardType boardType){
        return ArtistBoard.builder()
                .content(content)
                .writer(joinIdol)
                .boardType(boardType)
                .build();
    }

}
