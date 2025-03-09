package com.hongik.mentor.hongik_mentor.controller.comment;

import com.hongik.mentor.hongik_mentor.controller.dto.ApiResponseEntity;
import com.hongik.mentor.hongik_mentor.controller.dto.CommentCreateDto;
import com.hongik.mentor.hongik_mentor.controller.dto.CreatedCommentDto;
import com.hongik.mentor.hongik_mentor.controller.dto.comment.CommentModifyDto;
import com.hongik.mentor.hongik_mentor.controller.swagger.CommentControllerDocs;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.exception.CustomMentorException;
import com.hongik.mentor.hongik_mentor.exception.ErrorCode;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import com.hongik.mentor.hongik_mentor.repository.PostRepository;
import com.hongik.mentor.hongik_mentor.service.CommentService;
import com.hongik.mentor.hongik_mentor.service.dto.CommentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Tag(name = "댓글과 관련된 API", description = "댓글 생성/수정/삭제를 수행하는 API")
public class CommentController{


    private final CommentService commentService;

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Operation(summary = "게시글에 댓글을 작성하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201")
    })
    @PostMapping
    public ResponseEntity<ApiResponseEntity<CreatedCommentDto>> createComment(@RequestBody CommentCreateDto request) {
        return ResponseEntity.status(201).body(ApiResponseEntity
                .of(HttpStatus.CREATED, "댓글 생성 성공", commentService.createComment(request)));
    }

    // 특정 게시글의 모든 댓글 조회
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getAllComments(@PathVariable Long postId) {
        // 게시글 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomMentorException(ErrorCode.POST_NOT_EXISTS));

        // 댓글 조회
        List<CommentDto> commentDtos = post.getComments().stream()
                .map(comment -> CommentDto.fromEntity(comment.getMember().getName(), comment))
                .collect(Collectors.toList());

        return ResponseEntity.ok(commentDtos);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Long> modifyComment(@PathVariable Long commentId, @RequestBody String newContent) {
        // 댓글 수정 서비스 호출
        Long updatedCommentId = commentService.modifyComment(commentId, newContent);

        return ResponseEntity.ok(updatedCommentId);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        // 댓글 삭제 서비스 호출
        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }
}
