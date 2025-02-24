package com.hongik.mentor.hongik_mentor.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchByTagDto {

    private List<Long> tagIds;

}
