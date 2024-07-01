package com.example.backend.artistBoard.dto.market;

import com.example.backend.artistBoard.dto.PostVoteResponseDTO;
import com.example.backend.artistBoard.entity.BoardType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MarketPostResponseDTO {

    private Long id;
    private String title;
    private String price;
    private String content;
    private String writer;
    private LocalDateTime createDate;
    private String writerProfile;
    private String marketBoardStatus;
    private String boardStatusString;
    private boolean isLike;
    private Integer likeCount;
    private boolean isBookmark;
    private Integer bookmarkCount;
    private Integer commentCount;

    //image
    private Optional<List<String>> image = Optional.empty();

}
