package com.example.backend.chat.service;

import com.example.backend.chat.dto.ChatReceiverUserInfoResponse;
import com.example.backend.chat.dto.ChatRoomListResponse;
import com.example.backend.chat.dto.ChatTotalResponse;
import com.example.backend.chat.entity.ChatMessage;
import com.example.backend.chat.entity.ChatRoom;
import com.example.backend.chat.repository.ChatRoomRepository;
import com.example.backend.common.exception.AppException;
import com.example.backend.common.exception.ErrorCode;
import com.example.backend.idolCategory.repository.IdolCategoryRepository;
import com.example.backend.member.entity.JoinIdol;
import com.example.backend.member.repository.JoinIdolRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.example.backend.chat.entity.QChatMessage.chatMessage;
import static com.example.backend.chat.entity.QChatRoom.chatRoom;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtistChatServiceImpl implements ArtistChatService{

    private final ChatRoomRepository chatRoomRepository;
    private final IdolCategoryRepository idolCategoryRepository;
    private final JoinIdolRepository joinIdolRepository;
    private final RedisChatService redisChatService;
    private final EntityManager em;

    private void getIdolCategoryByArtist(String artist){
        idolCategoryRepository.findByArtist(artist).orElseThrow(() -> new AppException(ErrorCode.IDOL_CATEGORY_NOT_FOUND, "아티스트가 존재하지않습니다"));
    }

    private JoinIdol checkJoinMember(String username, String artist){
        return joinIdolRepository.findJoinIdolByMemberUsernameAndIdolCategoryArtist(username, artist).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_JOIN_IDOL_CATEGORY, "아이디로 체크: 권한이 없는 회원입니다."));
    }

    private JoinIdol checkJoinMemberWithNickname(String nickname, String artist){
        return joinIdolRepository.findJoinIdolByNicknameAndIdolCategoryArtist(nickname, artist).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_JOIN_IDOL_CATEGORY, "닉네임으로 체크: 권한이 없는 회원입니다."));
    }

    @Override
    @Transactional
    public ResponseEntity<String> checkAlreadySub(String artist, String messageSender, String messageReceiver) {
        // true : 이미 채팅 방이 있다.
        // false : 채팅방이 없다.

        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol sender = checkJoinMember(messageSender, artist);
        JoinIdol receiver = checkJoinMemberWithNickname(messageReceiver, artist);

        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        ChatRoom checkChatRoom = queryFactory.selectFrom(chatRoom)
                .where(
                        (chatRoom.messageSender.id.eq(sender.getId()).and(chatRoom.messageReceiver.id.eq(receiver.getId())))
                                .or(chatRoom.messageSender.id.eq(receiver.getId()).and(chatRoom.messageReceiver.id.eq(sender.getId())))
                ).fetchOne();


        // 채팅 방이 있는지 확인 //IsExist
        //Optional<ChatRoom> checkChatRoom = chatRoomRepository.findByMessageSenderIdAndMessageReceiverId(sender.getId(), receiver.getId());

        if (checkChatRoom == null){ // 처음 채팅
            String roomId = UUID.randomUUID().toString();
            ChatRoom chatRoom = ChatRoom.createChatRoom(roomId, sender, receiver);

            chatRoomRepository.save(chatRoom);
            return ResponseEntity.ok(roomId);
        }else { // 예전 채팅
            return ResponseEntity.ok(checkChatRoom.getRoomId());
        }
    }

    @Override
    public ResponseEntity<ChatTotalResponse> getMessageTotalPage(String artist, String roomId) {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        int totalResults = Math.toIntExact(queryFactory
                .selectFrom(chatMessage)
                .where(chatMessage.chatRoom.roomId.eq(roomId))
                .fetchCount());

        Integer messageTotal = redisChatService.getMessageTotal(roomId);

        Integer totalPages = (int) Math.ceil((double) (totalResults + messageTotal) / 10);

        System.out.println("redis: "+messageTotal+"/ db: "+ totalResults);

        return ResponseEntity.ok(new ChatTotalResponse(totalPages, (messageTotal + totalResults)));
    }

    @Override
    public ResponseEntity<ChatReceiverUserInfoResponse> getReceiverUserInfo(String artist, String roomId, String username) {
        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        // 채팅방있는지 확인
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);

        ChatRoom findChatRoom = queryFactory
                .selectFrom(chatRoom)
                .where(chatRoom.roomId.eq(roomId).and((chatRoom.messageSender.eq(user).or(chatRoom.messageReceiver.eq(user)))))
                .fetchOne();

        if (findChatRoom == null) {
            throw new AppException(ErrorCode.NOT_FOUND_CHAT_ROOM, "Chat room not found");
        }

        JoinIdol receiverUserInfo = findChatRoom.getMessageReceiver().equals(user) ? findChatRoom.getMessageSender() : findChatRoom.getMessageReceiver();

        return ResponseEntity.ok(new ChatReceiverUserInfoResponse(receiverUserInfo.getNickname(), receiverUserInfo.getJoinIdolMemberProfile().getImgUrl()));
    }

    @Override
    public ResponseEntity<List<ChatRoomListResponse>> getChatRoomList(String artist, String username) {
        // 최근 채팅 메세지, 메세지 보낸 시간, 보낸 사람 이름, 닉네임

        // 카테고리가 존재하는지 확인
        getIdolCategoryByArtist(artist);

        // 카테고리에 가입된 회원인지 확인
        JoinIdol user = checkJoinMember(username, artist);

        // 1. 내가 가입된 채팅방 검색
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        List<ChatRoom> chatRooms = queryFactory.selectFrom(chatRoom)
                .where(chatRoom.messageReceiver.eq(user).or(chatRoom.messageSender.eq(user)))
                .fetch();

        // 2. 채팅방 최근 메세지 + 시간 가져오기
        ChatMessage latestMessage;
        List<ChatRoomListResponse> response = new ArrayList<>();
        for (ChatRoom chatRoom : chatRooms) {
            JoinIdol userInfo = chatRoom.getMessageReceiver().equals(user) ? chatRoom.getMessageSender() : chatRoom.getMessageReceiver();

            // 1) redis에 저장하지않은 메세지 있는지 확인하기
            Map<String, String> redisLatestMessage = redisChatService.checkLatestMessage(chatRoom.getRoomId());

            // 2) redis에 없으면 db에 있는 마지막 메세지 가져오기
            if (redisLatestMessage.isEmpty()) { // 메세지가 전부 저장됨

                // 최근 저장된 메세지를 찾기
                ChatMessage findLatestMessage = queryFactory.selectFrom(chatMessage)
                        .groupBy(chatMessage.chatRoom.roomId.eq(chatRoom.getRoomId()))
                        .orderBy(chatMessage.createDate.desc())
                        .fetchFirst();

                LocalDateTime localDateTime = instantTimeToLocalDateTime(findLatestMessage.getCreateDate());

                response.add(new ChatRoomListResponse(userInfo.getNickname(), userInfo.getJoinIdolMemberProfile().getImgUrl(), findLatestMessage.getMessage(), localDateTime, chatRoom.getRoomId()));

            } else {    // 최근 메세지는 redis에 있음
                String message = redisLatestMessage.get("latestMessage");
                Long date = Long.parseLong(redisLatestMessage.get("createdDate"));

                LocalDateTime localDateTime = instantTimeToLocalDateTime(date);

                response.add(new ChatRoomListResponse(userInfo.getNickname(), userInfo.getJoinIdolMemberProfile().getImgUrl(), message, localDateTime, chatRoom.getRoomId()));
            }

        }

        // 3. 최근별로 정렬하기
        response.sort(Comparator.comparing(ChatRoomListResponse::getCreatedDate).reversed());

        return ResponseEntity.ok(response);
    }

    public LocalDateTime instantTimeToLocalDateTime(Long instantTime){
        Instant instant = Instant.ofEpochMilli(instantTime);
        return LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul"));
    }
}
