package com.hongik.mentor.hongik_mentor.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomMentorException extends RuntimeException {

    private ErrorCode errorCode;

    public CustomMentorException(Throwable cause, ErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }
}
