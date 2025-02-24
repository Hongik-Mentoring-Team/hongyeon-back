package com.hongik.mentor.hongik_mentor.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //리뷰 내용
    @Column(nullable = false)
    private String content;

    //리뷰 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", referencedColumnName = "member_id", nullable = false)
    private Member writer;

    //누구에 대한 리뷰인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", referencedColumnName = "member_id",nullable = false)
    private Member target;

    // 평점 (1~5)
    @Column(nullable = false)
    private int rating;

    //작성일
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //수정일
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // 작성자 리뷰 목록에 추가
    public void setWriter(Member writer) {
        this.writer = writer;
        if (!writer.getWrittenReviews().contains(this)) {
            writer.getWrittenReviews().add(this);
        }
    }

    // 대상자 리뷰 목록에 추가
    public void setTarget(Member target) {
        this.target = target;
        if (!target.getReceivedReviews().contains(this)) {
            target.getReceivedReviews().add(this);
        }
    }

    public Review() {
    }

    public Review(String content, Member writer, Member target, int rating) {
        this.content = content;
        this.writer = writer;
        this.target = target;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        setWriter(writer);
        setTarget(target);
    }

    public Long update(String content, int rating){
        this.content = content;
        this.rating = rating;
        this.updatedAt = LocalDateTime.now();

        return id;
    }
}
