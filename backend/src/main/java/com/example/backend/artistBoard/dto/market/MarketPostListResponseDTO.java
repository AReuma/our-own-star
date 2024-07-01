package com.example.backend.artistBoard.dto.market;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarketPostListResponseDTO {

    private Long id;
    private String title;
    private String price;
    private boolean isLike;
    private String imgUrl;
}
