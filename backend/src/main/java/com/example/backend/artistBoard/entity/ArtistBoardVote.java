package com.example.backend.artistBoard.entity;

import com.example.backend.common.TimeStamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 선택지는 최대 4개 가능
 * **/
@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistBoardVote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_board_vote_id")
    private Long id;

    private String choice1;
    private String choice2;
    private String choice3;
    private String choice4;

    private int choiceTotalCount;
    private int choiceCount1;
    private int choiceCount2;
    private int choiceCount3;
    private int choiceCount4;
    private int resultChoiceCount;
    private Boolean saveVote;

    /*@JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member writer;*/

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "artistBoardVote")
    private ArtistBoard artistBoard;

    private LocalDateTime voteExpireTime;

    public static ArtistBoardVote createVote(String choice1, String choice2, String choice3, String choice4, int choiceTotalCount, ArtistBoard artistBoard, LocalDateTime voteExpireTime ){
        ArtistBoardVote vote = ArtistBoardVote.builder()
                .choice1(choice1)
                .choice2(choice2)
                .choice3(choice3)
                .choice4(choice4)
                .choiceTotalCount(choiceTotalCount)
                .choiceCount1(0)
                .choiceCount2(0)
                .choiceCount3(0)
                .choiceCount4(0)
                .resultChoiceCount(0)
                .saveVote(Boolean.FALSE)
                .artistBoard(artistBoard)
                .voteExpireTime(voteExpireTime)
                .build();

        artistBoard.addVote(vote);

        return vote;
    }

    public void updateResult(int choiceCount1, int choiceCount2, int choiceCount3, int choiceCount4, int resultChoiceCount){
        this.choiceCount1 = choiceCount1;
        this.choiceCount2 = choiceCount2;
        this.choiceCount3 = choiceCount3;
        this.choiceCount4 = choiceCount4;
        this.resultChoiceCount = resultChoiceCount;
        this.saveVote = Boolean.TRUE;
    }
}
