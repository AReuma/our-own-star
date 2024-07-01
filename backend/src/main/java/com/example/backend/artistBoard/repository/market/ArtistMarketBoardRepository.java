package com.example.backend.artistBoard.repository.market;

import com.example.backend.artistBoard.entity.market.ArtistMarketBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistMarketBoardRepository extends JpaRepository<ArtistMarketBoard,Long> {

    Optional<ArtistMarketBoard> findArtistMarketBoardByIdAndWriterId(Long boardId, Long joinIdolId);
}
