package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.response.CommunityMemberResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.CommunityMember;
import org.springframework.data.domain.Page;

public interface CommunityMemberService {

    Page<CommunityMemberResponse> getCommunityMembers(int page, int size, String sortBy, Long CommunityId);

    CommunityMember findByCommunityIdAndAccountId(Long CommunityId, Long accountId);

    boolean isOwner(Long communityMemberId);

    boolean isModerator(Long communityMemberId);

    boolean isMemberExist(Long communityMemberId);

    void assignModerator(Long communityMemberId);

    void removeModerator(Long communityMemberId);

    void transferOwner(Long actorCommunityMemberId, Long newOwnerCommunityMemberId);

    void joinCommunity(Long communityId, Long accountId);

    void leaveCommunity(Long communityId, Long accountId);
}
