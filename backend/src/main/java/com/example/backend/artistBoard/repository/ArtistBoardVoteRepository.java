package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistBoard;
import com.example.backend.artistBoard.entity.ArtistBoardVote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ArtistBoardVoteRepository extends JpaRepository<ArtistBoardVote, Long> {
    //List<ArtistBoardVote> findByVoteExpireTimeBeforeAndSaveFalse(LocalDateTime date);
    List<ArtistBoardVote> findByVoteExpireTimeBeforeAndSaveVote(LocalDateTime date, boolean saveVote);

    ArtistBoardVote findByArtistBoardId(Long artistBoardId);
}
