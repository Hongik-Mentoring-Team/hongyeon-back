package com.hongik.mentor.hongik_mentor.config;

import com.hongik.mentor.hongik_mentor.constant.ConstantUri;
import com.hongik.mentor.hongik_mentor.controller.dto.MemberAdminDto;
import com.hongik.mentor.hongik_mentor.controller.dto.MemberResDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatMessageReqDto;
import com.hongik.mentor.hongik_mentor.domain.SocialProvider;
import com.hongik.mentor.hongik_mentor.exception.ErrorCode;
import com.hongik.mentor.hongik_mentor.exception.SendMessageException;
import com.hongik.mentor.hongik_mentor.service.ChatService;
import com.hongik.mentor.hongik_mentor.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Collections;

@Slf4j
@Component
public class StompChannelInterceptor implements ChannelInterceptor {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageConverter messageConverter;
    private final ChatService chatService;
    private final MemberService memberService;
    @Autowired
    public StompChannelInterceptor(@Lazy SimpMessagingTemplate messagingTemplate,
                                   @Lazy MessageConverter messageConverter,
                                   ChatService chatService,
                                   MemberService memberService) {
        this.messagingTemplate = messagingTemplate;
        this.messageConverter = messageConverter;
        this.chatService = chatService;
        this.memberService = memberService;
    }

    @Transactional(readOnly = true)
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);   //WebSocket 메시지 헤더 접근
        Principal principal = accessor.getUser();


        /** 메시지 인증 인가
         * */
        //사용자 인증 검증: 웹소켓세션에 인증정보 없음
        if(principal == null) throw new SendMessageException(ErrorCode.WEB_SOCKET_AUTHENTICATION_NOT_EXISTS);

        //STOMP의 CONNECT 시점에 memberId를 웹소켓 세션에 저장
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            saveMemberIdToWebsocketSession((OAuth2AuthenticationToken) principal, accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            String sessionId = accessor.getSessionId();                     //WebSocket 세션
            String username = principal != null ? principal.getName() : "Unknown";

            // 구독 요청 검증 로직 추가
            if (isSubscriptionAllowed(destination, accessor)) {
                log.info("User {} with SessionId {} is allowed to subscribe to {}", username, sessionId, destination);
                // 구독 허용
            } else {
                log.info("User {} with SessionId {} is NOT allowed to subscribe to {}", username, sessionId, destination);
                // 구독 거부
                throw new IllegalArgumentException("Subscription to destination [" + destination + "] is not allowed.");
            }

        } else if (StompCommand.SEND.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            String sessionId = accessor.getSessionId();
            String user = accessor.getUser() != null ? accessor.getUser().getName() : "Unknown";

            // 클라이언트의 메시지 전송 요청만 검증 => messageBroker가 구독URL로 메시지를 브로드캐시팅할 때도, Interceptor가 가로채나? => Inbout/Outbound 따라 다름
            if (isMessageSendAllowed(destination, principal, message)) {
                log.info("socialId {} with SessionId {} is allowed to send message to {}", user, sessionId, destination);
                // 메시지 전송 허용
            } else {
                // 메시지 전송 거부
                log.info("socialId {} with SessionId {} is NOT allowed to send message to {}", user, sessionId, destination);
                messagingTemplate.convertAndSendToUser(user, "/queue/errors",
                        Collections.singletonMap("message", "sending messages to destination " + destination + "is NOT allowed"));  //해당 에러url을 사용자가 구독해놨어야지 전송된다
                throw new IllegalArgumentException("Sending messages to destination [" + destination + "] is not allowed.");
            }

        }

        return message;
    }

    /**
     * 구독 요청이 허용되는지 여부를 검증하는 메서드
     *
     * @param destination 구독 목적지
     * @param headerAccessor   사용자 인증정보(-> 안에 principal을 가짐)
     * @return 허용 여부
     */
    private boolean isSubscriptionAllowed(String destination, StompHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
//        log.info("isSubscriptionAllowed -- pricipal형식: {}", principal);
        if (destination == null || !destination.startsWith(ConstantUri.SUBSCRIBE_PREFIX)) return false;

        //비회원
        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) principal;
        boolean notRegistered = authentication.getAuthorities()
                .stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_TEMP"));
        if(notRegistered) return false;

        //회원
            //해당 채팅방 멤버인지 확인 => 성능개선  가능: principal에 memberId를 포함시킴
        String socialId = authentication.getPrincipal().getName();
        SocialProvider socialProvider = SocialProvider.from((String) authentication.getPrincipal().getAttributes().get("socialProvider") );

        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");
        boolean isInChatroom = isInChatroom(destination, memberId);
        if(!isInChatroom) return false;

        return true;
    }
    /**
     * 메시지 전송 요청이 허용되는지 여부를 검증하는 메서드
     *
     * @param destination 메시지를 전송할 목적지
     * @param principal        사용자 인증 정보
     * @return 허용 여부
     */
    private boolean isMessageSendAllowed(String destination, Principal principal, Message<?> message) {
        //비회원
        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) principal;
        boolean notRegistered = authentication.getAuthorities()
                .stream().anyMatch(ga -> ga.getAuthority().equals("ROLE_TEMP"));
        if(notRegistered) return false;

        //회원
        if (destination == null || !destination.equals(ConstantUri.STOMP_MESSAGE_URL)) return false;
            //채팅방 멤버 검증
        String socialId = authentication.getPrincipal().getName();
        SocialProvider socialProvider = SocialProvider.from((String) authentication.getPrincipal().getAttributes().get("socialProvider") );
        ChatMessageReqDto messageDto = (ChatMessageReqDto) messageConverter.fromMessage(message, ChatMessageReqDto.class);

        if(!isInChatroom(messageDto, socialId, socialProvider)) return false;

        return true;
    }

    /**
     * 유저가 구독URL 채팅방에 포함되어 있는지 확인함
     * @param destination 구독URL 또는 @Param messageDto
     * @param socialId  소셜아이디
     * @param socialProvider 구글,네이버..
     *
     * */
    private boolean isInChatroom(String destination, Long memberId) {
        Long chatRoomId = Long.parseLong(destination.substring(ConstantUri.SUBSCRIBE_PREFIX.length()));
        boolean isInChatroom = chatService.findChatRoomByMemberId(memberId)
                .stream().anyMatch(chatRoomResponseDto -> chatRoomResponseDto.getId().equals(chatRoomId));

        return isInChatroom;
    }
    private boolean isInChatroom(ChatMessageReqDto messageDto, String socialId, SocialProvider socialProvider) {
        MemberAdminDto dto = memberService.findBySocialId(socialId, socialProvider).orElseThrow();
        Long chatRoomId = messageDto.getChatRoomId();
        boolean isInChatroom = chatService.findChatRoomByMemberId(dto.getId())
                .stream().anyMatch(chatRoomResponseDto -> chatRoomResponseDto.getId().equals(chatRoomId));
        return isInChatroom;
    }


    //기능: CONNECT 시점에 한번만 작동하며, 현재 참가중인 사용자의 memberId를 웹소켓세션에 저장
    private void saveMemberIdToWebsocketSession(OAuth2AuthenticationToken principal, StompHeaderAccessor accessor) {
        OAuth2User oAuth2User = principal.getPrincipal();
        String socialId = oAuth2User.getName();
        SocialProvider socialProvider = SocialProvider.from((String) oAuth2User.getAttributes().get("socialProvider"));
        Long currentUserId = memberService.getMemberIdOnllyForAdmin(socialId, socialProvider);
        accessor.getSessionAttributes().put("memberId", currentUserId);
        log.info("현재 로그인한 사용자의 memberID를 웹소켓세션에 저장하였습니다. 아이디: {}", currentUserId);
    }

}
