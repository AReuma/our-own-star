package com.example.backend.artistBoard.repository.market;

import com.example.backend.artistBoard.entity.market.ArtistMarketBoardBookmark;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistMarketBoardBookmarkRepository extends JpaRepository<ArtistMarketBoardBookmark,Long> {
    Optional<ArtistMarketBoardBookmark> findArtistMarketBoardBookmarkByArtistMarketBoardIdAndMemberId(Long boardId, Long memberId);
    Optional<List<ArtistMarketBoardBookmark>> findArtistMarketBoardBookmarksByArtistMarketBoardIdAndArtistMarketBoardWriterId(Long boardId, Long memberId);
}
