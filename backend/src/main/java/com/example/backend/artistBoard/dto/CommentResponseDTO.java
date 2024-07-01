package com.example.backend.artistBoard.dto;

import com.example.backend.artistBoard.entity.ArtistBoardComment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class CommentResponseDTO {

    private Long id;
    private String comment;
    private String writer;
    private String writerProfile;
    private Integer commentCount = 0;
    private Boolean isLike;
    private Integer likeCount;
    private Boolean isBookmark;
    private Integer bookmarkCount;
    private List<CommentResponseDTO> CommentResponseDTO = new ArrayList<>();

    public CommentResponseDTO(Long id, String comment, String writer, String writerProfile, Boolean isLike, Integer likeCount, Boolean isBookmark, Integer bookmarkCount) {
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

    public static CommentResponseDTO convertCommentToDto(ArtistBoardComment comment, boolean isLike, boolean  isBookmark) {
        return comment.getIsDeleted() ?
                new CommentResponseDTO(comment.getId(), "삭제된 댓글입니다.", null, null, null, null, null, null) :
                new CommentResponseDTO(comment.getId(), comment.getContent(), comment.getWriter().getNickname(), comment.getWriter().getJoinIdolMemberProfile().getImgUrl(), isLike, comment.getLikeCount(), isBookmark, comment.getBookmarkCount());
    }
}
