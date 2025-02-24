package com.hongik.mentor.hongik_mentor.controller.dto;

import com.hongik.mentor.hongik_mentor.domain.*;
import com.hongik.mentor.hongik_mentor.domain.tier.Tier;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/** 관리자용 DTO
 * Member PK를 포함함: HTTP 응답으로 보내지 않는다
 * */
@Getter
public class MemberResDto {

    private String socialId;    //socialId+provider를 조합하여 유저를 구분함
    private SocialProvider socialProvider;
    private String name;
    private String major;
    private Integer graduationYear;
    private MemberType type;    //재학생/졸업생
    private AccountStatus accountStatus;
    private Role role;
    private List<MemberBadgeResponseDto> badges = new ArrayList<>();
    private String mainBadgeUrl;
    private String imageUrl;
    private Tier tier;
    private Long rank_value;

    private List<PostDTO> posts = new ArrayList<>();
    private Set<Long> followings = new HashSet<>();
    private Set<Long> followers = new HashSet<>();
    private List<ReviewResponseDto> writtedReviews = new ArrayList<>();
    private List<ReviewResponseDto> receivedReviews = new ArrayList<>();


    public MemberResDto(Member member) {
        this.socialId = member.getSocialId();
        this.socialProvider = member.getSocialProvider();
        this.name=member.getName();
        this.major = member.getMajor();
        this.graduationYear=member.getGraduationYear();
        this.type=member.getType();
        this.accountStatus=member.getAccountStatus();
        this.role = member.getRole();
        member.getBadges()
                .forEach(badge -> {
                    MemberBadgeResponseDto dto = new MemberBadgeResponseDto(badge);
                    badges.add(dto);
                });
        this.mainBadgeUrl = member.getMainBadgeUrl();
        this.tier = member.getTier();
        this.rank_value = member.getRank_value();
        this.imageUrl = member.getImageUrl();

        member.getPosts()
                .forEach(post -> {
                    PostDTO postDTO = PostDTO.fromPost(post);
                    posts.add(postDTO);
                });
        member.getFollowers()
                .forEach(follower -> {
                    Long followerId = follower.getFollower().getId();
                    followers.add(followerId);
                });
        member.getFollowings()
                .forEach(following->{
                    Long followeeId = following.getFollowee().getId();
                    followings.add(followeeId);
                });
        member.getWrittenReviews()
                .stream()
                .map(ReviewResponseDto::fromEntity)
                .forEach(dto->writtedReviews.add(dto));
        member.getReceivedReviews()
                .stream()
                .map(ReviewResponseDto::fromEntity)
                .forEach(dto->receivedReviews.add(dto));

    }

}
