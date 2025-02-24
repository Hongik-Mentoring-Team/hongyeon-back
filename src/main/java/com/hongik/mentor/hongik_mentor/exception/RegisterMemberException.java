package com.hongik.mentor.hongik_mentor.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterMemberException extends RuntimeException {
    private final ErrorCode errorCode;
}
