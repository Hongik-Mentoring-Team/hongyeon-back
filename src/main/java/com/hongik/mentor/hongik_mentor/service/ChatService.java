package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.chat.*;
import com.hongik.mentor.hongik_mentor.domain.Applicant;
import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatMessage;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoom;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomMember;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.exception.ErrorCode;
import com.hongik.mentor.hongik_mentor.exception.InitiateChatException;
import com.hongik.mentor.hongik_mentor.repository.ChatRoomRepository;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import com.hongik.mentor.hongik_mentor.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/*
* Service for ChatRoom, ChatMessage
* */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    //ChatMessage CRUD
    //등록
    @Transactional
    public Long saveChatMessage(Long chatRoomId, ChatMessageReqDto messageDto, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        Member sender = memberRepository.findById(senderId).orElseThrow();
        ChatMessage entity = messageDto.toEntity(sender);

        chatRoom.addChatMessage(entity);
        chatRoomRepository.save(chatRoom);  //cascade로 chatMessage persist됨.
        return entity.getId();
    }
    //조회
    public List<ChatMessageResponseDto> findMessages(Long chatRoomId, Long requesterId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();

        return chatRoom.getChatMessages()
                .stream()
                .map(chatMessage -> new ChatMessageResponseDto(chatMessage, chatMessage.getSender().getId().equals(requesterId)))
                .collect(Collectors.toList());
    }

    //ChatRoom CRUD
    //등록
    @Transactional
    public Long createChatRoom(ChatRoomDto chatRoomDto, Long postId) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByPostId(postId);
        //이미 생성된 채팅방이 있다면 반환
        if (existingRoom.isPresent()) {
            return existingRoom.orElseThrow().getId();
        }

        //없다면 새로 생성
        Post findPost = postRepository.findById(postId).orElseThrow();  //중복 생성 방지 대안 2번: Post 인스턴스에 락
        try {
            ChatRoom savedChatRoom = chatRoomRepository.save(new ChatRoom(chatRoomDto.getName(), findPost));
            return savedChatRoom.getId();
        } catch (DataIntegrityViolationException e) {   //중복 생성 방지
            Optional<ChatRoom> creatdChatRoom = chatRoomRepository.findByPostId(postId);
            if (creatdChatRoom.isPresent()) {
                return creatdChatRoom.get().getId();
            } else {
                throw e;
            }

        }
    }

    //조회
    public ChatRoomResponseDto findChatRoom(Long chatRoomId, Long requesterId) {
        ChatRoom findChatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        
        //채팅방 참가자만 조회 가능
        boolean isParticipant = findChatRoom.getChatMembers().stream()
                .anyMatch(chatRoomMember -> chatRoomMember.getMember().getId().equals(requesterId));
        if(!isParticipant) throw new IllegalStateException("현재 채팅방에 속한 회원이 아닙니다");

        return new ChatRoomResponseDto(findChatRoom, requesterId);

    }
    public List<ChatRoomResponseDto> findChatRoomByMemberId(Long memberId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByMemberId(memberId);

        return chatRooms
                .stream()
                .map(chatRoom -> new ChatRoomResponseDto(chatRoom,memberId))
                .collect(Collectors.toList());
    }

    //수정
    //삭제
    @Transactional
    public Long deleteChatRoom(Long chatRoomId) {
        chatRoomRepository.deleteById(chatRoomId);
        return chatRoomId;
    }


    //ChatRoomMember CRUD
    //등록
    @Transactional
    public void saveChatRoomMembers(Long chatRoomId, Member mentor ,List<Applicant> mentees) {
        try {
            ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();

            //멘티
            mentees.forEach(applicant -> {
                ChatRoomMember chatRoomMember = new ChatRoomMember(applicant.getMember(), chatRoom, applicant.getNickname());
                chatRoom.addChatMember(chatRoomMember);
            });

            //멘토
            chatRoom.addChatMember(new ChatRoomMember(mentor,chatRoom,mentor.getName()));

            chatRoomRepository.save(chatRoom);//cascade로 ChatRoomMember persist
        } catch (NoSuchElementException e) {
            throw new InitiateChatException(ErrorCode.INITIATE_CHAT_IMPOSSIBLE);
        }

    }

    //초기 채팅방 생성
    @Transactional
    public Long initiateChat(ChatInitiateDto requestDto) {  // 성능 개선 가능: saveChatRoomMembers()를 제거하고 saveChatRoom()에서 모두 처리가 가능할듯
        String roomName = requestDto.getRoomName();

        Long roomId = this.createChatRoom(new ChatRoomDto(roomName),requestDto.getPostId());

        Post findPost = postRepository.findById(requestDto.getPostId()).orElseThrow();
        List<Applicant> applicants = findPost.getApplicants();
        log.info("방장: {}. ", findPost.getMember().getName());
        this.saveChatRoomMembers(roomId, findPost.getMember(), applicants);

        return roomId;
    }

    //채팅방 멤버인지 검증
    public boolean isInChatRoom(Long chatRoomId,Long requesterId) {
        ChatRoom findChatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow();
        return findChatRoom.getChatMembers().stream()
                .anyMatch(chatRoomMember -> chatRoomMember.getMember().getId().equals(requesterId));
    }

}
