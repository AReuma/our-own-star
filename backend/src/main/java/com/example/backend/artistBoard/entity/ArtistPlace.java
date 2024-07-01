package com.example.backend.artistBoard.entity;

import com.example.backend.idolCategory.entity.IdolCategory;
import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_place_id")
    private Long id;

    private String lat;

    private String lng;

    @Column(unique = true)
    private String placeName;

    private String addressName;

    private String roadAddressName;

    private String placeImage;

    private String placeUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idol_category_id")
    private IdolCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_id")
    private JoinIdol writer;

    @Lob
    private String memo;

    public static ArtistPlace createArtistPlace(String lat, String lng, String placeName, String addressName, String roadAddressName, String placeImage, String placeUrl, JoinIdol writer, IdolCategory idolCategory, String memo){
        return ArtistPlace.builder()
                .lat(lat)
                .lng(lng)
                .placeName(placeName)
                .addressName(addressName)
                .roadAddressName(roadAddressName)
                .placeImage(placeImage)
                .placeUrl(placeUrl)
                .writer(writer)
                .category(idolCategory)
                .memo(memo)
                .build();
    }
}
