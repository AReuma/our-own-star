package com.example.backend.artistBoard.repository.market;

import com.example.backend.artistBoard.entity.ArtistBoardLike;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoardLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistMarketBoardLikeRepository extends JpaRepository<ArtistMarketBoardLike,Long> {

    Optional<ArtistMarketBoardLike> findArtistMarketBoardLikeByArtistMarketBoardIdAndMemberId(Long boardId, Long memberId);
    Optional<List<ArtistMarketBoardLike>> findArtistMarketBoardLikesByArtistMarketBoardIdAndArtistMarketBoardWriterId(Long boardId, Long memberId);
}
