package com.hongik.mentor.hongik_mentor.controller.swagger;

import com.hongik.mentor.hongik_mentor.controller.dto.FollowRequestDTO;
import com.hongik.mentor.hongik_mentor.service.dto.FollowStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "팔로우 API")
public interface FollowControllerDocs {
    @Operation(summary = "팔로우 신청", description = "follower -> followee 에게 팔로우를 신청")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(name = "Follow엔티티 식별자 값만 전달", type = "Long", example = "1")
            )
    )
    public ResponseEntity<?> followMember(@RequestBody FollowRequestDTO followRequestDTO);

    @Operation(summary = "팔로우 취소")
    @ApiResponse(
            responseCode = "204",
            description = "반환 컨텐츠 없음"
    )
    @DeleteMapping("/api/unfollow/{followId}")
    public ResponseEntity<?> unfollowMember(@PathVariable Long followId);

    @Operation(summary = "팔로잉 상태 조회", description = "팔로워 수는 몇인지, 팔로잉 수는 몇인지")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = FollowStatusDto.class)
            )
    )
    @GetMapping("/api/follow/{memberId}")
    public ResponseEntity<?> getFollowStatus(@PathVariable Long memberId);
}
