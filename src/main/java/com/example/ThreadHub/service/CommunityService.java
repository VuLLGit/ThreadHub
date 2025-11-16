package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.CommunityRequest;
import com.example.ThreadHub.entity.Account;

public interface CommunityService {

    Boolean isNameAvailable(String name);

    void createCommunity(CommunityRequest communityRequest, Account account);
}
