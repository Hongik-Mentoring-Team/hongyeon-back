package com.hongik.mentor.hongik_mentor.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.processing.Generated;

@NoArgsConstructor @Getter
@Entity
public class MemberBadge {
    @Id @GeneratedValue
    @Column(name = "memberbadge_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "badge_id")
    private Badge badge;

    @Builder
    public MemberBadge(Member member, Badge badge) {
        this.member = member;
        this.badge = badge;
    }

    public static MemberBadge of(Member member, Badge badge) {
        return MemberBadge.builder()
                .member(member)
                .badge(badge)
                .build();
    }
}
