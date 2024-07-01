package com.example.backend.artistBoard.repository.market;

import com.example.backend.artistBoard.entity.market.ArtistMarketBoardCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtistMarketCommentLikeRepository extends JpaRepository<ArtistMarketBoardCommentLike, Long> {

    Optional<ArtistMarketBoardCommentLike> findByArtistMarketBoardCommentIdAndArtistMarketBoardCommentWriterNickname(Long commentId, String nickname);

    List<ArtistMarketBoardCommentLike> findByArtistMarketBoardCommentId(Long commentId);
}
