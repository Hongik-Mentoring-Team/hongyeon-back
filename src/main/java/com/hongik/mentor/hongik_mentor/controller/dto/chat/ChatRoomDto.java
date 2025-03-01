package com.hongik.mentor.hongik_mentor.controller.dto.chat;

import lombok.Getter;

@Getter
public class ChatRoomDto {

    private String name;    //채팅방 이름

    public ChatRoomDto(String name) {
        this.name = name;
    }

}
