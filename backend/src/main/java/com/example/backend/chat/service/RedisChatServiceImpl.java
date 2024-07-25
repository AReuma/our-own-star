package com.example.backend.chat.service;

import com.example.backend.chat.dto.ChatMessageDTO;
import com.example.backend.chat.dto.ChatMessageResponse;
import com.example.backend.chat.entity.ChatMessage;
import com.example.backend.chat.entity.ChatRoom;
import com.example.backend.chat.entity.QChatMessage;
import com.example.backend.chat.repository.ChatMessageRepository;
import com.example.backend.chat.repository.ChatRoomRepository;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.member.entity.JoinIdol;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jdk.jfr.Timestamp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.example.backend.artistBoard.entity.market.QArtistMarketBoard.artistMarketBoard;
import static com.example.backend.chat.entity.QChatMessage.chatMessage;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisChatServiceImpl implements RedisChatService{

    private static final int MESSAGE_THRESHOLD = 100;
    //private static final int REDIS_MAX_MESSAGES = 12;

    private final RedisTemplate<String, String> redisTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final EntityManager em;

    @Override
    @Transactional
    public void saveMessage(ChatMessageDTO message) {
        String key = message.getRoom(); // 방 번호
        LocalDateTime nowInSeoul = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        message.setCreatDate(nowInSeoul);

        long timestamp = nowInSeoul.toInstant(ZoneOffset.ofHours(9)).toEpochMilli();

        // 메세지 보낸 시간
        String messageValue = message.getSender() + ":" + message.getContent() + ":" + timestamp +":"+ message.getSenderProfile() + ":false"; // 마지막 false는 db에 저장하지 않은 상태를 나타냄

        redisTemplate.opsForZSet().add(key, messageValue, timestamp);

        // 메시지 개수 확인
        Long messageCount = redisTemplate.opsForZSet().size(key);
        Set<String> strings = redisTemplate.opsForZSet().reverseRange(key, 0, -1);

        int unsavedMessageCount = 0;

        if(strings != null) {
            for (String string : strings) {
                String[] messageInfo = string.split(":");
                boolean isSavedToDB = Boolean.parseBoolean(messageInfo[4]);
                if (isSavedToDB) break;
                else unsavedMessageCount ++;
            }
        }

        /*if (unsavedMessageCount >= MESSAGE_THRESHOLD) {
            // 메시지 DB 저장
            saveMessagesToDB(key);
        }*/

        // Redis 메시지 개수 제한
        long messageCountNonNull = Optional.ofNullable(messageCount).orElse(0L);
        if (unsavedMessageCount >= MESSAGE_THRESHOLD){
            saveMessagesToDB(key, unsavedMessageCount);
        }

        if (messageCountNonNull > MESSAGE_THRESHOLD) {
            redisTemplate.opsForZSet().removeRange(key, 0, 0);
        }
    }

    @Override
    public ResponseEntity<List<ChatMessageResponse>> getMessage(String roomId) {
        Set<String> messages = redisTemplate.opsForZSet().range(roomId, 0, -1);
        List<ChatMessageResponse> messageList = new ArrayList<>();
        if(messages != null) {
            for (String message : messages) {
                String[] messageInfo = message.split(":");
                String sender = messageInfo[0];
                String content = messageInfo[1];
                long timestamp = Long.parseLong(messageInfo[2]);
                String profile = messageInfo[3];

                Instant instant = Instant.ofEpochMilli(timestamp);
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));

                //System.out.println("변환된 한국 시간: " + localDateTime);

                messageList.add(new ChatMessageResponse(sender, profile, content, localDateTime, timestamp));
            }
        }
        return ResponseEntity.ok(messageList);
    }

    @Override
    public ResponseEntity<Boolean> getOldMessage(String roomId, Integer page) {
        /*DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");
        LocalDateTime localDateTime = LocalDateTime.parse(lastDate, formatter);

        System.out.println("localDateTime: "+ localDateTime);
        Instant instant = localDateTime.toInstant(ZoneOffset.ofHours(9));*/

        //long epochMilli = localDateTime.toInstant(ZoneOffset.ofHours(9)).toEpochMilli();
        //Instant instant = localDateTime.atZone(ZoneId.of("Asia/Seoul")).toInstant();
        //long epochMilli = instant.toEpochMilli();

        Set<String> strings = redisTemplate.opsForZSet().reverseRange(roomId, 0, -1);
        int unsavedMessageCount = 0;

        if(strings != null) {
            for (String string : strings) {
                String[] messageInfo = string.split(":");
                boolean isSavedToDB = Boolean.parseBoolean(messageInfo[4]);
                if (isSavedToDB) break;
                else unsavedMessageCount ++;
            }
        }

        System.out.println("savedMessageCount: "+unsavedMessageCount);

        long pageSize = 10L; // 페이지 당 메시지 개수
        long startIndex = 10L * (page - 1);
        long dbStartIndex = Math.max(0, startIndex - (MESSAGE_THRESHOLD - unsavedMessageCount)); //5번쨰부터 가져옴.

        long dbEndIndex = dbStartIndex + pageSize - 1; // 14

        // 5 ~ 14값을 가지고옴.

        long redisMessageCount = redisTemplate.opsForZSet().size(roomId);

        // Redis에 저장된 메시지 개수보다 DB에서 가져올 범위가 더 큰 경우, Redis 메시지는 모두 가져옴
/*        if (dbStartIndex >= redisMessageCount) {
            dbStartIndex = 0;
            dbEndIndex = pageSize - 1;
        }*/

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        List<ChatMessage> fetch = queryFactory
                .selectFrom(chatMessage)
                .where(
                        chatMessage.chatRoom.roomId.eq(roomId)
                                //.and(chatMessage.createDate.lt(Long.parseLong(lastDate)))
                )
                .orderBy(chatMessage.createDate.desc())
                .offset(dbStartIndex)
                .limit(pageSize)
                .fetch();

        if (fetch.isEmpty()){
            return ResponseEntity.ok(Boolean.FALSE);
        }else {
            for (ChatMessage message : fetch) {
                //System.out.println("oldMessage: "+message.get);
                // 메세지 보낸 시간
                String messageValue = message.getMessageSender().getNickname() + ":" + message.getMessage() + ":" + message.getCreateDate() +":"+ message.getMessageSender().getJoinIdolMemberProfile().getImgUrl() + ":true"; // 마지막 false는 db에 저장하지 않은 상태를 나타냄

                redisTemplate.opsForZSet().add(roomId, messageValue, message.getCreateDate());
            }

            return ResponseEntity.ok(Boolean.TRUE);
        }
    }

    @Override
    public Integer getMessageTotal(String roomId) {
        Set<String> strings = redisTemplate.opsForZSet().reverseRange(roomId, 0, -1);
        int unsavedMessageCount = 0;

        if(strings != null) {
            for (String string : strings) {
                String[] messageInfo = string.split(":");
                boolean isSavedToDB = Boolean.parseBoolean(messageInfo[4]);
                if (isSavedToDB) break;
                else unsavedMessageCount ++;
            }
        }

        return unsavedMessageCount;
    }
    @Override
    public Map<String, String> checkLatestMessage(String chatRoomId) {
        Map<String, String> response = new HashMap<>();

        Set<String> latestMessage = redisTemplate.opsForZSet().reverseRange(chatRoomId, 0, 0);

        if (latestMessage != null){
            String[] messageInfo = latestMessage.iterator().next().split(":");
            String content = messageInfo[1];
            long timestamp = Long.parseLong(messageInfo[2]);
            boolean isSavedToDB = Boolean.parseBoolean(messageInfo[4]);

            if (!isSavedToDB){ // 저장되지 않음

                response.put("latestMessage", content);
                response.put("createdDate", String.valueOf(timestamp));

                return response;
            }

        }

        return response;
    }

    @Transactional
    public void saveMessagesToDB(String key, int unsavedMessageCount){
        Set<String> messages = redisTemplate.opsForZSet().reverseRange(key, 0, unsavedMessageCount);

        if(messages != null){
            for (String message : messages) {
                String[] messageInfo = message.split(":");
                String sender = messageInfo[0];
                String content = messageInfo[1];
                long timestamp = Long.parseLong(messageInfo[2]);
                String profile = messageInfo[3];
                boolean isSavedToDB = Boolean.parseBoolean(messageInfo[4]);

                // DB에 저장되지 않은 메시지인지 확인
                if (!isSavedToDB) {
                    // DB에 저장할 ChatMessage 객체 생성
                    ChatRoom chatRoom = chatRoomRepository.findChatRoomByRoomId(key).orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_CHAT_ROOM, "채팅방이 존재하지 않는다."));
                    JoinIdol senderJoinIdol = chatRoom.getMessageReceiver().getNickname().equals(sender) ? chatRoom.getMessageReceiver() : chatRoom.getMessageSender();

                    ChatMessage chatMessage = ChatMessage.createChatMessage(senderJoinIdol, chatRoom, content, timestamp);

                    // DB에 저장
                    chatMessageRepository.save(chatMessage);

                    // Redis에서 메시지의 저장 여부 업데이트
                    String updatedMessageValue = senderJoinIdol.getNickname() + ":" + content + ":" + timestamp+":"+ profile + ":true"; // true indicates saved to DB
                    redisTemplate.opsForZSet().remove(key, message);
                    redisTemplate.opsForZSet().add(key, updatedMessageValue, timestamp);
                }
            }
        }
    }
}
