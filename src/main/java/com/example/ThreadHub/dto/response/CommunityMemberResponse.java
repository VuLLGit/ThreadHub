package com.example.ThreadHub.dto.response;

import com.example.ThreadHub.entity.enums.MemberRole;
import com.example.ThreadHub.entity.enums.MemberStatus;


public class CommunityMemberResponse {

    private Long id;

    private MemberRole memberRole;

    private MemberStatus memberStatus;

    private Long AccountId;

    private Long CommunityId;

    //getter setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MemberRole getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(MemberRole memberRole) {
        this.memberRole = memberRole;
    }

    public MemberStatus getMemberStatus() {
        return memberStatus;
    }

    public void setMemberStatus(MemberStatus memberStatus) {
        this.memberStatus = memberStatus;
    }

    public Long getAccountId() {
        return AccountId;
    }

    public void setAccountId(Long accountId) {
        AccountId = accountId;
    }

    public Long getCommunityId() {
        return CommunityId;
    }

    public void setCommunityId(Long communityId) {
        CommunityId = communityId;
    }
}
