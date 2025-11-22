package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.request.CreateCommunityRequest;
import com.example.ThreadHub.dto.request.UpdateCommunityRequest;
import com.example.ThreadHub.dto.response.CommunityResponse;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComunityServiceImpl implements CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;

    @Autowired
    public ComunityServiceImpl(CommunityRepository communityRepository, CommunityMemberRepository communityMemberRepository) {
        this.communityRepository = communityRepository;
        this.communityMemberRepository = communityMemberRepository;
    }

    private CommunityResponse mapToDTO(Community community) {
        CommunityResponse dto = new CommunityResponse();
        dto.setId(community.getId());
        dto.setName(community.getName());
        dto.setImageUrl(community.getImageUrl());
        dto.setCommunityStatus(community.getCommunityStatus());
        return dto;
    }

    @Override
    public Boolean isNameAvailable(String name) {
        return communityRepository.findAll().stream()
                .noneMatch(community -> community.getName().equals(name));
    }

    @Override
    public Community getCommunityById(Long communityId) {
        return communityRepository.findById(communityId).orElse(null);
    }

    @Override
    public CommunityResponse createCommunity(CreateCommunityRequest createCommunityRequest, Account account) {
        Community community = new Community();
        community.setName(createCommunityRequest.getName());
        community.setImageUrl(createCommunityRequest.getImageUrl());
        community.setCommunityStatus(CommunityStatus.ACTIVE);
        communityRepository.save(community);

        CommunityMember communityMember = new CommunityMember();
        communityMember.setCommunity(community);
        communityMember.setAccount(account);
        communityMember.setMemberRole(MemberRole.OWNER);
        communityMember.setMemberStatus(MemberStatus.ACTIVE);
        communityMemberRepository.save(communityMember);

        return mapToDTO(community);
    }

    @Override
    public CommunityResponse editCommunity(UpdateCommunityRequest updateCommunityRequest, Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow();
        community.setName(updateCommunityRequest.getName());
        community.setImageUrl(updateCommunityRequest.getImageUrl());
        community.setCommunityStatus(CommunityStatus.ACTIVE);
        communityRepository.save(community);

        return mapToDTO(community);
    }

    @Override
    public CommunityResponse inactivateCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow();
        community.setCommunityStatus(CommunityStatus.INACTIVE);
        communityRepository.save(community);

        return mapToDTO(community);
    }

    @Override
    public CommunityResponse activateCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow();
        community.setCommunityStatus(CommunityStatus.ACTIVE);
        communityRepository.save(community);

        return mapToDTO(community);
    }

    @Override
    public List<CommunityResponse> getAllCommunitiesByAccountId(Long accountId) {
        List<CommunityMember> communityMembers = communityMemberRepository.findAllByAccountId(accountId);
        List<Community> communities = communityMembers.stream().map(CommunityMember::getCommunity).toList();

        return communities.stream().map(this::mapToDTO).collect(Collectors.toList());
    }
}
