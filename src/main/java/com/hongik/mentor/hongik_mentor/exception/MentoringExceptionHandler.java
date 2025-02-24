package com.hongik.mentor.hongik_mentor.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/* Global Exception Handler
* 책임 분리: 컨트롤러와 서비스에 예외 처리 로직을 최소화
일관성: 모든 에러 응답이 동일한 구조로 반환되어 클라이언트가 쉽게 파싱하고 처리.
유지보수성: 예외 처리 로직을 @ControllerAdvice에서 관리해 변경이 쉽고, 중복 코드 제거.
* */
@RestControllerAdvice
public class MentoringExceptionHandler {

    @ExceptionHandler(CustomMentorException.class)
    public ResponseEntity<ErrorResponseEntity> handleCustomMentorException(CustomMentorException e) {
        return ErrorResponseEntity.errorResponseEntity(e.getErrorCode());
    }
    @ExceptionHandler(InitiateChatException.class)
    public ResponseEntity<ErrorResponseEntity> handleInitiateChatException(InitiateChatException e) {
        return ErrorResponseEntity.errorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(RegisterMemberException.class)
    public ResponseEntity<ErrorResponseEntity> handleRegisterMemberException(RegisterMemberException e) {
        return ErrorResponseEntity.errorResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(SendMessageException.class)
    public ResponseEntity<ErrorResponseEntity> handleSendMessageException(SendMessageException e) {
        return ErrorResponseEntity.errorResponseEntity(e.getErrorCode());
    }

}
