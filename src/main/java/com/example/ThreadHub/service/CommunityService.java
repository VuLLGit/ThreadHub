package com.example.ThreadHub.service;

import com.example.ThreadHub.dto.request.CreateCommunityRequest;
import com.example.ThreadHub.entity.Account;

public interface CommunityService {

    Boolean isNameAvailable(String name);

    void createCommunity(CreateCommunityRequest createCommunityRequest, Account account);
}
