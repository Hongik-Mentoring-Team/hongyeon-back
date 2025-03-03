package com.hongik.mentor.hongik_mentor.oauth.dto;

import com.hongik.mentor.hongik_mentor.domain.member.Member;
import com.hongik.mentor.hongik_mentor.domain.member.SocialProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes; //attributes.get(nameAttributeKey) == socialId
    private String nameAttributeKey;    //nameAttributeKey == userNameAttributeName ==  socialId값이 아니라 socialId의 키값 !!키값임
    private String name;
    private String email;
    private String picture;
    private String registrationId;
    private String socialId;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture, String registrationId, String socialId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.registrationId = registrationId;
        this.socialId = socialId;
    }

    //UserRequest -> OAuthAttributes 변환
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> customAttributes = new HashMap<>(attributes);
        if ("naver".equals(registrationId)) {
            return ofNaver("id", customAttributes, registrationId);   //기존의 userNameAttributeName: "response"이므로 -> "id"로 변경 필요
        } else if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, customAttributes, registrationId);
        } else
            return ofKakao(userNameAttributeName, customAttributes, registrationId);


    }

    //kakao로그인용
    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes, String registrationId) {
        //Authentication.attributes에 "socialProvider" 필드 추가
        attributes.put("socialProvider", registrationId);
        //properties 접근
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String nickname = String.valueOf(properties.get("nickname"));

        //account 접근
        Map<String, Object> kakaoAcount = (Map<String, Object>) attributes.get("kakao_acount");
        String email = String.valueOf(kakaoAcount.get("email"));
        Map<String, Object> profile = (Map<String, Object>) kakaoAcount.get("profile");
        String profileImageUrl = String.valueOf(profile.get("profile_image_url"));

        return OAuthAttributes.builder()
                .attributes(attributes)
                .email(email)
                .name(nickname)
                .picture(profileImageUrl)
                .nameAttributeKey(userNameAttributeName)
                .registrationId(registrationId)
                .socialId((String) attributes.get(userNameAttributeName))
                .build();
/*카카오 attributes (JSON) 구조
        예시
        {
          "id": 1234567890,
          "connected_at": "2024-01-01T00:00:00Z",
          "properties": {
            "nickname": "홍길동",
            "profile_image": "http://example.com/profile.jpg",
            "thumbnail_image": "http://example.com/thumbnail.jpg"
          },
          "kakao_account": {
            "profile_needs_agreement": false,
            "profile": {
              "nickname": "홍길동",
              "profile_image_url": "http://example.com/profile.jpg",
              "thumbnail_image_url": "http://example.com/thumbnail.jpg"
            },
            "email_needs_agreement": false,
            "is_email_valid": true,
            "is_email_verified": true,
            "email": "user@example.com"
          }

        }
* */

    }

    //google로그인용
    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String,Object> attributes, String registrationId) {
        //Authentication.attributes에 "socialProvider" 필드 추가
        attributes.put("socialProvider", registrationId);
        return OAuthAttributes.builder()
                .attributes(attributes)
                .email(String.valueOf(attributes.get("email")))
                .name(String.valueOf(attributes.get("name")))
                .picture(String.valueOf(attributes.get("picture")))
                .nameAttributeKey(userNameAttributeName)
                .registrationId(registrationId)
                .socialId((String) attributes.get(userNameAttributeName))
                .build();

/* attributes형식
        {
            "sub": "110169484474386276334",         // 사용자의 고유 식별자 (소셜 아이디 역할)
                "name": "John Doe",                      // 전체 이름
                "given_name": "John",                    // 이름
                "family_name": "Doe",                    // 성
                "picture": "https://lh3.googleusercontent.com/a-/AAuE7m...",  // 프로필 이미지 URL
                "email": "johndoe@gmail.com",            // 이메일 주소
                "email_verified": true,                  // 이메일 인증 여부
                "locale": "en"                           // 지역 정보
        }
*/

    }


    //naver로그인용
    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes,String registrationId) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        //Authentication.attributes에 "socialProvider" 필드 추가
        response.put("socialProvider", registrationId);
        return OAuthAttributes.builder()
                .attributes(response)
                .email(String.valueOf(response.get("email")))
                .name(String.valueOf(response.get("name")))
                .picture(String.valueOf(response.get("profile_image")))
                .nameAttributeKey(userNameAttributeName)
                .registrationId(registrationId)
                .socialId((String) response.get(userNameAttributeName))
                .build();
/*
        {
            "resultcode": "00",          // 요청 결과 코드 (성공이면 "00")
                "message": "success",        // 결과 메시지
                "response": {
                    "id": "32742776",          // 네이버 고유 식별자 (소셜 아이디 역할)
                    "nickname": "john_doe",      // 닉네임
                    "name": "John Doe",         // 전체 이름
                    "email": "johndoe@naver.com", // 이메일 주소
                    "gender": "M",             // 성별 (M/F)
                    "age": "20-29",            // 연령대
                    "birthday": "10-01"        // 생일 (월-일)
                }
        }
*/

    }

    public Member toEntity() {
        return new Member(socialId, SocialProvider.from(registrationId));
    }


}
