package com.hongik.mentor.hongik_mentor.controller.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

//MemberSaveDto에 포함됨
@RequiredArgsConstructor
@Data
public class MemberRegisterDto {
    private final String name;              //From client
    private final String major;             //From client
    private final Integer graduationYear;   //From client
}
