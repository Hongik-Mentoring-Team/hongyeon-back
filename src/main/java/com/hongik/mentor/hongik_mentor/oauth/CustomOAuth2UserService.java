package com.hongik.mentor.hongik_mentor.oauth;

import com.hongik.mentor.hongik_mentor.domain.Member;
import com.hongik.mentor.hongik_mentor.domain.SocialProvider;
import com.hongik.mentor.hongik_mentor.oauth.dto.OAuthAttributes;
import com.hongik.mentor.hongik_mentor.oauth.dto.SessionMember;
import com.hongik.mentor.hongik_mentor.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)  //DB에 member 조회 쿼리를 실행시키기 위해 필요함
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService {
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    //Custom OAuth2User(principal) 반환
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
    //1.
        //userRequest -> OAuthAttributes 생성
        String registrationId = userRequest.getClientRegistration().getRegistrationId();    //소셜공급자 구분
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();   //socialId를 담고있는 Key값 (ex. Google은 "sub", Facebook은 "id")

        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());//attributes받아옴 from 위임된 OAuth2UserService의 OAuth2User로부터


        //OAuthAttributes -> Member엔티티 변환 & DB 저장 또는 수정
        Member member = saveOrUpdate(oAuthAttributes, userNameAttributeName);
    //2.
        //httpSession에 저장
        httpSession.setAttribute("sessionMember", new SessionMember(member,(String)oAuthAttributes.getAttributes().get("name")));

    //3.
        //Session(또는 Spring Context)에 Principal저장
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),   //Authority설정: 이후에 Authentication.authorities에 저장됨
                oAuthAttributes.getAttributes(),
                oAuthAttributes.getNameAttributeKey()
        );
    }

    //Member가 이미 있다면 update, 없다면 save
    private Member saveOrUpdate(OAuthAttributes oAuthAttributes, String usernameAttributeName) {
        String socialId = oAuthAttributes.getSocialId();
        SocialProvider socialProvider = SocialProvider.from(oAuthAttributes.getRegistrationId());
        Optional<Member> findOptionalMember = memberRepository.findBySocialId(socialId, socialProvider);

        //DB에 이미 Member있음
        if (findOptionalMember.isPresent()) {
            log.info("가입된 회원입니다 위치: {}", this.getClass());
            Member member = findOptionalMember.get();
            return member;
        }
        //DB에 Member 없음 == 회원가입
        else{
//            return memberRepository.save(oAuthAttributes.toEntity()); //Member의 다른 멤버변수가 채워지지 않았기에 save불가
            log.info("신규 회원입니다 위치: {}", this.getClass());
            return oAuthAttributes.toEntity();
        }
    }
}
