package com.hongik.mentor.hongik_mentor.controller;

import com.hongik.mentor.hongik_mentor.controller.dto.FollowRequestDTO;
import com.hongik.mentor.hongik_mentor.controller.swagger.FollowControllerDocs;
import com.hongik.mentor.hongik_mentor.service.FollowService;
import com.hongik.mentor.hongik_mentor.service.MemberService;
import com.hongik.mentor.hongik_mentor.service.dto.FollowStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FollowController implements FollowControllerDocs {

    private final FollowService followService;

    @PostMapping("/api/follow")
    public ResponseEntity<?> followMember(@RequestBody FollowRequestDTO followRequestDTO){
        log.trace("Follow request : {}", followRequestDTO);
        Long followId = followService.followMember(followRequestDTO);

        return ResponseEntity.ok(followId);

    }

    @DeleteMapping("/api/unfollow/{followId}")
    public ResponseEntity<?> unfollowMember(@PathVariable Long followId){

        log.trace("Unfollow request : {}", followId);
        followService.unfollowMember(followId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/follow/{memberId}")
    public ResponseEntity<?> getFollowStatus(@PathVariable Long memberId){
        FollowStatusDto followStatus = followService.getFollowStatus(memberId);

        return ResponseEntity.ok(followStatus);
    }


}
