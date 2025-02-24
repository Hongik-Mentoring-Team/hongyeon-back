package com.hongik.mentor.hongik_mentor.controller.dto.comment;

import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.post.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
public class CommentResDto {

    private Long commentId;

    private String comment;

    private Long postId;

    private Long memberId;

    private LocalDateTime createdAt;

    private boolean isOwner;

    //댓글 작성자가 요청자와 같은지 정보 제공 O(같다면 댓글 삭제가 가능)
    public CommentResDto(Comment comment, boolean isOwner) {
        this.commentId = comment.getId();
        this.comment = comment.getContent();
        this.postId =comment.getPost().getId();
        this.memberId = comment.getMember().getId();
        this.createdAt = comment.getCreatedAt();
        this.isOwner = isOwner;
    }

    //댓글 작성자가 요청자와 같은지 정보 제공 X(같다면 댓글 삭제가 가능)
    public CommentResDto(Comment comment) {
        this.commentId = comment.getId();
        this.comment = comment.getContent();
        this.postId =comment.getPost().getId();
        this.memberId = comment.getMember().getId();
        this.createdAt = comment.getCreatedAt();
    }
}
