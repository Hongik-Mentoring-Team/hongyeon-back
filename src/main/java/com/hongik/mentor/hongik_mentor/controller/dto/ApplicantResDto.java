package com.hongik.mentor.hongik_mentor.controller.dto;

import lombok.Data;

@Data
public class ApplicantResDto {

    private Long id;

    // 게시글
    private Long postId;

    // 지원자
    private Long memberId;
}
