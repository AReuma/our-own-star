package com.example.backend.artistBoard.repository.market;

import com.example.backend.artistBoard.entity.market.ArtistMarketBoardCommentBookmark;
import com.example.backend.artistBoard.entity.market.ArtistMarketBoardCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.file.LinkOption;
import java.util.List;
import java.util.Optional;

public interface ArtistMarketCommentBookmarkRepository extends JpaRepository<ArtistMarketBoardCommentBookmark, Long> {

    Optional<ArtistMarketBoardCommentBookmark> findByArtistMarketBoardCommentIdAndArtistMarketBoardCommentWriterNickname(Long commentId, String nickname);

    List<ArtistMarketBoardCommentBookmark> findByArtistMarketBoardCommentId(Long commentId);
}
