package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.MemberSaveDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatMessageReqDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatMessageResponseDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatRoomDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatRoomResponseDto;
import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.SocialProvider;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomType;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
@Slf4j
@ActiveProfiles("test")
@Transactional
@SpringBootTest
class ChatServiceTest {
    @Autowired
    ChatService chatService;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Test
    void 채팅방_생성_테스트() {
        /*given*/

        //Member생성
        Long member1Id = memberService.save(new MemberSaveDto("1", SocialProvider.GOOGLE, "olaf", "CS", 2025));
        Long member2Id = memberService.save(new MemberSaveDto("2", SocialProvider.GOOGLE, "tryn", "EC", 2025));

        //create Post
        Post post = Post.builder()
                .capacity(2)
                .category(Category.MENTOR)
                .chatRoomType(ChatRoomType.PRIVATE)
                .content("hello bro")
                .title("come to my mentoring")
                .member(memberRepository.findById(member1Id).orElseThrow())
                .build();

        /*when*/
        Long roomId = chatService.createChatRoom(new ChatRoomDto("roomA"),post.getId());
        log.info("chatroom id={}", roomId);

        Map<Long, String> chatMembersInfo = new HashMap<>();
        chatMembersInfo.put(member1Id, "olafN");
        chatMembersInfo.put(member2Id, "trynN");

        chatService.saveChatRoomMembers(roomId, chatMembersInfo);


        //then
        ChatRoomResponseDto findChatRoom = chatService.findChatRoom(roomId);
        assertThat(findChatRoom.getName()).isEqualTo("roomA");
        assertThat(findChatRoom.getChatMembers()).hasSize(2);

    }

    @Test
    void 메시지_저장_테스트() {
        //Member생성 저장
        Long member1Id = memberService.save(new MemberSaveDto("1", SocialProvider.GOOGLE, "olaf", "CS", 2025));
        Long member2Id = memberService.save(new MemberSaveDto("2", SocialProvider.GOOGLE, "tryn", "EC", 2025));
        //Chatroom 생성 저장
        Long roomId = chatService.createChatRoom(new ChatRoomDto("roomA"));
        //ChatMember 생성 저장
        Map<Long, String> chatMembersInfo = new HashMap<>();
        String nickname1 = "olafN";
        chatMembersInfo.put(member1Id, nickname1);
        String nickname2 = "trynN";
        chatMembersInfo.put(member2Id, nickname2);
        chatService.saveChatRoomMembers(roomId, chatMembersInfo);

        /*when*/
        String content = "나의 첫 메시지다!";
        chatService.saveChatMessage(roomId, new ChatMessageReqDto(roomId, nickname1, member1Id, content));

        //then
        List<ChatMessageResponseDto> messages = chatService.findMessages(roomId);
        assertThat(messages.get(0).getContent()).isEqualTo(content);
        log.info("message: {}", messages.get(0).getContent());

    }

}