package com.hongik.mentor.hongik_mentor.domain.member;


import com.hongik.mentor.hongik_mentor.domain.*;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.domain.tier.Tier;
import com.hongik.mentor.hongik_mentor.domain.tier.TierAssigner;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//회원 엔티티
/*고려사항
*
* 1. 로그인 전략
* - only 소셜로그인 VS 소셜로그인+자체로그인
* 비교:
*       상황: 유저 '홍길동'이 카카오로 회원가입 후 네이버로도 같은 계정취급을 받으며 로그인하고 싶음.
*       이때, 소셜로그인만을 사용한다면 적절히 처리가 불가능함. 반면 자체로그인을 도입한다면 개인의 주민번호를 사용하여 유저 식별이 가능하기 때문에 하나의 통합된 계정으로 생성 가능함.
* 결정:
*       자체로그인 시 필요한 보안수준을 아직 모르기에 소셜로그인만으로 진행.
*
* 2. socialId x provider VS email
* 필요성: 사용자가 처음 소셜로그인 하여 회원가입하는 경우와 그 외의 경우를 구분.
*          소셜로그인을 통해 회원가입한 회원들 사이의 구분에 사용되는 기준임.
* 이때, 유저를 식별하기위해서 socialId와 email중 하나를 택할 수 있음.
* 유연성: socialId가 더 높음.
*       왜냐하면, 소셜공급자마다 제공하는 api가 다르고 email을 제공하지 않을 가능성도 존재함.
* 결정:
*       socialId x provider 사용
*
* 3. 권한 설계
* 의문점: 권한을 어디까지 나눠야할 필요성이 있을지 아직 모르겠음.
*
* */

@Getter
@Entity
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)     //참고(sequence, table전략은 JPA에서 ID를 미리 할당받기에 쿼리를 지연 가능, 반면 identity는 즉시 쿼리 발생)
    @Column(name = "member_id")
    private Long id;    //DB용 PK

    @Column(nullable = false)
    private String socialId;    //~userNameAttributeName              socialId+provider를 조합하여 유저를 구분함

    @Column(nullable = false) @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;  //~registrationId

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String major;

    @Column(nullable = false)
    private Integer graduationYear;

    @Column(nullable = false) @Enumerated(EnumType.STRING)
    private MemberType type;    //재학생/졸업생

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "followee", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> followings = new HashSet<>();

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Follow> followers = new HashSet<>();

    @Column(nullable = false) @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberBadge> badges = new ArrayList<>();

    private String mainBadgeUrl;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Tier tier;
    private Long rank_value;

    private AccountStatus accountStatus; //생성시 자동주입

    // 내가 작성한 리뷰
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> writtenReviews = new ArrayList<>();

    // 나에 대한 리뷰
    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> receivedReviews = new ArrayList<>();

    public Member() {
    }

    //Member 객체 생성이 발생하는 경우: 회원가입. but 사용자가 회원가입 화면을 보기 전에 db에 불완전Member를 넣어야하는 경우가 많음. -> 이 생성자가 필요할까? => DB에 불완전한 Member는 안넣으면됨
    //USER(정규) member생성
    public Member(String socialId, SocialProvider socialProvider, String name, String major, Integer graduationYear) {
        this.socialId = socialId;
        this.socialProvider = socialProvider;
        this.name = name;
        this.major = major;
        this.graduationYear = graduationYear;
        /*if (graduationYear <= LocalDate.now().getYear()) {
            this.type = MemberType.GRADUATE;
        } else {
            this.type=MemberType.STUDENT;
        }*/
        this.mainBadgeUrl = "";
        this.imageUrl = "";
        this.type = MemberType.TEMP;
        this.accountStatus = AccountStatus.ACTIVE;
        this.role = Role.USER;
        this.tier = TierAssigner.evaluate(0L);
        this.rank_value = 0L;
    }

    //TEMP(임시) member생성
    public Member(String socialId, SocialProvider socialProvider) {
        this.socialId = socialId;
        this.socialProvider = socialProvider;
        this.role=Role.TEMP;
    }

    public Long update(String name, String major, Integer graduationYear, MemberType memberType) {
        this.name=name;
        this.major=major;
        this.graduationYear=graduationYear;
        this.type = memberType;

        return id;
    }


    // 평균 평점 계산 메서드
    public double getAverageRating() {
        if (receivedReviews.isEmpty()) {
            return 0.0;
        }
        return receivedReviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public void addBadge(MemberBadge memberBadge) {
        this.badges.add(memberBadge);
    }

    public void setMainBadgeUrl(String url) {
        this.mainBadgeUrl=url;
    }

    public void addFollower(Follow follower) {
        this.followers.add(follower);
    }

    public void addFollowing(Follow following) {
        this.followings.add(following);
    }
}
