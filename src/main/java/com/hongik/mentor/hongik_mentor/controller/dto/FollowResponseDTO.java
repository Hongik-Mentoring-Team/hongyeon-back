package com.hongik.mentor.hongik_mentor.controller.dto;

import com.hongik.mentor.hongik_mentor.domain.Follow;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FollowResponseDTO {
    private Long id;
    private Long followerId;
    private Long followeeId;

    public static FollowResponseDTO from(Follow entity) {
        return new FollowResponseDTO(entity.getId(), entity.getFollower().getId(), entity.getFollowee().getId());
    }
}
