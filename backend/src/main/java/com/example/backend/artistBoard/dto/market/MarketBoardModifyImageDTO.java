package com.example.backend.artistBoard.dto.market;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarketBoardModifyImageDTO {
    private Long imgId;
    private String imgUrl;
}
