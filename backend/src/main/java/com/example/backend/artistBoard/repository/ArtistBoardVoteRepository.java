package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistBoard;
import com.example.backend.artistBoard.entity.ArtistBoardVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistBoardVoteRepository extends JpaRepository<ArtistBoardVote, Long> {
}
