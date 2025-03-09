//package com.hongik.mentor.hongik_mentor.repository;
//
//import com.hongik.mentor.hongik_mentor.domain.member.AccountStatus;
//import com.hongik.mentor.hongik_mentor.domain.member.Member;
//import com.hongik.mentor.hongik_mentor.domain.member.MemberType;
//import com.hongik.mentor.hongik_mentor.domain.member.SocialProvider;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@Transactional  //트랜잭션 롤백을 위해서
//@SpringBootTest
//class MemberRepositoryTest {
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @BeforeEach
//    public void setUp() {
//        Member member1 = new Member("1", SocialProvider.GOOGLE, "olaf", "CS", 2025, MemberType.GRADUATE, AccountStatus.ACTIVE);
//        Member member2 = new Member("2", SocialProvider.GOOGLE, "tryn", "CS", 2025, MemberType.GRADUATE, AccountStatus.ACTIVE);
//    }
//
//    @AfterEach
//    public void clean() {
//        memberRepository.deleteAll();
//    }
//    @Test
//    void create테스트() {
//        Member member1 = new Member("1", SocialProvider.GOOGLE, "olaf", "CS", 2025, MemberType.GRADUATE, AccountStatus.ACTIVE);
//        Member member2 = new Member("2", SocialProvider.GOOGLE, "tryn", "CS", 2025, MemberType.GRADUATE, AccountStatus.ACTIVE);
//
//        Long id1 = memberRepository.save(member1).getId();
//        Long id2 = memberRepository.save(member2).getId();
//
//        Member findMember1 = memberRepository.findById(id1);
//        Member findMember2 = memberRepository.findById(id2);
//
//        Assertions.assertThat(findMember1.getName()).isEqualTo(member1.getName());
//        Assertions.assertThat(findMember2.getName()).isEqualTo(member2.getName());
//
//    }
//
//    @Test
//    void delete테스트() {
//
//        //given
//        Member member1 = new Member("1", SocialProvider.GOOGLE, "olaf", "CS", 2025, MemberType.GRADUATE, AccountStatus.ACTIVE);
//        Member member2 = new Member("2", SocialProvider.GOOGLE, "tryn", "CS", 2025, MemberType.GRADUATE, AccountStatus.ACTIVE);
//
//        Long id1 = memberRepository.save(member1).getId();
//        Long id2 = memberRepository.save(member2).getId();
//
//        //when
//        memberRepository.deleteAll();
//
//        //then
//        List<Member> members = memberRepository.findAll();
//        Assertions.assertThat(members).isEmpty();
//    }
//}