package com.hongik.mentor.hongik_mentor.controller.swagger;

import com.hongik.mentor.hongik_mentor.controller.dto.MemberRegisterDto;
import com.hongik.mentor.hongik_mentor.controller.dto.MemberResDto;
import com.hongik.mentor.hongik_mentor.controller.dto.MemberSaveDto;
import com.hongik.mentor.hongik_mentor.oauth.LoginMember;
import com.hongik.mentor.hongik_mentor.oauth.dto.SessionMember;
import com.hongik.mentor.hongik_mentor.oauth.util.SessionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Tag(name = "회원 API")
public interface MemberControllerDocs {

    @Operation(summary = "회원가입", description = "소셜로그인에 성공한 사용자가 추가정보를 입력한 후에 해당api를 통해 회원가입을 한다")
    @ApiResponse(responseCode = "200" ,
            description = "성공",
            content = @Content(
                    mediaType = "application/json" ,
                    schema = @Schema(name = "key없이 value만 전달", type = "Long", example = "1")
            )
    )
    public Long createMember(@RequestBody MemberRegisterDto memberRegisterDto,
                             @LoginMember SessionMember sessionMember,
                             HttpSession httpSession);

    @Operation(summary = "모든 회원 조회", description = "모든 회원을 조회하는 기능이므로 !사용에 주의!")
    @ApiResponse(responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = MemberResDto.class))
            )
    )
    public List<MemberResDto> findMembers();

    @Operation(summary = "회원 단건 조회" , description = "회원 식별자를 파라미터로 단건 조회")
    @ApiResponse(responseCode = "200" ,
            description = "성공",
            content = @Content(
                    mediaType = "application/json" ,
                    schema = @Schema(implementation = MemberResDto.class)
            )
    )
    public MemberResDto findMember(@PathVariable Long id);

    @Operation(summary = "회원 프로필 조회" , description = "현재 로그인된 사용자의 프로필을 조회" )
    @ApiResponse(responseCode = "200" ,
            description = "성공",
            content = @Content(
                    mediaType = "application/json" ,
                    schema = @Schema(implementation = MemberResDto.class)
            )
    )
    public MemberResDto getMemberProfile(HttpSession httpSession);

    @Operation(summary = "로그인 중인 사용자 이름 조회" , description = "로그인 여부를 판별 가능하다")
    @ApiResponses({
                    @ApiResponse(
                        responseCode = "200",
                        description = "성공",
                        content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(name = "name", type = "String", example = "최근호")
                    )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "이름조회 실패, 로그아웃 상태입니다",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(name = "값만 전달", type = "String", example = "현재 로그인된 세션이 없습니다")
                            )
                    )
    })
    public ResponseEntity<?> getSessionMemberInfo(@LoginMember SessionMember sessionMember, HttpSession httpSession);

    @Operation(summary = "회원 탈퇴")
    @ApiResponse(responseCode = "200",
            description = "성공 및 컨텐츠를 반환하지 않습니다"
    )
    public void deleteMember(@LoginMember SessionMember sessionMember);



}
