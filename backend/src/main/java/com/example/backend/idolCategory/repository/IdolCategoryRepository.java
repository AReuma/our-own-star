package com.example.backend.idolCategory.repository;

import com.example.backend.idolCategory.entity.IdolCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdolCategoryRepository extends JpaRepository<IdolCategory, Long> {
    Optional<IdolCategory> findByArtist(String artist);
}
