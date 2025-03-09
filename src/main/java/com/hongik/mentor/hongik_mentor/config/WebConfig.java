package com.hongik.mentor.hongik_mentor.config;

import com.hongik.mentor.hongik_mentor.constant.ConstantUri;
import com.hongik.mentor.hongik_mentor.global.interceptor.LoggingInterceptor;
import com.hongik.mentor.hongik_mentor.oauth.LoginMemberArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry interceptorRegistry) {
        interceptorRegistry.addInterceptor(loggingInterceptor);
    }

    //MVC config에 LoginMemberArgumentResolver 객체 등록
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    //MVC - CORS 응답 헤더 설정
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(ConstantUri.NEED_TO_CHAGE) //허용할 외부 origin
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders(ConstantUri.NEED_TO_CHAGE)
                .allowCredentials(true);        //인증수단(쿠키) 전송 허용
    }
}
