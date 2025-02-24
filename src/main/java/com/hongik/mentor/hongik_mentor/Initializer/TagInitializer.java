package com.hongik.mentor.hongik_mentor.Initializer;

import com.hongik.mentor.hongik_mentor.domain.post.Tag;
import com.hongik.mentor.hongik_mentor.repository.TagRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@Component    //main 어플리케이션 클래스에서 수동 빈 등록
@Slf4j
@RequiredArgsConstructor
public class TagInitializer {

    private final TagRepository tagRepository;

    @PostConstruct
    public void initTags() {
        List<String> tagNames = new ArrayList<>(List.of(
                "개발전체",
                "프론트엔드 개발자",
                "백엔드 개발자",
                "금융권",
                "대기업 SI",
                "안드로이드 개발자",
                "보안 엔지니어",
                "데이터 사이언티스트",
                "안드로이드 개발자"));

        List<Tag> findTags = tagRepository.findAll();
        List<String> findNames = findTags.stream()
                .map(tag -> String.valueOf(tag.getName()))
                .toList();

        /*태그 목록 동기화*/

        //1. 태그 추가

        List<Tag> tagsToAdd = tagNames.stream()
                .filter(name -> !findNames.contains(name))
                .map(name -> Tag.builder().name(name).build())
                .collect(Collectors.toList());
        tagsToAdd.forEach(tag -> log.info("추가된 태그 반영: {}",tag.getName()));
        if (!tagsToAdd.isEmpty()) {
            tagRepository.saveAll(tagsToAdd);
        }

        //2. 태그 삭제
        List<Tag> tagsToRemove = findTags.stream()
                .filter(tag -> !tagNames.contains(tag.getName()))
                .collect(Collectors.toList());
        tagsToRemove.forEach(tag -> log.info("삭제된 태그 반영:{}", tag.getName()));
        if (!tagsToRemove.isEmpty()) {
            tagRepository.deleteAll(tagsToRemove);
        }
    }
}
