package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.request.CommunityRequest;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.CommunityMember;
import com.example.ThreadHub.entity.enums.CommunityStatus;
import com.example.ThreadHub.entity.enums.MemberRole;
import com.example.ThreadHub.entity.enums.MemberStatus;
import com.example.ThreadHub.repository.CommunityMemberRepository;
import com.example.ThreadHub.repository.CommunityRepository;
import com.example.ThreadHub.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;

    @Autowired
    private ComunityServiceImpl(CommunityRepository communityRepository, CommunityMemberRepository communityMemberRepository) {
        this.communityRepository = communityRepository;
        this.communityMemberRepository = communityMemberRepository;
    }

    @Override
    public Boolean isNameAvailable(String name) {
        return communityRepository.findAll().stream()
                .noneMatch(community -> community.getName().equals(name));
    }

    @Override
    public void createCommunity(CommunityRequest communityRequest, Account account) {
        Community community = new Community();
        community.setName(communityRequest.getName());
        community.setImageUrl(communityRequest.getImageUrl());
        community.setCommunityStatus(CommunityStatus.ACTIVE);
        communityRepository.save(community);

        CommunityMember communityMember = new CommunityMember();
        communityMember.setCommunity(community);
        communityMember.setAccount(account);
        communityMember.setMemberRole(MemberRole.MODERATOR);
        communityMember.setMemberStatus(MemberStatus.ACTIVE);
        communityMemberRepository.save(communityMember);
    }
}
