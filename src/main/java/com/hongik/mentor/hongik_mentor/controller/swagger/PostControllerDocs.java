package com.hongik.mentor.hongik_mentor.controller.swagger;

import com.hongik.mentor.hongik_mentor.controller.dto.PostCreateDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.PostDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "게시글 API", description = "게시글과 관련된 API")
public interface PostControllerDocs {

    @Operation(summary = "게시글 생성", description = "사용자가 게시글을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "게시글 생성 성공",
    content = @Content(mediaType = "application/json", schema = @Schema()))
    ResponseEntity<?> createPost(@RequestBody PostCreateDTO postCreateDTO, HttpSession httpSession);




}
