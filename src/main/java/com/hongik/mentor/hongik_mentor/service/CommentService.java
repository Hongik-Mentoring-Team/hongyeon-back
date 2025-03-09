package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.CommentCreateDto;
import com.hongik.mentor.hongik_mentor.controller.dto.CreatedCommentDto;
import com.hongik.mentor.hongik_mentor.domain.member.Member;
import com.hongik.mentor.hongik_mentor.domain.post.Comment;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.exception.CustomMentorException;
import com.hongik.mentor.hongik_mentor.exception.ErrorCode;
import com.hongik.mentor.hongik_mentor.repository.CommentRepository;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import com.hongik.mentor.hongik_mentor.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    @Transactional
    public CreatedCommentDto createComment(CommentCreateDto request) {

        Member member = memberRepository.findById(request.getMemberId()).get();

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new CustomMentorException(ErrorCode.POST_NOT_EXISTS));

        Comment comment = Comment.of(request, member, post);

        commentRepository.save(comment);



        return new CreatedCommentDto(comment.getId());
    }

    @Transactional
    public Long modifyComment(Long commentId, String newContent) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.COMMENT_NOT_EXISTS));

        comment.modifyContent(newContent);

        return comment.getId();
    }


    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.COMMENT_NOT_EXISTS));

        commentRepository.delete(comment);
    }
}
