package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.FollowRequestDTO;
import com.hongik.mentor.hongik_mentor.domain.Follow;
import com.hongik.mentor.hongik_mentor.domain.member.Member;
import com.hongik.mentor.hongik_mentor.exception.CustomMentorException;
import com.hongik.mentor.hongik_mentor.exception.ErrorCode;
import com.hongik.mentor.hongik_mentor.repository.FollowRepository;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import com.hongik.mentor.hongik_mentor.service.dto.FollowStatusDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long followMember(FollowRequestDTO followRequestDTO){ // followerId : 팔로우를 하려는 회원, followingId : 팔로우를 당하는 회원

        Member follower = memberRepository.findById(followRequestDTO.getFollowerId())
                .orElseThrow();

        Member followee = memberRepository.findById(followRequestDTO.getFolloweeId())
                .orElseThrow();

        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();

        follower.addFollower(follow);

        followee.addFollowing(follow);


        followRepository.save(follow);

        return follow.getId();
    }

    @Transactional
    public void unfollowMember(Long followId){

        Follow follow = followRepository.findById(followId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.FOLLOW_RELATIONSHIP_DOES_NOT_EXIST));

        followRepository.delete(follow);

    }

    public FollowStatusDto getFollowStatus(Long memberId){

        int numOfFollowers = followRepository.countByFollowerId(memberId);

        int numOfFollowings = followRepository.countByFolloweeId(memberId);

        return FollowStatusDto.builder()
                .memberId(memberId)
                .followers(numOfFollowers)
                .followings(numOfFollowings)
                .build();

    }
}
