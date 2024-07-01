package com.example.backend.artistBoard.dto.market;

import com.example.backend.artistBoard.entity.ArtistBoardComment;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoardComment;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class MarketCommentResponseDTO {

    private Long id;
    private String comment;
    private String writer;
    private String writerProfile;
    private Integer commentCount = 0;
    private Boolean isLike;
    private Integer likeCount;
    private Boolean isBookmark;
    private Integer bookmarkCount;
    private List<MarketCommentResponseDTO> CommentResponseDTO = new ArrayList<>();

    public MarketCommentResponseDTO(Long id, String comment, String writer, String writerProfile, Boolean isLike, Integer likeCount, Boolean isBookmark, Integer bookmarkCount) {
        this.id = id;
        this.comment = comment;
        this.writer = writer;
        this.writerProfile = writerProfile;
        this.isLike = isLike;
        this.likeCount = likeCount;
        this.isBookmark = isBookmark;
        this.bookmarkCount = bookmarkCount;
    }

    public void updateCommentCount(){
        this.commentCount++;
    };

    public static MarketCommentResponseDTO convertMarketCommentToDto(ArtistMarketBoardComment comment, Boolean isLike, Boolean isBookmark) {
        return comment.getIsDeleted() ?
                new MarketCommentResponseDTO(comment.getId(), "삭제된 댓글입니다.", null, null, null, null, null, null) :
                new MarketCommentResponseDTO(comment.getId(), comment.getContent(), comment.getWriter().getNickname(), comment.getWriter().getJoinIdolMemberProfile().getImgUrl(), isLike, comment.getLikeCount(), isBookmark, comment.getBookmarkCount());
    }
}
