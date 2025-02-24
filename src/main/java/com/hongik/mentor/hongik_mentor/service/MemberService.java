package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.FollowRequestDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.MemberResDto;
import com.hongik.mentor.hongik_mentor.controller.dto.MemberAdminDto;
import com.hongik.mentor.hongik_mentor.controller.dto.MemberSaveDto;
import com.hongik.mentor.hongik_mentor.domain.Follow;
import com.hongik.mentor.hongik_mentor.domain.Badge;
import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.MemberType;

import com.hongik.mentor.hongik_mentor.domain.SocialProvider;
import com.hongik.mentor.hongik_mentor.exception.RegisterMemberException;
import com.hongik.mentor.hongik_mentor.exception.CustomMentorException;
import com.hongik.mentor.hongik_mentor.exception.ErrorCode;
import com.hongik.mentor.hongik_mentor.repository.FollowRepository;

import com.hongik.mentor.hongik_mentor.repository.BadgeRepository;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import com.hongik.mentor.hongik_mentor.service.dto.FollowStatusDto;
import com.univcert.api.UnivCert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
@Slf4j
@RequiredArgsConstructor @Transactional(readOnly = true)
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final BadgeRepository badgeRepository;

    private final FollowRepository followRepository;

    @Transactional
    public void setMainBadge(Long badgeId, Long memberId) {
        Badge findBadge = badgeRepository.findById(badgeId);
        Member findMember = memberRepository.findById(memberId).orElseThrow();

        findMember.setMainBadgeUrl(findBadge.getImageUrl());
    }

    /*재학생 인증*/
    //1. 메일로 인증코드  전송
    public boolean requestUnivEmailVerification(String userEmail){
        try {
            Map<String, Object> resp = UnivCert.certify("a718a7f1-5c62-4c2e-b007-bc8411926fe3", userEmail, "홍익대학교", true);
            return (Boolean) resp.get("success");
        } catch (IOException e) {
            log.warn("인증메일 발송 실패 - userEmail:{}", userEmail, e);
            throw new RuntimeException("대학교 인증메일 발송 과정에서 오류가 발생하였음", e);
        }

    }

    //2. 인증코드 검증
    public boolean confirmUnivEmailVerification(String userEmail, int verificationCode) {
        try {
            Map<String, Object> resp = UnivCert.certifyCode("a718a7f1-5c62-4c2e-b007-bc8411926fe3", userEmail, "홍익대학교", verificationCode);
            return (Boolean) resp.get("success");
        } catch (IOException e) {
            log.warn("인증코드 검증 시도 실패", e);
            throw new RuntimeException("인증코드를 검증하는 과정에서 오류가 발생하였습니다", e);
        }
    }


    /*crud*/
    //Create
    @Transactional
    public Long save(MemberSaveDto memberSaveDto) {
        //정합성 검증
        if (isDuplicatedMember(memberSaveDto)) {
            throw new RegisterMemberException(ErrorCode.DUPLICATE_MEMBER_REGISTER);
        }
        return memberRepository.save(memberSaveDto.toEntity()).getId();
    }

    private boolean isDuplicatedMember(MemberSaveDto memberSaveDto) {
        String socialId = memberSaveDto.getSocialId();
        SocialProvider socialProvider = memberSaveDto.getSocialProvider();
        Optional<Member> findMember = memberRepository.findBySocialId(socialId, socialProvider);
        if (findMember.isPresent()) {
            return true;
        }
        return false;
    }

    //Read
    public MemberResDto findById(Long id) {
        Member findMember = memberRepository.findById(id).orElseThrow();

        return new MemberResDto(findMember);
    }

    public List<MemberResDto> findAll() {

        List<MemberResDto> collect = memberRepository.findAll()
                .stream()
                .map(MemberResDto::new)
                .collect(Collectors.toList());

        return collect;
    }

    public Long getMemberIdOnllyForAdmin(String socialId, SocialProvider socialProvider) {
        Long memberid = memberRepository.findBySocialId(socialId, socialProvider).orElseThrow().getId();
        return memberid;
    }

    //Update

    @Transactional
    public Long update(Long id, String name, String major, Integer graduationYear, MemberType memberType) {
        Member findMember = memberRepository.findById(id).orElseThrow();
        return findMember.update(name, major, graduationYear, memberType);
    }

    //Delete
    @Transactional
    public void delete(Long memberId) {
        memberRepository.delete(memberId);
    }

    public Optional<MemberAdminDto> findBySocialId(String socialId, SocialProvider socialProvider) {
        try {
            MemberAdminDto dto = new MemberAdminDto(memberRepository.findBySocialId(socialId, socialProvider).get());
            return Optional.of(dto);
        } catch (NoSuchElementException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Long followMember(FollowRequestDTO followRequestDTO){ // followerId : 팔로우를 하려는 회원, followingId : 팔로우를 당하는 회원
        Member follower = memberRepository.findById(followRequestDTO.getFollowerId()).orElseThrow();

        Member followee = memberRepository.findById(followRequestDTO.getFolloweeId()).orElseThrow();

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

//        followRepository.deleteById(followId);
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
