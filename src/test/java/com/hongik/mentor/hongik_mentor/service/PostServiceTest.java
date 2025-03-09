package com.hongik.mentor.hongik_mentor.service;

import com.hongik.mentor.hongik_mentor.controller.dto.PostCreateDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.PostDTO;
import com.hongik.mentor.hongik_mentor.controller.dto.PostModifyDTO;
import com.hongik.mentor.hongik_mentor.domain.Category;
import com.hongik.mentor.hongik_mentor.domain.member.Member;
import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomType;
import com.hongik.mentor.hongik_mentor.domain.post.Comment;
import com.hongik.mentor.hongik_mentor.domain.post.Post;
import com.hongik.mentor.hongik_mentor.domain.member.SocialProvider;
import com.hongik.mentor.hongik_mentor.exception.CustomMentorException;
import com.hongik.mentor.hongik_mentor.repository.CommentRepository;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import com.hongik.mentor.hongik_mentor.repository.PostRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;


    @DisplayName("게시글 생성을 성공한다.")
    @Test
    void createPost(){

        //given
        Member member = new Member("socialId", SocialProvider.KAKAO, "박승범", "컴퓨터공학과", 2025);

        memberRepository.save(member);

        PostCreateDTO request = createPostRequest(Category.MENTEE, "제목1", "내용1", List.of(1L, 2L));

        //when
        Long createdPostId = postService.createPost(request, member.getId());

        //then
        assertThat(createdPostId).isNotNull();

    }

    @DisplayName("게시글의 식별자로, 게시글을 조회한다.")
    @Test
    void getPost(){

        //given
        Member member = new Member("socialId", SocialProvider.KAKAO, "박승범", "컴퓨터공학과", 2025);

        memberRepository.save(member);

        Post post = Post.builder()
                .title("게시글1")
                .content("내용1")
                .member(member)
                .build();


        postRepository.save(post);

        //when
        PostDTO postDto = postService.getPost(post.getId(), member.getId());

        //then
        assertThat(postDto)
                .extracting("title", "content")
                .containsExactly("게시글1", "내용1");

    }

    @DisplayName("게시글 조회 시 댓글까지 모두 조회한다.")
    @Test
    void getPostWithAllComments(){

        //given
        Member member = new Member("socialId", SocialProvider.KAKAO, "박승범", "컴퓨터공학과", 2025);

        memberRepository.save(member);

        Post post = Post.builder()
                .title("게시글1")
                .content("내용1")
                .member(member)
                .build();


        postRepository.save(post);

        Comment comment1 = Comment.builder()
                .content("댓글1")
                .member(member)
                .post(post)
                .build();

        Comment comment2 = Comment.builder()
                .content("댓글2")
                .member(member)
                .post(post)
                .build();

        post.addComment(comment1);
        post.addComment(comment2);

        commentRepository.saveAll(List.of(comment1, comment2));



        //when
        PostDTO postWithComments
                = postService.getPost(post.getId(), member.getId());


        //then
        assertThat(postWithComments.getComments()).hasSize(2)
                .extracting("comment")
                .containsExactly("댓글1", "댓글2");


    }
    
    @DisplayName("제시된 태그들을 모두 포함하는 게시글을 모두 조회한다.")
    @Test
    void searchPostsByTags(){
        
        //given
        Member member = new Member("socialId", SocialProvider.KAKAO, "박승범", "컴퓨터공학과", 2025);

        memberRepository.save(member);

        PostCreateDTO request1 = createPostForTest("게시글 1", "내용 1", member.getId(), List.of(1L, 2L));

        PostCreateDTO request2 = createPostForTest("게시글 2", "내용 1", member.getId(), List.of(2L, 3L));

        PostCreateDTO request3 = createPostForTest("게시글 3", "내용 1", member.getId(), List.of(1L, 3L));

        postService.createPost(request1, member.getId());
        postService.createPost(request2, member.getId());
        postService.createPost(request3, member.getId());
        
        //when
        List<PostDTO> postDTOS = postService.searchPostsByTags(null, List.of(1L));

        //then
        assertThat(postDTOS).hasSize(2)
                .extracting("title", "content")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("게시글 1", "내용 1"),
                        Tuple.tuple("게시글 3", "내용 1")
                );


        
    }

    private PostCreateDTO createPostForTest(String title, String content, Long memberId, List<Long> tagIds) {
        return PostCreateDTO.builder()
                .title(title)
                .content(content)
                .tagIds(tagIds)
                .build();
    }

    @DisplayName("게시글을 수정한다. 수정가능한 부분은 제목과 내용, 태그종류이다.")
    @Test
    void modifyPost(){

        //given
        Member member = new Member("socialId", SocialProvider.KAKAO, "박승범", "컴퓨터공학과", 2025);

        memberRepository.save(member);

        Post post = Post.builder()
                .title("게시글1")
                .content("내용1")
                .member(member)
                .build();


        postRepository.save(post);

        PostModifyDTO postModifyDTO = new PostModifyDTO("새로운 게시글", "새로운 내용", List.of(2L), 8);


        //when
        Long modifiedPostId = postService.modifyPost(post.getId(), postModifyDTO);

        Post modifiedPost = postRepository.findById(modifiedPostId).get();

        //then
        assertThat(modifiedPostId).isNotNull();
        assertThat(modifiedPost)
                .extracting("title", "content")
                .containsExactly("새로운 게시글", "새로운 내용");


    }

    @DisplayName("게시글에 좋아요를 누른다. 좋아요 수는 1씩 증가한다.")
    @Test
    void thumbUp(){

        //given
        Member member = new Member("socialId", SocialProvider.KAKAO, "박승범", "컴퓨터공학과", 2025);

        memberRepository.save(member);

        Post post = Post.builder()
                .title("게시글1")
                .content("내용1")
                .member(member)
                .build();


        postRepository.save(post);


        //when
        Long postIdAfterLikeBtn = postService.thumbUp(post.getId(), member.getId());

        Post postAfterLikeBtn = postRepository.findById(postIdAfterLikeBtn).get();

        //then
        assertThat(postAfterLikeBtn.getLikes()).hasSize(1)
                .extracting("member.id")
                .containsExactly(member.getId());

    }

    @DisplayName("게시글을 삭제한다. 이때 댓글과 좋아요 정보도 같이 삭제된다.")
    @Test
    void deletePost(){

        //given
        Member member = new Member("socialId", SocialProvider.KAKAO, "박승범", "컴퓨터공학과", 2025);

        memberRepository.save(member);

        PostCreateDTO request = createPostRequest(Category.MENTEE, "제목1", "내용1", List.of(1L, 2L));

        Long createdPostId = postService.createPost(request, member.getId());

        Post post = postRepository.findById(createdPostId).get();

        Comment comment1 = Comment.builder()
                .content("댓글1")
                .member(member)
                .post(post)
                .build();

        Comment comment2 = Comment.builder()
                .content("댓글2")
                .member(member)
                .post(post)
                .build();

        post.addComment(comment1);
        post.addComment(comment2);

        commentRepository.saveAll(List.of(comment1, comment2));

        postService.thumbUp(post.getId(), member.getId());

        //when
        postService.deletePost(createdPostId, member.getId());

        //then
        assertThatThrownBy(() -> postService.getPost(createdPostId, member.getId()))
                .isInstanceOf(CustomMentorException.class);


    }

    @DisplayName("게시글을 태그 리스트와 카테고리로 조회한다. 이때 해당하는 검색조건이 주어지지 않으면 무시한다.")
    @Test
    void searchByTagsAndCategory(){

        //given

        Member member = new Member("socialId", SocialProvider.KAKAO, "박승범", "컴퓨터공학과", 2025);

        memberRepository.save(member);

        PostCreateDTO request5 = createPostRequest(Category.MENTEE, "제목1", "내용1", List.of(1L, 2L, 3L));
        PostCreateDTO request1 = createPostRequest(Category.MENTEE, "제목1", "내용1", List.of(1L, 3L));
        PostCreateDTO request2 = createPostRequest(Category.MENTEE, "제목1", "내용1", List.of(2L, 3L));
        PostCreateDTO request3 = createPostRequest(Category.MENTOR, "제목1", "내용1", List.of(4L, 5L));
        PostCreateDTO request4 = createPostRequest(Category.MENTOR, "제목1", "내용1", List.of(1L));

        postService.createPost(request1, member.getId());
        postService.createPost(request2, member.getId());
        postService.createPost(request3, member.getId());
        postService.createPost(request4, member.getId());
        postService.createPost(request5, member.getId());

        //when
        List<Post> searchWithTagsAndCategory = postRepository.searchByTagsAndCategory(List.of(1L), Category.MENTEE);
        List<Post> searchWithNoTags = postRepository.searchByTagsAndCategory(null, Category.MENTEE);
        List<Post> searchWithNoCategory = postRepository.searchByTagsAndCategory(List.of(1L, 3L), null);

        //then
        assertThat(searchWithTagsAndCategory).hasSize(2)
                .extracting("category")
                .containsExactlyInAnyOrder(Category.MENTEE, Category.MENTEE);
        assertThat(searchWithNoTags).hasSize(3)
                .extracting("category")
                .containsExactly(Category.MENTEE, Category.MENTEE, Category.MENTEE);
        assertThat(searchWithNoCategory).hasSize(2);
        assertThat(searchWithTagsAndCategory).allSatisfy(post ->{
            List<Long> tagIds = post.getTags().stream()
                    .map(postTag -> postTag.getTag().getId())
                    .toList();
            assertThat(tagIds).containsAll(List.of(1L, 3L));
        });


    }

    private static PostCreateDTO createPostRequest(Category category, String title, String content, List<Long> tagIds) {
        return PostCreateDTO.builder()
                .title(title)
                .content(content)
                .tagIds(tagIds)
                .category(category)
                .chatRoomType(ChatRoomType.PUBLIC)
                .build();
    }

    private static Post createPost(Member member, String title, String content) {
        return Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .category(Category.MENTEE)
                .capacity(8)
                .chatRoomType(ChatRoomType.PUBLIC)
                .build();
    }

}