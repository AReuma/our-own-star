package com.example.backend.artistBoard.dto.market;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarketBoardModifyResponseDTO {
    private String title;
    private String price;
    private String content;
    private List<MarketBoardModifyImageDTO> imgList = new ArrayList<>();
}
