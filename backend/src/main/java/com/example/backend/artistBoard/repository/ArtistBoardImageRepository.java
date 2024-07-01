package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistBoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistBoardImageRepository extends JpaRepository<ArtistBoardImage, Long> {
}
