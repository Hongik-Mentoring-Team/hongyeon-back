package com.hongik.mentor.hongik_mentor.controller.dto;

import com.hongik.mentor.hongik_mentor.controller.dto.comment.CommentResDto;
import com.hongik.mentor.hongik_mentor.domain.Applicant;
import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomMember;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomType;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.service.dto.PostLikeDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private String author;  //작성자 이름

    private Long postId;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private List<TagDTO> tags;

    private Category category;

    private List<CommentResDto> comments;

    private int likes;

    private int capacity;

    private ChatRoomType chatRoomType;

    private int currentApplicants;

    private boolean isClosed;

    private boolean isOwner;    //생성자에 따라 제공 or 미제공

    private Long chatRoomId;

    //    private List<Applicant> applicants = new ArrayList<>();

    //1. Post작성자 본인인지 검증 정보 제공 O
    public static PostDTO fromPost(Post post, boolean isOwner, List<CommentResDto> commentResDtos) {
        return PostDTO.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .tags(post.getTags().stream()
                        .map(postTag -> TagDTO.fromTag(postTag.getTag())).toList())
                .category(post.getCategory())
                .author(post.getMember().getName())
                .comments(commentResDtos)
                .likes(post.getLikes().size())
                .capacity(post.getCapacity())
                .chatRoomType(post.getChatRoomType())
                .currentApplicants(post.getCurrentApplicants())
                .isClosed(post.isClosed())
                .isOwner(isOwner)
                .chatRoomId(post.getChatRoom()==null?-1:post.getChatRoom().getId())
                .build();
    }

    //2. Post작성자 본인인지 검증 정보 제공 X
    public static PostDTO fromPost(Post post) {
        return PostDTO.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .tags(post.getTags().stream()
                        .map(postTag -> TagDTO.fromTag(postTag.getTag())).toList())
                .category(post.getCategory())
                .author(post.getMember().getName())
                .comments(post.getComments()
                        .stream()
                        .map(comment -> new CommentResDto(comment))
                        .toList())
                .likes(post.getLikes().size())
                .capacity(post.getCapacity())
                .chatRoomType(post.getChatRoomType())
                .currentApplicants(post.getCurrentApplicants())
                .isClosed(post.isClosed())
                .chatRoomId(post.getChatRoom()==null?-1:post.getChatRoom().getId())
                .build();


    }

}
