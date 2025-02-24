package com.hongik.mentor.hongik_mentor.controller.dto;


import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PostCreateDTO {

    private String title;

    private String content;

    private Category category;

    private List<Long> tagIds;

    private int capacity; //모집인원

    private ChatRoomType chatRoomType; //채팅방 타입(공개/비공개)

//    private Long memberId; => 게시글 생성시 접속 세션에서 Id조회

    @Builder
    public PostCreateDTO(String title, String content, List<Long> tagIds, Category category, int capacity, ChatRoomType chatRoomType) {
        this.title = title;
        this.content = content;
        this.tagIds = tagIds;
        this.category = category;
        this.capacity = capacity;
        this.chatRoomType = chatRoomType;
//        this.memberId=this.memberId; 게시글 생성시 접속 세션에서 Id조회
    }


}
