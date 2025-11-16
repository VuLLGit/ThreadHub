package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.response.CommunityMemberResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import org.springframework.data.domain.Page;

public interface CommunityMemberService {

    Page<CommunityMemberResponse> getCommunityMembers(int page, int size, String sortBy, Long CommunityId);

    void assignModerator(Community community, Account account);
}
