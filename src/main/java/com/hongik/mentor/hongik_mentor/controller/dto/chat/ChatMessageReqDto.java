package com.hongik.mentor.hongik_mentor.controller.dto.chat;

import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatMessage;
import lombok.Data;

import java.time.LocalDateTime;
//!리팩토링
// ChatMessageResponseDto와 내용이 똑같이 변해버림. 나중에 리팩토링 필요
@Data
public class ChatMessageReqDto {
    private Long chatRoomId;
    private String nickname;
    private Long chatRoomMemberId;
//    private Long memberId;
    private String content;
    private LocalDateTime createdAt;

    public ChatMessageReqDto(Long chatRoomId, String nickname, String content, Long chatRoomMemberId) {
        this.chatRoomId = chatRoomId;
        this.nickname = nickname;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.chatRoomMemberId = chatRoomMemberId;
    }

    public ChatMessage toEntity(Member sender) {
        return new ChatMessage(sender, nickname, content, createdAt);
    }
}
