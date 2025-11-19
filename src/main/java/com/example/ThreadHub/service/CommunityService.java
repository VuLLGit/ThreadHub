package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.CreateCommunityRequest;
import com.example.ThreadHub.dto.request.UpdateCommunityRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;

public interface CommunityService {

    Boolean isNameAvailable(String name);

    Community getCommunityById((Long communityId);

    void createCommunity(CreateCommunityRequest createCommunityRequest, Account account);

    void EditCommunity(UpdateCommunityRequest uodateCommunityRequest, Long communityId);

    void inactivateCommunity(Long communityId);

    void activateCommunity(Long communityId);
}
