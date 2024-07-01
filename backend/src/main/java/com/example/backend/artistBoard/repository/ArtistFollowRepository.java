package com.example.backend.artistBoard.repository;

import com.example.backend.artistBoard.entity.ArtistFollow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArtistFollowRepository extends JpaRepository<ArtistFollow, Long> {
    Optional<ArtistFollow> findByFollowerNicknameAndFollowingNickname(String userNickname, String otherUserNickname);
}
