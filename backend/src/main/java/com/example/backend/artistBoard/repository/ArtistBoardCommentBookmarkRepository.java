package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistBoardCommentBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistBoardCommentBookmarkRepository extends JpaRepository<ArtistBoardCommentBookmark, Long> {
    Optional<ArtistBoardCommentBookmark> findByArtistBoardCommentIdAndMemberNickname(Long id, String nickname);

    List<ArtistBoardCommentBookmark> findByArtistBoardCommentId(Long id);
}
