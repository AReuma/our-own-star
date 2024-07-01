package com.example.backend.artistBoard.entity.market;

import com.example.backend.artistBoard.entity.*;
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
public class ArtistMarketBoard extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_market_board_id")
    private Long id;

    private String title;

    @Lob
    private String content;

    private String price;

    @Enumerated(EnumType.STRING)
    private MarketBoardStatus marketBoardStatus;

    @JoinColumn(name = "join_idol_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private JoinIdol writer;

    @Builder.Default
    private Integer likeCount = 0;

    @Builder.Default
    private Integer bookmarkCount = 0;

    @Builder.Default
    @OneToMany(mappedBy = "artistMarketBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArtistMarketBoardImage> imageUrlList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "artistMarketBoard", cascade = CascadeType.ALL)
    private List<ArtistMarketBoardLike> artistMarketLike = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "artistMarketBoard", cascade = CascadeType.ALL)
    private List<ArtistMarketBoardComment> artistMarketBoardComments = new ArrayList<>();

    public void addImage(ArtistMarketBoardImage image) {
        this.imageUrlList.add(image);
    }

    public void addArtistMarketBoardComment(ArtistMarketBoardComment artistMarketBoardComment){
        this.artistMarketBoardComments.add(artistMarketBoardComment);
    }

    public void decreaseLikeCount(){
        if (this.likeCount > 0) {
            likeCount--;
        }
    }

    public void increaseLikeCount(ArtistMarketBoardLike artistMarketBoardLike){
        this.artistMarketLike.add(artistMarketBoardLike);
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

    public void updateBoardStatus(MarketBoardStatus marketBoardStatus){
        this.marketBoardStatus = marketBoardStatus;
    }

    public static ArtistMarketBoard createArtistMarketBoard(String title, String content, JoinIdol joinIdol, String price){
        return ArtistMarketBoard.builder()
                .title(title)
                .content(content)
                .writer(joinIdol)
                .price(price)
                .marketBoardStatus(MarketBoardStatus.SELL)
                .build();
    }

    public ArtistMarketBoard modifyMarketBoard(String title, String price, String content){
        this.title = title;
        this.price = price;
        this.content = content;

        return this;
    }
}
