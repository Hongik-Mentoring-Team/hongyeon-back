package com.hongik.mentor.hongik_mentor.oauth.dto;

import com.hongik.mentor.hongik_mentor.domain.member.Member;
import com.hongik.mentor.hongik_mentor.domain.member.MemberType;
import com.hongik.mentor.hongik_mentor.domain.member.SocialProvider;
import com.hongik.mentor.hongik_mentor.exception.ErrorCode;
import com.hongik.mentor.hongik_mentor.exception.RegisterMemberException;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class SessionMember {
    private Long id;
    private String socialId;
    private SocialProvider provider;
    private String name;
    private String major;
    private Integer graduationYear;
    private MemberType type;

    public SessionMember(Member member, String tempName) {
        this.id= member.getId();
        this.socialId=member.getSocialId();
        this.provider=member.getSocialProvider();
        this.name = member.getName() == null ? tempName : member.getName();
        this.major= member.getMajor();
        this.graduationYear= member.getGraduationYear();
        this.type = member.getType();
    }

    public SessionMember(String socialId, SocialProvider provider, String name, String major, Integer graduationYear, MemberType type) {
        this.socialId = socialId;
        this.provider = provider;
        this.name = name;
        this.major = major;
        this.graduationYear = graduationYear;
        this.type = type;
    }

    public void update(String name, String major, Integer graduationYear) {
        this.name=name;
        this.major=major;
        this.graduationYear=graduationYear;
    }

    public Long getId() {
        if (id == null) {
            throw new RegisterMemberException(ErrorCode.NOT_REGISTERED_USER);
        } else {
            return id;
        }
    }


}
