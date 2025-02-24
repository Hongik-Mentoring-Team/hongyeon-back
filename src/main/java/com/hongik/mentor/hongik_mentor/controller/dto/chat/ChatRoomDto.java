package com.hongik.mentor.hongik_mentor.controller.dto.chat;

import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoom;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomMember;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomStatus;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ChatRoomDto {

    private String name;    //채팅방 이름

    public ChatRoomDto(String name) {
        this.name = name;
    }

}
