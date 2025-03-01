package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.MemberSaveDto;
import com.hongik.mentor.hongik_mentor.domain.Badge;
import com.hongik.mentor.hongik_mentor.domain.member.Member;
import com.hongik.mentor.hongik_mentor.domain.member.MemberBadge;
import com.hongik.mentor.hongik_mentor.domain.member.SocialProvider;
import com.hongik.mentor.hongik_mentor.repository.BadgeRepository;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Transactional
@SpringBootTest
public class BadgeServiceTest {
    @Autowired
    BadgeService badgeService;
    @Autowired
    BadgeRepository badgeRepository;
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Test
    void findBySocialId테스트() {
        Badge badge1 = new Badge("badge1", "1234");
        Badge badge2 = new Badge("badge2", "432");

        Long id1 = badgeService.addBadge(badge1);
        Long id2 = badgeService.addBadge(badge2);

        Badge findBadge1 = badgeService.findOne(id1);
        Badge findBadge2 = badgeService.findOne(id2);

        Assertions.assertThat(findBadge1).isEqualTo(badge1);
        Assertions.assertThat(findBadge2).isEqualTo(badge2);
    }

    @Test
    void giveBadgeTest() {
        Badge badge1 = new Badge("badge1", "1234");
        Badge badge2 = new Badge("badge2", "432");

        Long id1 = badgeService.addBadge(badge1);
        Long id2 = badgeService.addBadge(badge2);

        Member member1 = new Member("asdf", SocialProvider.GOOGLE, "olaf", "computer", 2012);
        memberRepository.save(member1);

        badgeService.giveBadgeToMember(id1,member1.getId());
//        log.info("badges: {}", member1.getBadges());
        Assertions.assertThat(member1.getBadges().get(0).getBadge()).isEqualTo(badge1);
    }

    @Test
    public void addBadgeTest() {
        Badge badge1 = new Badge("badge1", "1234");
        Badge badge2 = new Badge("badge2", "432");
        Member member1 = new Member("asdf", SocialProvider.GOOGLE, "olaf", "computer", 2012);

        Long memberId = memberService.save(new MemberSaveDto(member1.getSocialId(), member1.getSocialProvider(), member1.getName(), member1.getMajor(), member1.getGraduationYear()));
        Member findMember = memberRepository.findById(memberId).orElseThrow();
        findMember.addBadge(new MemberBadge(findMember,badge1));

        Assertions.assertThat(findMember.getBadges().size()).isEqualTo(1);

    }
}
