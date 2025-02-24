package com.hongik.mentor.hongik_mentor.controller.dto;

import com.hongik.mentor.hongik_mentor.domain.MemberBadge;
import lombok.Getter;

@Getter
public class MemberBadgeResponseDto {
    private Long badgeId;
    private String badgeName;
    private String badgeImageUrl;

    public MemberBadgeResponseDto(MemberBadge  memberBadge) {
        this.badgeId = memberBadge.getId();
        badgeName = memberBadge.getBadge().getName();
        badgeImageUrl = memberBadge.getBadge().getName();
    }
}
