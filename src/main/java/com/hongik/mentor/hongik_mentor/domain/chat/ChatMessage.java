package com.hongik.mentor.hongik_mentor.domain.chat;

import com.hongik.mentor.hongik_mentor.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @NoArgsConstructor
@Entity
public class ChatMessage {
    @Id @GeneratedValue
    @Column(name = "chat_message_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "chatroom_id", nullable = false)
    private ChatRoom chatRoom;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id", nullable = false)
    private Member sender;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;

    public ChatMessage(Member sender, String nickname, String content, LocalDateTime createdAt) {
        this.nickname = nickname;
        this.content = content;
        this.createdAt = createdAt;
        this.sender = sender;
    }

    // TODO: EnableJpaAuditing 적용
    @PrePersist //em.flush직전 콜백
    public void prePersist() {
        createdAt = (createdAt == null ? LocalDateTime.now() : createdAt);
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
