package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.domain.Badge;
import com.hongik.mentor.hongik_mentor.domain.member.Member;
import com.hongik.mentor.hongik_mentor.domain.member.MemberBadge;
import com.hongik.mentor.hongik_mentor.repository.BadgeRepository;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class BadgeService {
    private final BadgeRepository badgeRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void giveBadgeToMember(Long badgeId, Long memberId) {
        Badge findBadge = badgeRepository.findById(badgeId);
        Member findMember = memberRepository.findById(memberId).orElseThrow();

        MemberBadge memberBadge = new MemberBadge(findMember, findBadge);
        badgeRepository.saveMemberBadge(memberBadge);

        findMember.addBadge(memberBadge);
    }

    /*crud*/
    @Transactional
    public Long addBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    public Badge findOne(Long id) {
        return badgeRepository.findById(id);
    }

    public List<Badge> findAll() {
        return badgeRepository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        badgeRepository.delete(id);
    }

    public Long count() {
        return badgeRepository.count();
    }
}

