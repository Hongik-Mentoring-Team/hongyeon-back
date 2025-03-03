package com.hongik.mentor.hongik_mentor.controller.dto;

import com.hongik.mentor.hongik_mentor.domain.member.Member;
import com.hongik.mentor.hongik_mentor.domain.member.SocialProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class MemberSaveDto {
    //socialId+provider를 조합하여 유저를 구분함
    private final String socialId;                  //From SessionMember
    private final SocialProvider socialProvider;    //From SessionMember
    private final String name;              //From client
    private final String major;             //From client
    private final Integer graduationYear;   //From client


    public Member toEntity() {
        return new Member(socialId, socialProvider, name, major, graduationYear);
    }
}
