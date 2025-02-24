package com.hongik.mentor.hongik_mentor.controller.dto.chat;

import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoom;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomStatus;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class ChatRoomResponseDto {
        private Long id;
        private String name;    //채팅방 이름
        private List<ChatRoomMemberResponseDto> chatMembers = new ArrayList<>();
        private List<ChatMessageResponseDto> chatMessages = new ArrayList<>();
        private ChatRoomStatus roomStatus;
        private Long currentChatMemberId;       //현재 사용자의 chatRoomMemberId

        public ChatRoomResponseDto(ChatRoom chatRoom, Long memberId) {
                Long currentChatMemberId = chatRoom.getChatMembers().stream()
                        .filter(chatMember -> chatMember.getMember().getId().equals(memberId))
                        .map(chatMember -> chatMember.getId())
                        .findFirst()
                        .orElse(null);
                log.info("발견한 송신자의 chatroommemberId: {}", currentChatMemberId);

                this.id = chatRoom.getId();
                this.name = chatRoom.getName();
                this.roomStatus = chatRoom.getRoomStatus();
                this.chatMembers = chatRoom.getChatMembers()
                        .stream()
                        .map(chatRoomMember -> new ChatRoomMemberResponseDto(chatRoomMember))
                        .collect(Collectors.toList());
                this.chatMessages = chatRoom.getChatMessages()
                        .stream()
                        .map(chatMessage -> new ChatMessageResponseDto(chatMessage, memberId.equals(chatMessage.getSender().getId())))
                        .collect(Collectors.toList());
                this.currentChatMemberId = currentChatMemberId;
        }
}
