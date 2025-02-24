package com.hongik.mentor.hongik_mentor.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    ACTIVE("활성"), DEACTIVE("비활성"), BANNED("정지"), TEMP("회원 가입 중");
    private final String key;
}
