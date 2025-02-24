package com.hongik.mentor.hongik_mentor.oauth.util;

import com.hongik.mentor.hongik_mentor.oauth.dto.SessionMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

public class SessionUtil {
    public static Long getCurrentMemberId(HttpSession httpSession) {
        if(httpSession==null) throw new IllegalStateException("위치: SessionUtil.getCurrentMemberId");

        SessionMember sessionMember = (SessionMember) httpSession.getAttribute("sessionMember");
        return sessionMember.getId();
    }
}
