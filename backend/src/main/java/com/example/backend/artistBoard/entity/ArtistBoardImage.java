package com.example.backend.artistBoard.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistBoardImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_board_image_id")
    private Long id;

    private String artistBoardImgURL;

    @JoinColumn(name = "artist_board_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ArtistBoard artistBoard;

    public static ArtistBoardImage saveArtistBoardImg(String imgUrl, ArtistBoard artistBoard){
        ArtistBoardImage boardImage = ArtistBoardImage.builder()
                .artistBoard(artistBoard)
                .artistBoardImgURL(imgUrl)
                .build();

        artistBoard.addImage(boardImage);
        return boardImage;
    }
}
