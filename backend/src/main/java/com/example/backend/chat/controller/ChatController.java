package com.example.backend.chat.controller;

import com.example.backend.chat.dto.*;
import com.example.backend.chat.service.ArtistChatService;
import com.example.backend.chat.service.RedisChatService;
import com.example.backend.member.dto.LoginInfoDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/idol/chat")
@RequiredArgsConstructor
@Tag(name = "ChatController", description = "Socket을 이용한 채팅 관리하는 API")
public class ChatController {

    private final ArtistChatService artistChatService;
    private final RedisChatService redisChatService;

    @MessageMapping("/sendMessage/{chatRoom}")
    @SendTo("/sub/topic/chat/{chatRoom}")
    public ChatMessageDTO sendMessage(@Payload @Valid ChatMessageDTO message, @DestinationVariable String chatRoom) {
        log.info("chat Test: "+ message.getRoom());
        //long startTime = System.nanoTime();
        redisChatService.saveMessage(message);
        //artistChatService.saveMessage(message);

        /*long endTime = System.nanoTime();
        long duration = endTime - startTime; // 나노초 단위 시간 차이

        System.out.println("Message saving time: " + duration + " nanoseconds");*/

        return message;
    }

    @MessageMapping("/topic/chat/{chatRoom}")
    public void handleSubscription(@DestinationVariable String chatRoom) {
        // 클라이언트의 구독 요청을 처리하는 로직
        System.out.println("구독 요청이 들어옴: " + chatRoom);
    }

    @PostMapping("/{artist}/isSub") // 채팅이 db에 있는지 확인?
    public ResponseEntity<String> checkAlreadySub(@PathVariable String artist, @RequestBody CheckAlreadySubRequestDTO dto, Authentication authentication){
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistChatService.checkAlreadySub(artist, loginInfoDto.getUsername(), dto.getMessageReceiver());
    }

    @GetMapping("/{artist}/chat/{roomId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessage(@PathVariable String artist, @PathVariable String roomId){
        log.info("메세지 가져오기 ");
        return redisChatService.getMessage(roomId);
        //return artistChatService.getMessage(roomId, artist);
    }

    @GetMapping("/{artist}/getOldMsg/{roomId}/{page}")
    public ResponseEntity<Boolean> getOldMessage(@PathVariable String artist, @PathVariable String roomId, @PathVariable Integer page){
        log.info("옛날 메세지 가져오기 : "+ page);
        return redisChatService.getOldMessage(roomId, page);
    }

    @GetMapping("/{artist}/getTotalPage/{roomId}")
    public ResponseEntity<ChatTotalResponse> getMessageTotalPage(@PathVariable String artist, @PathVariable String roomId, Authentication authentication){
        log.info("getMessageTotalPage: "+ roomId);
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistChatService.getMessageTotalPage(artist, roomId);
    }

    @GetMapping("/{artist}/getReceiverUserInfo/{roomId}")
    public ResponseEntity<ChatReceiverUserInfoResponse> getReceiverUserInfo(@PathVariable String artist, @PathVariable String roomId, Authentication authentication){
        log.info("getReceiverUserInfo");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistChatService.getReceiverUserInfo(artist, roomId, loginInfoDto.getUsername());
    }

    @GetMapping("/{artist}/getChatRoomList")
    public ResponseEntity<List<ChatRoomListResponse>> getChatRoomList(@PathVariable @Valid @NotBlank String artist, Authentication authentication){
        log.info("getReceiverUserInfo");
        LoginInfoDto loginInfoDto = (LoginInfoDto) authentication.getPrincipal();
        return artistChatService.getChatRoomList(artist, loginInfoDto.getUsername());
    }
}
