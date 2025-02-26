package com.hongik.mentor.hongik_mentor.controller;

import com.hongik.mentor.hongik_mentor.controller.dto.FollowRequestDTO;
import com.hongik.mentor.hongik_mentor.controller.swagger.FollowControllerDocs;
import com.hongik.mentor.hongik_mentor.service.MemberService;
import com.hongik.mentor.hongik_mentor.service.dto.FollowStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class FollowController implements FollowControllerDocs {

    private final MemberService memberService;

    @PostMapping("/api/follow")
    public ResponseEntity<?> followMember(@RequestBody FollowRequestDTO followRequestDTO){
        Long followId = memberService.followMember(followRequestDTO);

        return ResponseEntity.ok(followId);

    }

    @DeleteMapping("/api/unfollow/{followId}")
    public ResponseEntity<?> unfollowMember(@PathVariable Long followId){
        memberService.unfollowMember(followId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/follow/{memberId}")
    public ResponseEntity<?> getFollowStatus(@PathVariable Long memberId){
        FollowStatusDto followStatus = memberService.getFollowStatus(memberId);

        return ResponseEntity.ok(followStatus);
    }


}
