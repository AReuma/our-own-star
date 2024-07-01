package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistBoardLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArtistBoardLikeRepository extends JpaRepository<ArtistBoardLike, Long> {

    @Query("select bl from ArtistBoardLike bl where bl.artistBoard.id = :boardId and  bl.member.nickname = :username")
    Optional<ArtistBoardLike> findByBoardIdAndUsername(Long boardId, String username);

    Optional<ArtistBoardLike> findByArtistBoardIdAndMemberNickname(Long boardId, String username);
}
