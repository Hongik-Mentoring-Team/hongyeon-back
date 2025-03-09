package com.hongik.mentor.hongik_mentor.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Category {
    MENTOR("멘토 게시판"), MENTEE("멘티 게시판");
    private final String key;

}
