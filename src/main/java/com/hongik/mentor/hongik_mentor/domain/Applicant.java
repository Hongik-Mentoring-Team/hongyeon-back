package com.hongik.mentor.hongik_mentor.domain;

import com.hongik.mentor.hongik_mentor.domain.post.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor @NoArgsConstructor
@Entity
public class Applicant {
    @Id @GeneratedValue
    private Long id;

    // 게시글
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "post_id")
    private Post post;

    // 지원자
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "member_id")
    private Member member;

    private String nickname;
}
