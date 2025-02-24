package com.hongik.mentor.hongik_mentor.domain.chat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChatRoomType {
    PUBLIC("완전 오픈 채팅"), PRIVATE("비공개 채팅");

    private final String key;
}
