package com.hongik.mentor.hongik_mentor.controller.dto;

import com.hongik.mentor.hongik_mentor.domain.member.Member;
import com.hongik.mentor.hongik_mentor.domain.Review;
import lombok.Getter;

@Getter
public class ReviewSaveDto {
    private String content;
    private Long writerId;
    private Long targetId;
    private int rating;

    public ReviewSaveDto() {
    }

    public ReviewSaveDto(String content, Long writerId, Long targetId, int rating) {
        this.content = content;
        this.writerId = writerId;
        this.targetId = targetId;
        this.rating = rating;
    }

    public Review toEntity(String content, Member writer, Member target, int rating) {
        return new Review(content, writer, target, rating);
    }
}
