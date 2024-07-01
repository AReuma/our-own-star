package com.example.backend.artistBoard.entity.market;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistMarketBoardImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_market_board_image_id")
    private Long id;

    private String artistMarketBoardImgURL;

    @JoinColumn(name = "artist_market_board_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ArtistMarketBoard artistMarketBoard;

    public static ArtistMarketBoardImage saveArtistBoardImg(String imgUrl, ArtistMarketBoard artistMarketBoard){
        ArtistMarketBoardImage marketBoardImage = ArtistMarketBoardImage.builder()
                .artistMarketBoard(artistMarketBoard)
                .artistMarketBoardImgURL(imgUrl)
                .build();

        artistMarketBoard.addImage(marketBoardImage);

        return marketBoardImage;
    }

    public void deleteMarketBoardImage(){
        this.artistMarketBoard = null;
    }
}
