package com.hongik.mentor.hongik_mentor.controller.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@Data
public class ChatInitiateDto {
    private String roomName;    //채팅방 이름

    private Long postId;

}

//json 형식:
  /*
{
  "roomName": "Study Group",
  "postId" : 123
}

* */
