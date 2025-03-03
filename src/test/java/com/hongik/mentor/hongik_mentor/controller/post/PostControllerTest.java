//package com.hongik.mentor.hongik_mentor.controller.post;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hongik.mentor.hongik_mentor.config.SecurityConfig;
//import com.hongik.mentor.hongik_mentor.controller.PostController;
//import com.hongik.mentor.hongik_mentor.controller.dto.PostCreateDTO;
//import com.hongik.mentor.hongik_mentor.domain.Category;
//import com.hongik.mentor.hongik_mentor.domain.chat.ChatRoomType;
//import com.hongik.mentor.hongik_mentor.domain.member.Member;
//import com.hongik.mentor.hongik_mentor.domain.member.SocialProvider;
//import com.hongik.mentor.hongik_mentor.oauth.dto.SessionMember;
//import com.hongik.mentor.hongik_mentor.oauth.util.SessionUtil;
//import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
//import com.hongik.mentor.hongik_mentor.service.PostService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@WebMvcTest(controllers = PostController.class)
//@Import(value = SecurityConfig.class)
//class PostControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//
//    @MockBean
//    private PostService postService;
//
//    @DisplayName("")
//    @Test
//    void createPost() throws Exception {
//
//        //given
//        PostCreateDTO request = PostCreateDTO.builder()
//                .title("title")
//                .content("content")
//                .capacity(8)
//                .chatRoomType(ChatRoomType.PUBLIC)
//                .category(Category.MENTEE)
//                .build();
//
//        Member member1 = new Member("1111", SocialProvider.GOOGLE, "박승범", "컴퓨터공학과", 2025);
//
//        MockHttpSession mockHttpSession = new MockHttpSession();
//        SessionMember park = new SessionMember(member1, "Park");
//
//        mockHttpSession.setAttribute("sessionMember", park);
//
//        //when //then
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/post")
//                .content(objectMapper.writeValueAsString(request))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isCreated());
//
//    }
//}