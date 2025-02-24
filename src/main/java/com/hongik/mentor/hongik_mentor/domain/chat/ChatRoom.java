package com.hongik.mentor.hongik_mentor.domain.chat;

import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

@Getter @NoArgsConstructor
@Entity
public class ChatRoom {
    @Id @GeneratedValue
    @Column(name = "chatroom_id")
    private Long id;

    @OneToMany(mappedBy = "chatRoom"
            ,fetch = FetchType.EAGER
            , cascade = CascadeType.ALL, orphanRemoval = true) //ChatRoomMember 레포를 새로만들지 않고 cascade처리함
    @OrderColumn(name = "chatroommember_order") //성능 최적화
    private List<ChatRoomMember> chatMembers = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom"
            , fetch = FetchType.EAGER   //ChatMessage는 거의 항상 같이 조회하므로 Eager로딩
            , cascade = CascadeType.ALL, orphanRemoval = true) //ChatMessage 영속성 관리는 ChatRoom이 함
    @OrderColumn(name = "chatmessage_order")
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ChatRoomStatus roomStatus;

    private String name;    //채팅방 이름

    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id", unique = true, nullable = false)
    private Post post;

    public ChatRoom(String name, Post post) {
        this.name = name;
        this.roomStatus = ChatRoomStatus.OPEN;
        this.post = post;
        post.setChatRoom(this);
    }

    //양방향 연관관계 편의 메서드
    public void addChatMember(ChatRoomMember member) {
        chatMembers.add(member);
        member.setChatRoom(this);
    }
    public void addChatMessage(ChatMessage message) {
        chatMessages.add(message);
        message.setChatRoom(this);
    }

}
/** 성능 개선 가능
 * 상황: List<ChatRoomMember> List<ChatMessage>를 즉시로딩하므로 joinFetch가 일어나면서 Exception발생.
 *
 * 가능한 해결책1: @OrderColumn 사용 => 채택
 * 단: 순서 관리용 추가 칼럼, hibernate의 중복 데이터 처리 시간 증가 가능
 * ( 각 컬렉션의 순서를 저장하는 칼럼을 생성하므로, 데이터 무결성 관리가 필요 & 데이터 변경이 많으면 정합성 문제 발생할수도 )
 * ->  추가 문제점 발생: ChatMessage 하나를 insert할 때마다 chatmessage_order칼럼에  update 쿼리가 N개 나감 (성능 개선 가능)
 *
 * 가능한 해결책2: 지연로딩
 *  - 단: 추가 쿼리 발생
 *
 * 가능한 해결책3: 지연로딩+batch size
 *  단: 지연로딩 시점에 트랜잭션이 있어야함
 * */