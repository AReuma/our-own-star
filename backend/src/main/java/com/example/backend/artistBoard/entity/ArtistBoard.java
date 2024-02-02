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
    @OneToMany(mappedBy = "artistBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtistBoardImage> imageUrlList = new ArrayList<>();

    @JoinColumn(name = "artist_board_vote_id")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private ArtistBoardVote artistBoardVote;

    public void addImage(ArtistBoardImage image) {
        this.imageUrlList.add(image);
    }


    public void addVote(ArtistBoardVote vote) {
        this.artistBoardVote = vote;
    }

    public static ArtistBoard createArtistBoard(String content, JoinIdol joinIdol, BoardType boardType){
        return ArtistBoard.builder()
                .content(content)
                .writer(joinIdol)
                .boardType(boardType)
                .build();
    }

}
