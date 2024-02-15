package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistBoard;
import com.example.backend.artistBoard.entity.ArtistBoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistBoardRepository extends JpaRepository<ArtistBoard, Long> {
}
