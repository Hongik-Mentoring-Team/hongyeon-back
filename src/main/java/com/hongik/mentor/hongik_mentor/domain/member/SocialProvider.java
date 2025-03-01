package com.hongik.mentor.hongik_mentor.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialProvider {
    NAVER("naver"), GOOGLE("google"), KAKAO("kakao");  //변수명("socialProvider")

    private final String socialProvider;

    public static SocialProvider from(String registrationId) {
        for (SocialProvider provider : values()) {
            if (provider.getSocialProvider().equals(registrationId)) {
                return provider;
            }
        }

        //registrationId가 SocialProvider목록에 없는 경우
        throw new IllegalArgumentException("unknown registration Id: " + registrationId);
    }
}
