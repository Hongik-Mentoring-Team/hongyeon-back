package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.PostCreateDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.PostDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.PostModifyDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.comment.CommentModifyDto;
import com.hongik.mentor.hongik_mentor.controller.dto.comment.CommentReqDto;
import com.hongik.mentor.hongik_mentor.controller.dto.comment.CommentResDto;
import com.hongik.mentor.hongik_mentor.domain.*;
import com.hongik.mentor.hongik_mentor.domain.post.*;
import com.hongik.mentor.hongik_mentor.exception.CustomMentorException;
import com.hongik.mentor.hongik_mentor.exception.ErrorCode;
import com.hongik.mentor.hongik_mentor.oauth.util.SessionUtil;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import com.hongik.mentor.hongik_mentor.repository.PostRepository;
import com.hongik.mentor.hongik_mentor.repository.TagRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    private final MemberRepository memberRepository;

    private final TagRepository tagRepository;

    @Transactional
    public Long createPost(PostCreateDTO postCreateDTO, Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new CustomMentorException(ErrorCode.MEMBER_NOT_EXISTS));

        Post post = Post.builder()
                .member(member)
                .title(postCreateDTO.getTitle())
                .content(postCreateDTO.getContent())
                .category(postCreateDTO.getCategory())
                .capacity(postCreateDTO.getCategory() == Category.MENTOR ? postCreateDTO.getCapacity() : 1)
                .build();

        postCreateDTO.getTagIds()
                .forEach(id -> {
                    Tag tag = tagRepository.findById(id).orElseThrow(() -> new RuntimeException("Tag not found"));
                    PostTag postTag = PostTag.of(tag, post);
                    post.addTags(postTag);
                });

        postRepository.save(post);

        return post.getId();
    }

    public PostDTO getPost(Long postId, Long requesterId){    //추후 함수 getPost함수 이름을 별도로 빼서 순수하게 post조회하는 함수 생성해야할듯

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.POST_NOT_EXISTS));

        List<CommentResDto> commentResDtos = post.getComments().stream()
                .map(comment -> {
                    boolean isCommentOwner = requesterId.equals(comment.getMember().getId());
                    return new CommentResDto(comment, isCommentOwner);
                }).toList();

        boolean isOwner = requesterId.equals(post.getMember().getId());

        return PostDTO.fromPost(post,isOwner,commentResDtos);
    }

    @Transactional
    public Long modifyPost(Long postId, PostModifyDTO postModifyDTO) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.POST_NOT_EXISTS));

        List<Tag> tags = postModifyDTO.getTagIds().stream()
                .map(id -> tagRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Tag not found"))).toList();

        List<PostTag> postTags = tags.stream().map(tag -> PostTag.builder()
                .post(post)
                .tag(tag)
                .build()).toList();

        post.clearTags();

        post.modifyPost(postModifyDTO.getTitle(), postModifyDTO.getContent(), postTags, postModifyDTO.getCapacity());

        postRepository.save(post);

        return post.getId();

    }

    @Transactional
    public Long deletePost(Long postId, Long requesterId){
        Post findPost = postRepository.findById(postId).orElseThrow();
        if (isEntityOwner(requesterId, findPost.getMember().getId())) {
            postRepository.deleteById(postId);
        } else {
            throw new RuntimeException("해당 게시글의 소유자가 아닙니다");
        }

        return postId;
    }




    public List<PostDTO> searchPostsByTags(Category category, List<Long> tagIds) {
        List<Post> posts=new ArrayList<>();

        if (category == null) {
            posts = postRepository.searchByTags(tagIds);
        } else if (tagIds == null) {
            posts = postRepository.searchByCategory(category);
        } else {
            posts = postRepository.searchByTagsAndCategory(tagIds, category);
        }

        if(posts.isEmpty()) return List.of();

        return posts.stream()
                .map(PostDTO::fromPost)
                .toList();

    }


    @Transactional
    public Long thumbUp(Long postId, Long memberId) { // 좋아요 기능
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Member member = memberRepository.findById(memberId).orElseThrow();

        addLikesToPost(member, post);

        postRepository.save(post);

        return post.getId();

    }

    private void addLikesToPost(Member member, Post post) {
        PostLike postLike = PostLike.builder()
                .member(member)
                .post(post)
                .build();

        post.addLikes(postLike);
    }

    // 모집 지원 기능
    @Transactional
    public void applyToPost(Long postId, Long memberId, String nickname) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.POST_NOT_EXISTS));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.MEMBER_NOT_EXISTS));

        try {
            post.addApplicant(member, nickname);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("다시 시도해 주세요.");
        }

        postRepository.save(post);
    }

    // 모집 상태 초기화 기능
    @Transactional
    public void resetPostApplicants(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.POST_NOT_EXISTS));

        post.resetApplicants();
        postRepository.save(post);
    }

    // 모집 상태 확인 기능
    public boolean isPostClosed(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.POST_NOT_EXISTS));
        return post.isClosed();
    }

    // 신청 취소 기능
    @Transactional
    public void cancelApplication(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.POST_NOT_EXISTS));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.MEMBER_NOT_EXISTS));

        post.cancelApplicant(member);
        postRepository.save(post);
    }

    /** Tag
     *
     * */
    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    /**
     * Comment
     */
    @Transactional
    public void createComment(CommentReqDto dto, Long memberId) {
        Post findPost = postRepository.findById(dto.getPostId()).orElseThrow(() -> new IllegalStateException("댓글을 작성하신 게시글이 존재하지 않습니다"));
        Member findMember = memberRepository.findById(memberId).orElseThrow();

        findPost.getComments().add(Comment.builder()
                .post(findPost)
                .member(findMember)
                .content(dto.getComment())
                .build());

        postRepository.save(findPost);
    }

    @Transactional
    public void modifyComment(Long postId, Long commentId, CommentModifyDto dto, HttpSession httpSession) {

        //미완. 추가 작업 필요

        /*Post findPost = postRepository.findById(postId).orElseThrow();

        if(isEntityOwner(SessionUtil.getCurrentMemberId(httpSession)),)*/
    }

    /**
     * Utility
     * */
    //요청자가 해당 엔티티의 주인인지 검증
    private static boolean isEntityOwner(Long requesterId, Long entityOwnerId) {
        return requesterId.equals(entityOwnerId);
    }
}
