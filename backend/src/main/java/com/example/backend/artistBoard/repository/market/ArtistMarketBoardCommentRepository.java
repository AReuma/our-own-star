package com.example.backend.artistBoard.repository.market;

import com.example.backend.artistBoard.entity.market.ArtistMarketBoardBookmark;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistMarketBoardCommentRepository extends JpaRepository<ArtistMarketBoardComment,Long> {
    Optional<List<ArtistMarketBoardComment>> findArtistMarketBoardCommentsByArtistMarketBoardIdAndArtistMarketBoardWriterId(Long boardId, Long memberId);
}
