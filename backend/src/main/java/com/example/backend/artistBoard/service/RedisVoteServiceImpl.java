package com.example.backend.artistBoard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisVoteServiceImpl implements RedisVoteService{
    private static final String VOTE_RESULTS_KEY_PREFIX = "voteResults:";   // 투표 결과 Field
    private static final String USER_VOTE_KEY_PREFIX = "userVote:"; // 투표한 사용자 Field

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, String> hashOperations;

    @Autowired
    public RedisVoteServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    /**
     * 회원이 투표를 했는지 확인하는 메서드
     * */
    @Override
    public boolean hasVoted(String userId, String postId) {
        String key = USER_VOTE_KEY_PREFIX + postId;
        return hashOperations.hasKey(key, userId);
    }

    /**
     * 투표 결과 저장하는 메서드
     *
     * @return
     */
    @Override
    public ResponseEntity<String> addVote(String userId, Long postId, int voteChoice) {
        String userKey = USER_VOTE_KEY_PREFIX + postId;
        String voteKey = VOTE_RESULTS_KEY_PREFIX + postId;

        if (!hasVoted(userId, String.valueOf(postId))) { // 투표한적이 없다.
            hashOperations.put(userKey, userId, String.valueOf(voteChoice));
            hashOperations.increment(voteKey, String.valueOf(voteChoice), 1);
            return ResponseEntity.ok().body("투표 완료");
        }else {
            String userVoteChoice = getUserVoteChoice(userId, String.valueOf(postId));
            if (!userVoteChoice.equals(String.valueOf(voteChoice))) {
                hashOperations.put(userKey, userId, userVoteChoice);
                hashOperations.increment(voteKey, userVoteChoice, -1);
                hashOperations.put(userKey, userId, String.valueOf(voteChoice));
                hashOperations.increment(voteKey, String.valueOf(voteChoice), 1);

                return ResponseEntity.ok().body("투표 변경 완료");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("중복 투표");
        }
    }

    /**
     * 회원의 투표 결과 출력하는 메서드
     */
    @Override
    public String getUserVoteChoice(String userId, String postId) {
        String userKey = USER_VOTE_KEY_PREFIX + postId;
        return hashOperations.get(userKey, userId);
    }

    /**
     * 투표 결과를 퍼센트로 출력하는 메서드
     * */
    @Override
    public Map<String, String> getVoteResults(Long postId) {
        String key = VOTE_RESULTS_KEY_PREFIX + postId;
        Map<String, String> voteInfo = hashOperations.entries(key);
        Map<String, String> voteResult = new HashMap<>();
        int totalVotes = 0;

        // 총 투표 수 계산
        for (String count : voteInfo.values()) {
            totalVotes += Integer.parseInt(count);
        }

        for (Map.Entry<String, String> entry : voteInfo.entrySet()) {
            String choice = entry.getKey();
            int count = Integer.parseInt(entry.getValue());
            double percentage = (double) count / totalVotes * 100.0;
            voteResult.put(choice, String.valueOf(percentage));
        }

        return voteResult;
    }

}
