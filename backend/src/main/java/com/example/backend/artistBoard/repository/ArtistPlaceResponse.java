package com.example.backend.artistBoard.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistPlaceResponse {
    private String addressName;
    private String placeImage;
    private String placeName;
    private String roadAddressName;
    private String lat;
    private String lng;
    private String placeUrl;
    private String memo;
}
