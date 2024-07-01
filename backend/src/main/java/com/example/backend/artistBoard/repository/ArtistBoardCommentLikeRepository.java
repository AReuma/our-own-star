package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistBoardCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistBoardCommentLikeRepository extends JpaRepository<ArtistBoardCommentLike, Long> {
    Optional<ArtistBoardCommentLike> findByArtistBoardCommentIdAndMemberNickname(Long id, String nickname);

    List<ArtistBoardCommentLike> findByArtistBoardCommentId(Long id);
}
