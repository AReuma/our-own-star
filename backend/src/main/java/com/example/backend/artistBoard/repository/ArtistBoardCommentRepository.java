package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistBoardComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistBoardCommentRepository extends JpaRepository<ArtistBoardComment, Long> {

}
