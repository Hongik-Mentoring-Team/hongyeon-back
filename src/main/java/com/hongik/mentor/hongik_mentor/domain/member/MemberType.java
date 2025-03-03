package com.hongik.mentor.hongik_mentor.domain.member;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberType {
    TEMP("재학생 인증 필요"),STUDENT("재학생"), GRADUATE("졸업생");

    private final String title;
}
