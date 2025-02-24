package com.hongik.mentor.hongik_mentor.controller.dto.chat;

import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatMessage;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoom;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponseDto {
    private Long chatRoomId;
    private Long memberId;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
    private boolean isOwner;    //요청자 본인의 메시지인지 식별. (json변수명: owner)

    public ChatMessageResponseDto(ChatMessage chatMessage, boolean isOwner) {
        this.chatRoomId = chatMessage.getChatRoom().getId();
        this.memberId = chatMessage.getSender().getId();
        this.nickname = chatMessage.getNickname();
        this.content = chatMessage.getContent();
        this.createdAt = chatMessage.getCreatedAt();
        this.isOwner = isOwner;
    }
}
