package com.hongik.mentor.hongik_mentor.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    POST_NOT_EXISTS(HttpStatus.NOT_FOUND, "해당하는 게시글이 존재하지 않습니다"),

    INITIATE_CHAT_IMPOSSIBLE(HttpStatus.BAD_REQUEST,"채팅방 개설에 실패하였습니다"),

    DUPLICATE_MEMBER_REGISTER(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다"),

    WEB_SOCKET_AUTHENTICATION_NOT_EXISTS(HttpStatus.BAD_REQUEST, "웹소켓 세션에 인증정보가 NULL입니다"),

    FOLLOW_RELATIONSHIP_DOES_NOT_EXIST(HttpStatus.NOT_FOUND, "해당유저를 팔로우했던 결과가 존재하지 않습니다."),

    MEMBER_NOT_EXISTS(HttpStatus.NOT_FOUND, "해당하는 유저가 존재하지 않습니다."),

    COMMENT_NOT_EXISTS(HttpStatus.NOT_FOUND, "해당하는 댓글이 존재하지 않습니다."),

    NOT_REGISTERED_USER(HttpStatus.BAD_REQUEST,"가입되어 있지 않은 회원입니다."),

    DUPLICATE_MENTORING_APPLY(HttpStatus.BAD_REQUEST,"이미 지원된 게시글 입니다."),

    FAILED_TO_APPLY(HttpStatus.BAD_REQUEST, "지원 과정에서 오류가 발생했습니다"),

    CAPACITY_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "모집인원이 기신청자 수를 충족시키지 못합니다.");


    private final HttpStatus httpStatus;

    private final String message;
}
