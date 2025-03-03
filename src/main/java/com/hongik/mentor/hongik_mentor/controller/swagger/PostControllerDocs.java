package com.hongik.mentor.hongik_mentor.controller.swagger;

import com.hongik.mentor.hongik_mentor.controller.dto.PostCreateDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.PostDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.PostModifyDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.comment.CommentModifyDto;
import com.hongik.mentor.hongik_mentor.controller.dto.comment.CommentReqDto;
import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.post.Tag;
import com.hongik.mentor.hongik_mentor.oauth.util.SessionUtil;
import com.hongik.mentor.hongik_mentor.service.dto.PostLikeDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.PostModifyDTO;
import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.service.dto.PostLikeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@io.swagger.v3.oas.annotations.tags.Tag(name = "게시글 API", description = "게시글과 관련된 API")
public interface PostControllerDocs {

    @Operation(summary = "게시글 생성", description = "사용자가 게시글을 생성합니다.")
    @ApiResponse(
            responseCode = "201",
            description = "게시글 생성 성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(name = "postId", type = "Long", example = "1")
            )
    )
    ResponseEntity<?> createPost(@RequestBody PostCreateDTO postCreateDTO, HttpSession httpSession);

    @Operation(summary = "게시글 단건 조회", description = "게시글의 세부정보를 조회합니다")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = PostDTO.class)
            )
    )
    public ResponseEntity<?> getPost(@PathVariable Long postId, HttpSession httpSession);

    @Operation(summary = "게시글 수정", description = "게시글의 일부를 수정합니다")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(name = "modifiedPost", type = "Long", example = "1")
            )
    )
    public ResponseEntity<?> modifyPost(@PathVariable Long postId, @RequestBody PostModifyDTO postModifyDTO);

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다")
    @ApiResponse(
            responseCode = "204",
            description = "성공하여 아무런 컨텐츠를 반환하지 않음"
    )
    public ResponseEntity<?> deletePost(@PathVariable Long postId, HttpSession httpSession);

    @Operation(summary = "태그기반 게시글 조회", description = "태그를 검색조건으로 조회합니다")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = PostDTO.class))
            )
    )
    public ResponseEntity<?> searchPostByTags(@RequestParam(required = false) Category category, @RequestParam(required = false) List<Long> tagIds);

    @Operation(summary = "게시글 좋아요 추가", description = "해당 게시글에 좋아요를 추가합니다")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(name = "likedPostId", type = "Long", example = "1")
            )
    )
    public ResponseEntity<?> thumbUpPost(@PathVariable Long postId, @RequestBody PostLikeDTO postLikeDTO);


    /**
     * Tag API
     */
    @Operation(summary = "모든 태그 조회")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Tag.class))
            )
    )
    public ResponseEntity<?> getTags();

    /**
     * Comment API
     */
    @Operation(summary = "댓글 생성", description = "댓글을 추가합니다")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(name = "없음(값만 전달)", type = "String", example = "댓글을 달았습니다")
            )
    )
    public ResponseEntity<?> createComment(@PathVariable Long postId,
                                           @RequestBody CommentReqDto dto,
                                           HttpSession httpSession);

    @Operation(summary = "댓글 수정")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(name = "없음(값만 전달)", type = "String", example = "댓글을 수정하였습니다")
            )
    )
    public ResponseEntity<?> modifyComment(@PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           @RequestBody CommentModifyDto dto,
                                           HttpSession httpSession);

    /**
     * Applicant API
     */
    @Operation(summary = "지원하기", description = "멘토 게시글의 멘토링에 지원하기")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(name = "없음(값만 전달)", type = "String", example = "지원에 성공했습니다!")
            )
    )
    public ResponseEntity<?> createApplicant(@PathVariable Long postId,
                                             @RequestParam String nickname,
                                             HttpSession httpSession);

}
