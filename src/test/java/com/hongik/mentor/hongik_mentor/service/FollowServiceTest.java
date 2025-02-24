package com.hongik.mentor.hongik_mentor.service;


import com.hongik.mentor.hongik_mentor.controller.dto.FollowRequestDTO;
import com.hongik.mentor.hongik_mentor.domain.Follow;
import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.SocialProvider;
import com.hongik.mentor.hongik_mentor.exception.CustomMentorException;
import com.hongik.mentor.hongik_mentor.exception.ErrorCode;
import com.hongik.mentor.hongik_mentor.repository.FollowRepository;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class FollowServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FollowRepository followRepository;

    @BeforeEach
    void setUp() {
        followRepository.deleteAllInBatch();
        memberRepository.deleteAll();
    }


    @Test
    @DisplayName("회원 팔로우에 성공한다.")
    @Transactional
    public void follow_member_success() {

        //given
        Member member1 = new Member("1111", SocialProvider.GOOGLE, "박승범", "컴퓨터공학과", 2025);

        Member member2 = new Member("2222", SocialProvider.GOOGLE, "최근호", "컴퓨터공학과", 2025);

        memberRepository.save(member1);
        memberRepository.save(member2);

        FollowRequestDTO request = FollowRequestDTO.builder()
                .followerId(member1.getId())
                .followeeId(member2.getId())
                .build();

        //when
        Long followId = memberService.followMember(request);

        //then
        assertThat(member1.getFollowers()).hasSize(1);
        assertThat(member2.getFollowings()).hasSize(1);


    }

    @DisplayName("언팔로우 시 팔로우 내역은 삭제된다.")
    @Test
    @Transactional
    void unfollowMember(){

        //given
        Member member1 = new Member("1111", SocialProvider.GOOGLE, "박승범", "컴퓨터공학과", 2025);

        Member member2 = new Member("2222", SocialProvider.GOOGLE, "최근호", "컴퓨터공학과", 2025);

        memberRepository.save(member1);
        memberRepository.save(member2);

        Follow follow = Follow.builder()
                .follower(member1)
                .following(member2)
                .build();

        Follow savedFollow = followRepository.save(follow);

        //when
        memberService.unfollowMember(savedFollow.getId());

        //then
        assertThatThrownBy(() -> followRepository.findById(follow.getId())
                .orElseThrow(() -> new CustomMentorException(ErrorCode.FOLLOW_RELATIONSHIP_DOES_NOT_EXIST)))
                .isInstanceOf(CustomMentorException.class);
//                .hasMessage("해당유저를 팔로우했던 결과가 존재하지 않습니다.");
     }

     @DisplayName("한 사람이 팔로우하고 있는 내역을 모두 조회한다. 즉 팔로잉 내역 조회")
     @Test
     @Transactional
     void test(){

         //given
         Member member1 = new Member("1111", SocialProvider.GOOGLE, "박승범", "컴퓨터공학과", 2025);

         Member member2 = new Member("2222", SocialProvider.GOOGLE, "최근호", "컴퓨터공학과", 2025);

         Member member3 = new Member("3333", SocialProvider.GOOGLE, "전형진", "컴퓨터공학과", 2025);

         memberRepository.save(member1);
         memberRepository.save(member2);
         memberRepository.save(member3);

         Follow follow1 = createFollow(member1, member2);

         Follow follow2 = createFollow(member1, member3);

         followRepository.saveAll(List.of(follow1, follow2));

         //when
         int numOfFollowing = followRepository.countByFollowerId(member1.getId());

         List<Follow> followings = followRepository.findByFollowerId(member1.getId());

         //then
         assertThat(numOfFollowing).isEqualTo(2);
         assertThat(followings).hasSize(2)
                 .extracting("following.id", "follower.id")
                 .containsExactlyInAnyOrder(
                         tuple(member2.getId(), member1.getId()),
                         tuple(member3.getId(), member1.getId())
                 );

     }


     @DisplayName("한 사람이 팔로우 하고 있는 사람의 수와 팔로우하고 있는 내역들을 모두 조회한다. 즉, 팔로잉 내역 조회 ")
     @Test
     @Transactional
     void getFollowingStatus(){

         //given
         Member member1 = new Member("1111", SocialProvider.GOOGLE, "박승범", "컴퓨터공학과", 2025);

         Member member2 = new Member("2222", SocialProvider.GOOGLE, "최근호", "컴퓨터공학과", 2025);

         Member member3 = new Member("3333", SocialProvider.GOOGLE, "전형진", "컴퓨터공학과", 2025);

         memberRepository.save(member1);
         memberRepository.save(member2);
         memberRepository.save(member3);

        Follow follow1 = createFollow(member2, member1);
        Follow follow2 = createFollow(member3, member1);

        followRepository.saveAll(List.of(follow1, follow2));


        //when
        int numOfFollowers = followRepository.countByFolloweeId(member1.getId());

        List<Follow> followers = followRepository.findByFolloweeId(member1.getId());

        //then
        assertThat(numOfFollowers).isEqualTo(2);
        assertThat(followers).hasSize(2)
                .extracting("follower.id", "following.id")
                .containsExactlyInAnyOrder(
                        tuple(member2.getId(), member1.getId()),
                        tuple(member3.getId(), member1.getId())
                );

     }

    private Follow createFollow(Member follower, Member followee) {
        Follow follow = Follow.builder()
                .follower(follower)
                .following(followee)
                .build();
        return follow;
    }


}