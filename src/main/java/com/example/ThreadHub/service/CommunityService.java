package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.CommunityRequest;
import com.example.ThreadHub.dto.response.CommunityResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;

import java.util.List;

public interface CommunityService {

    Boolean isNameAvailable(String name);

    Community getCommunityById(Long communityId);

    CommunityResponse createCommunity(CommunityRequest communityRequest, Account account);

    CommunityResponse editCommunity(CommunityRequest CommunityRequest, Long communityId);

    CommunityResponse inactivateCommunity(Long communityId);

    CommunityResponse activateCommunity(Long communityId);

    List<CommunityResponse> getAllCommunitiesByAccountId(Long accountId);
}
