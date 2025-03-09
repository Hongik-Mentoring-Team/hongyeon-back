package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatMessageReqDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatMessageResponseDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatRoomDto;
import com.hongik.mentor.hongik_mentor.controller.dto.chat.ChatRoomResponseDto;
import com.hongik.mentor.hongik_mentor.domain.Applicant;
import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.member.Member;
import com.hongik.mentor.hongik_mentor.domain.member.SocialProvider;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomType;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import com.hongik.mentor.hongik_mentor.repository.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Autowired
    PostRepository postRepository;

    @Test
    void 채팅방_생성_테스트() {
        /*given*/

        //Member생성
        Member member1 = new Member("1111", SocialProvider.GOOGLE, "박승범", "컴퓨터공학과", 2025);
        Member member2 = new Member("2222", SocialProvider.GOOGLE, "최근호", "컴퓨터공학과", 2025);
        Member member3 = new Member("3333", SocialProvider.GOOGLE, "전형진", "컴퓨터공학과", 2025);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //create Post
        Post post = Post.builder()
                .capacity(2)
                .category(Category.MENTOR)
                .chatRoomType(ChatRoomType.PRIVATE)
                .content("hello bro")
                .title("come to my mentoring")
                .member(memberRepository.findById(member1.getId()).orElseThrow())
                .build();

        postRepository.save(post);

        Applicant applicant1 = Applicant.builder()
                .member(member2)
                .post(post)
                .nickname("olaf")
                .build();

        Applicant applicant2 = Applicant.builder()
                .member(member3)
                .post(post)
                .nickname("밥김국")
                .build();

        /*when*/
        Long roomId = chatService.createChatRoom(new ChatRoomDto("roomA"),post.getId());
        log.info("chatroom id={}", roomId);


//        Map<Long, String> chatMembersInfo = new HashMap<>();
//        chatMembersInfo.put(member1.getId(), "olafN");
//        chatMembersInfo.put(member2Id, "trynN");

        chatService.saveChatRoomMembers(roomId, member1, List.of(applicant1, applicant2));


        //then
        ChatRoomResponseDto findChatRoom = chatService.findChatRoom(roomId, member1.getId());
        assertThat(findChatRoom.getName()).isEqualTo("roomA");
        assertThat(findChatRoom.getChatMembers()).hasSize(3)
                .extracting("nickname")
                .containsExactlyInAnyOrder("olaf", "밥김국", "박승범");

    }

    @Test
    void 메시지_저장_테스트() {

        //given

        //Member생성 저장
        Member member1 = new Member("1111", SocialProvider.GOOGLE, "박승범", "컴퓨터공학과", 2025);
        Member member2 = new Member("2222", SocialProvider.GOOGLE, "최근호", "컴퓨터공학과", 2025);
        Member member3 = new Member("3333", SocialProvider.GOOGLE, "전형진", "컴퓨터공학과", 2025);

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        //create Post
        Post post = Post.builder()
                .capacity(2)
                .category(Category.MENTOR)
                .chatRoomType(ChatRoomType.PRIVATE)
                .content("hello bro")
                .title("come to my mentoring")
                .member(memberRepository.findById(member1.getId()).orElseThrow())
                .build();

        postRepository.save(post);

        Applicant applicant1 = Applicant.builder()
                .member(member2)
                .post(post)
                .nickname("olaf")
                .build();

        Applicant applicant2 = Applicant.builder()
                .member(member3)
                .post(post)
                .nickname("밥김국")
                .build();

        Long roomId = chatService.createChatRoom(new ChatRoomDto("roomA"),post.getId());

        chatService.saveChatRoomMembers(roomId, member1, List.of(applicant1, applicant2));

        /*when*/
        String content = "나의 첫 메시지다!";
        chatService.saveChatMessage(roomId, new ChatMessageReqDto(roomId, applicant1.getNickname(),
                content, applicant1.getId()), member2.getId());

        //then
        List<ChatMessageResponseDto> messages = chatService.findMessages(roomId, member2.getId());
        assertThat(messages.get(0).getContent()).isEqualTo(content);
        log.info("message: {}", messages.get(0).getContent());

    }

}