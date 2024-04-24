package com.example.backend.artistBoard.service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface RedisVoteService {

    boolean hasVoted(String userId, String postId);
    ResponseEntity<String> addVote(String userId, Long postId, int voteChoice);
    String getUserVoteChoice(String userId, String postId);
    Map<String, String> getVoteResults(Long postId);
}
