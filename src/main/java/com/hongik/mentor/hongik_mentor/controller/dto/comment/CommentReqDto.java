package com.hongik.mentor.hongik_mentor.controller.dto.comment;

import com.hongik.mentor.hongik_mentor.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Data
public class CommentReqDto {

    private String comment;

    private Long postId;
}
