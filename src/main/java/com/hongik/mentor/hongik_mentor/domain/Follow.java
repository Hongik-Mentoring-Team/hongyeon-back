package com.hongik.mentor.hongik_mentor.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

    @Id @GeneratedValue
    @Column(name = "follow_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id")
    private Member follower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "followee_id")
    private Member followee;    //from근호: following은 헷갈려서 바꿔요 ㅠ

    @Builder
    public Follow(Member follower, Member followee) {
        this.follower = follower;
        this.followee = followee;
    }
}
