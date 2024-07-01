package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistBoardBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistBoardBookmarkRepository extends JpaRepository<ArtistBoardBookmark, Long> {

    Optional<ArtistBoardBookmark> findByArtistBoardIdAndMemberNickname(Long boardId, String nickname);
}
