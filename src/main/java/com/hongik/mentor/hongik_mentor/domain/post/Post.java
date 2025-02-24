package com.hongik.mentor.hongik_mentor.domain.post;

import com.hongik.mentor.hongik_mentor.controller.dto.PostCreateDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.PostModifyDTO;
import com.hongik.mentor.hongik_mentor.domain.Applicant;
import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoom;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomType;
import com.hongik.mentor.hongik_mentor.exception.CustomMentorException;
import com.hongik.mentor.hongik_mentor.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String content;

    //게시판   ex)멘토 게시판/멘티 게시판
    @Enumerated(EnumType.STRING)
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostTag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PostLike> likes = new ArrayList<>();

    //모집인원
    @Column(nullable = false)
    private int capacity;   //멘티 게시글: -1

    //해당 게시글로부터 생성될 채팅방 타입
    @Enumerated(EnumType.STRING)
    private ChatRoomType chatRoomType;

    //개설된 채팅방
    @OneToOne(mappedBy = "post")
    private ChatRoom chatRoom;

    //현재 신청자 수
    @Builder.Default
    private int currentApplicants = 0;

    //신청자 멤버 리스트
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true) //Post가 영속성 관리
    @Builder.Default
    private List<Applicant> applicants = new ArrayList<>();

    //모집 마감 여부
    @Builder.Default
    private boolean isClosed = false;

    //Optimistic Locking
    @Version
    private Long version;

    public void addTags(PostTag tag){
        this.tags.add(tag);
    }

    public void addLikes(PostLike like){
        this.likes.add(like);
    }

    public void addComment(Comment comment){
        this.comments.add(comment);
    }

    public void clearTags(){
        this.tags.clear();
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void modifyPost(String title, String content, List<PostTag> postTags, int capacity) {
        this.title = title;
        this.content = content;
        this.tags.addAll(postTags);
        if (currentApplicants > capacity) {
            throw new CustomMentorException(ErrorCode.CAPACITY_NOT_ENOUGH);
        }
    }
/* => Applicant엔티티 도입

    //신청자 멤버 리스트
    @ManyToMany
    @JoinTable(
            name = "post_applicants",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private List<Member> applicants = new ArrayList<>();
*/

/* Applicant 편의 메서드*/
    //신청자 추가
    public void addApplicant(Member member,String nickname) {
        try {
            if (isClosed) {
                throw new RuntimeException("모집 마감된 게시글입니다");
            }

            List<Long> appliedMembersId = this.applicants
                    .stream()
                    .map(applicant -> applicant.getMember().getId())
                    .toList();
            if (appliedMembersId.contains(member.getId())) {    //기신청된 회원

                throw new CustomMentorException(ErrorCode.DUPLICATE_MENTORING_APPLY);
            }

            this.currentApplicants++;
            this.applicants.add(Applicant.builder()
                    .post(this)
                    .member(member)
                    .nickname(nickname)
                    .build());

            if (this.currentApplicants >= this.capacity) {
                this.isClosed = true;
            }
        } catch (CustomMentorException e1) {
            throw e1;
        } catch (RuntimeException e2) {
            throw new CustomMentorException(e2, ErrorCode.FAILED_TO_APPLY);
        }

    }

    //신청 취소
    public void cancelApplicant(Member member) {
        List<Long> appliedMembersId = this.applicants.stream()
                .map(applicant -> applicant.getMember().getId())
                .toList();
        if (!appliedMembersId.contains(member.getId())) {
            throw new IllegalStateException("신청한 적이 없는 회원입니다.");
        }

        this.currentApplicants--;
        Applicant hopeToCancel=new Applicant();
        for (Applicant applicant : applicants)
            if(applicant.getMember().getId()==member.getId()) hopeToCancel = applicant;
        this.applicants.remove(hopeToCancel);

        if (capacity>currentApplicants) {
            this.isClosed = false;
        }
    }

    //모집 상태 초기화
    public void resetApplicants() {
        this.currentApplicants = 0;
        this.isClosed = false;
        this.applicants.clear();
    }
}
