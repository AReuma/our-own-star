package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistPlaceRepository extends JpaRepository<ArtistPlace, Long> {
    List<ArtistPlace> findByCategoryArtist(String Artist);
}
