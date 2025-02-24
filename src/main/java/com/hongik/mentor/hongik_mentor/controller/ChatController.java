package com.hongik.mentor.hongik_mentor.controller;

import com.hongik.mentor.hongik_mentor.constant.ConstantUri;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatInitiateDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatMessageReqDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatRoomResponseDto;
import com.hongik.mentor.hongik_mentor.oauth.util.SessionUtil;
import com.hongik.mentor.hongik_mentor.service.ChatService;
import com.hongik.mentor.hongik_mentor.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatService chatService;
    private final MemberService memberService;
    private final SimpMessagingTemplate messagingTemplate;

    //메시지 수신
    @MessageMapping("/chat/message")//클라이언트가 /app/chat/message로 메시지 보냈을 때
    public void broadCastMessage(@Payload ChatMessageReqDto messageDto, StompHeaderAccessor headerAccessor) { //웹소켓 환경에서는 웹소켓 세션 이용(StompHeaderAccessor)
        Long senderId = (Long) headerAccessor.getSessionAttributes().get("memberId");

        //메시지 저장
        chatService.saveChatMessage(messageDto.getChatRoomId(), messageDto, senderId);
        Long chatRoomId = messageDto.getChatRoomId();

        //해당 채팅방으로 메시지 전달
        //메시지를 url(/topic/chat/room)로 브로드캐스팅(url은 구독 url임)
        messagingTemplate.convertAndSend(ConstantUri.SUBSCRIBE_PREFIX + chatRoomId, messageDto);
    }

    //채팅방 및 채팅멤버 생성
    @PostMapping("/api/v1/chatRoom/initiate")
    public ResponseEntity<Map<String, Long>> initiateChat(@RequestBody ChatInitiateDto requestDto) {
        //create ChatRoom, ChatRoomMember at once
        Long chatRoomId = chatService.initiateChat(requestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Collections.singletonMap("chatRoomId", chatRoomId));
    }

    //채팅 메시지 내역 전달: 채팅방 url창을 새로 열때마다 호출
    @GetMapping("/api/v1/chatRoom/history/{chatRoomId}")
    public ResponseEntity<ChatRoomResponseDto> sendMessageHistory(@PathVariable Long chatRoomId, HttpSession httpSession) {
        ChatRoomResponseDto responseDto = chatService.findChatRoom(chatRoomId, SessionUtil.getCurrentMemberId(httpSession));
        return ResponseEntity.ok()
                .body(responseDto);
    }

    @GetMapping("api/v1/chatRoom/{chatRoomId}/check-access")
    public ResponseEntity<Map<String,Object>> checkParticipant(@PathVariable Long chatRoomId, HttpSession httpSession) {
        Long requesterId = SessionUtil.getCurrentMemberId(httpSession);
        return ResponseEntity.ok()
                .body(Collections.singletonMap("canJoin", String.valueOf(chatService.isInChatRoom(chatRoomId, requesterId))));
    }
}
