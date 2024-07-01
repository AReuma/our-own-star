package com.example.backend.artistBoard.entity;

import com.example.backend.common.TimeStamped;
import com.example.backend.member.entity.JoinIdol;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistBoardComment extends TimeStamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artist_board_comment_id")
    private Long id; // 댓글 고유 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_idol_id")
    private JoinIdol writer; // 댓글 작성자

    @Column(nullable = false)
    @Lob
    private String content; // 댓글 내용

    @ColumnDefault("FALSE")
    @Column(nullable = false)
    private Boolean isDeleted; // 삭제 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ArtistBoardComment parent; // 부모 댓글

    @OneToMany(mappedBy = "parent", orphanRemoval = true)
    private List<ArtistBoardComment> children = new ArrayList<>(); // 자식 댓글들

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_board_id")
    private ArtistBoard artistBoard; // 게시글

    @Builder.Default
    private Integer likeCount = 0;

    @Builder.Default
    private Integer bookmarkCount = 0;

    public static ArtistBoardComment createArtistBoardcomment(String content, JoinIdol joinIdol, ArtistBoard artistBoard){
        ArtistBoardComment artistBoardComment = ArtistBoardComment.builder()
                .content(content)
                .writer(joinIdol)
                .artistBoard(artistBoard)
                .isDeleted(Boolean.FALSE)
                .build();

        artistBoard.addArtistBoardComment(artistBoardComment);

        return artistBoardComment;
    }

    public void updateParentComment(ArtistBoardComment comment) {
        this.parent = comment;
    }

    public void changeIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        this.likeCount = 0;
        this.bookmarkCount = 0;
    }

    public void increaseLikeCount(){
        this.likeCount++;
    }

    public void decreaseLikeCount(){
        if (this.likeCount > 0) {
            likeCount--;
        }
    }

    public void increaseBookmarkCount(){
        this.bookmarkCount++;
    }

    public void decreaseBookmarkCount(){
        if (this.bookmarkCount > 0) {
            bookmarkCount--;
        }
    }
}
