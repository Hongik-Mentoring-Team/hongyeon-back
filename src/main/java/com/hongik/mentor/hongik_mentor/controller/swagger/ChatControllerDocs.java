package com.hongik.mentor.hongik_mentor.controller.swagger;

import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatInitiateDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatMessageReqDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatRoomResponseDto;
import com.hongik.mentor.hongik_mentor.oauth.util.SessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.Map;

@Tag(name = "채팅 API", description = "채팅방, 채팅멤버, 채팅 메시지 를 모두 포함한 API")
public interface ChatControllerDocs {

    @Operation(summary = "채팅방에 메시지 보내기", description = "송신자가 보낸 채팅 메시지는 여기로 옵니다")
    @ApiResponse(
            responseCode = "200",
            description = "성공, 반환 컨텐츠는 없음"
    )
    public void broadCastMessage(@Payload ChatMessageReqDto messageDto, StompHeaderAccessor headerAccessor);

    @Operation(summary = "채팅 시작 위치", description = "모집이 완료된 이후 채팅방 생성, 채팅 멤버 등록은 여기에서 이루어집니다. + 채팅방이 성공적으로 생성되면 식별자가 반환됩니다")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(name = "chatRoomId", type = "Long", example = "1")
            )
    )
    public ResponseEntity<Map<String, Long>> initiateChat(@RequestBody ChatInitiateDto requestDto);

    @Operation(summary = "채팅방 모든 정보 조회" , description = "처음 채팅방에 들어갈 때 사용합니다. 모든 메시지, 멤버, 채팅방 정보를 반환합니다")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatRoomResponseDto.class)
            )
    )
    public ResponseEntity<ChatRoomResponseDto> sendChatRoomHistory(@PathVariable Long chatRoomId, HttpSession httpSession);

    @Operation(summary = "채팅방에 참여중인지 확인" , description = "참여중이면 true, 아니면 false" )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(name = "canJoin", type = "String", example = "true/false 중 1")
            )
    )
    public ResponseEntity<Map<String,Object>> checkParticipant(@PathVariable Long chatRoomId, HttpSession httpSession);
}
