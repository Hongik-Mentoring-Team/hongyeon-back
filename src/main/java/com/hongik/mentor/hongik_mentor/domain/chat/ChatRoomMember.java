package com.hongik.mentor.hongik_mentor.domain.chat;

import com.hongik.mentor.hongik_mentor.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
@Entity
public class ChatRoomMember {
    @Id @GeneratedValue
    @Column(name = "chatroom_member_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "chatroom_id", nullable = false)  //양방향, 영속성 관리는 ChatRoom이 함
    private ChatRoom chatRoom;
    private String nickname;

    public ChatRoomMember(Member member, ChatRoom chatRoom, String nickname) {
        this.member = member;
        this.nickname = nickname;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
