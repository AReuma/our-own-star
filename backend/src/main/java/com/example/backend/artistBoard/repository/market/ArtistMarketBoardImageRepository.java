package com.example.backend.artistBoard.repository.market;

import com.example.backend.artistBoard.entity.market.ArtistMarketBoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistMarketBoardImageRepository extends JpaRepository<ArtistMarketBoardImage,Long> {

    Optional<List<ArtistMarketBoardImage>> findArtistMarketBoardImagesByArtistMarketBoardIdAndArtistMarketBoardWriterId(Long boardId, Long joinIdolId);
}
