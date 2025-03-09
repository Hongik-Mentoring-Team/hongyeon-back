package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.PostDTO;
import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.SocialProvider;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import com.hongik.mentor.hongik_mentor.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
@DisplayName("PostService 검색 기능 테스트")
class PostServiceSearchTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember;
    private Post testPost1;
    private Post testPost2;

    @BeforeEach
    void setUp() {
        // 테스트 멤버 저장
        Member testMember = new Member("asdf", SocialProvider.GOOGLE, "olaf", "computer", 2012);
        memberRepository.save(testMember);

        // 테스트 게시글 저장
        testPost1 = postRepository.save(Post.builder()
                .title("Spring Boot 게시글")
                .content("Spring Boot와 JPA를 활용한 프로젝트")
                .member(testMember)
                .category(Category.MENTOR)
                .build());

        testPost2 = postRepository.save(Post.builder()
                .title("JPA 게시글")
                .content("QueryDSL과 함께 JPA 활용하기")
                .member(testMember)
                .category(Category.MENTEE)
                .build());
    }

    @Test
    @DisplayName("제목으로 게시글 검색")
    void searchByTitle() {
        // when
        List<PostDTO> result = postService.searchByTitle("Spring");

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).contains("Spring");
    }

    @Test
    @DisplayName("내용으로 게시글 검색")
    void searchByContent() {
        // when
        List<PostDTO> result = postService.searchByContent("QueryDSL");

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).contains("QueryDSL");
    }

    @Test
    @DisplayName("작성자로 게시글 검색")
    void searchByPoster() {
        // when
        List<PostDTO> result = postService.searchByMember("olaf");

        // then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAuthor()).isEqualTo("olaf");
    }
}
