package com.hongik.mentor.hongik_mentor.controller.comment;

import com.hongik.mentor.hongik_mentor.controller.dto.ApiResponseEntity;
import com.hongik.mentor.hongik_mentor.controller.dto.CommentCreateDto;
import com.hongik.mentor.hongik_mentor.controller.dto.CreatedCommentDto;
import com.hongik.mentor.hongik_mentor.controller.swagger.CommentControllerDocs;
import com.hongik.mentor.hongik_mentor.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "댓글과 관련된 API", description = "댓글 생성/수정/삭제를 수행하는 API")
public class CommentController implements CommentControllerDocs{


    private final CommentService commentService;

    @Operation(summary = "게시글에 댓글을 작성하는 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201")
    })
    @PostMapping("/api/comments")
    public ResponseEntity<ApiResponseEntity<CreatedCommentDto>> createComment(@RequestBody CommentCreateDto request) {
        return ResponseEntity.status(201).body(ApiResponseEntity
                .of(HttpStatus.CREATED, "댓글 생성 성공", commentService.createComment(request)));
    }

}
