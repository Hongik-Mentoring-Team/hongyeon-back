package com.hongik.mentor.hongik_mentor.controller.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class CommentModifyDto {

    private String comment;

    private Long postId;
}
