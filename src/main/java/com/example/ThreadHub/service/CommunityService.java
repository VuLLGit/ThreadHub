package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.CreateCommunityRequest;
import com.example.ThreadHub.dto.request.UpdateCommunityRequest;
import com.example.ThreadHub.dto.response.MyCommunitiesResonse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;

import java.util.List;

public interface CommunityService {

    Boolean isNameAvailable(String name);

    Community getCommunityById(Long communityId);

    void createCommunity(CreateCommunityRequest createCommunityRequest, Account account);

    void editCommunity(UpdateCommunityRequest uodateCommunityRequest, Long communityId);

    void inactivateCommunity(Long communityId);

    void activateCommunity(Long communityId);

    List<MyCommunitiesResonse> getAllCommunitiesByAccountId(Long accountId);
}
