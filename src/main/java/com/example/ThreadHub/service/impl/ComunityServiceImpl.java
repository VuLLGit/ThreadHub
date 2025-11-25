package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.request.CommunityRequest;
import com.example.ThreadHub.dto.response.CommunityResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.CommunityMember;
import com.example.ThreadHub.entity.enums.CommunityStatus;
import com.example.ThreadHub.entity.enums.MemberRole;
import com.example.ThreadHub.entity.enums.MemberStatus;
import com.example.ThreadHub.exception.NotFoundException;
import com.example.ThreadHub.repository.CommunityMemberRepository;
import com.example.ThreadHub.repository.CommunityRepository;
import com.example.ThreadHub.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
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

    private Boolean isNameAvailable(String name) {
        return communityRepository.findAll().stream()
                .noneMatch(community -> community.getName().equals(name));
    }

    @Override
    public Page<CommunityResponse> getAllCommunities(int page, int size, String sortBy, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        Page<Community> communities;
        if (search == null || search.isBlank()) {
            communities = communityRepository.findAll(pageable);
        } else {
            communities = communityRepository.findAllSearchedCommunities(search.trim(), pageable);
        }

        return communities.map(this::mapToDTO);
    }

    @Override
    public CommunityResponse getCommunityById(Long communityId) {
        Community community = communityRepository.findById(communityId).orElse(null);

        if( community == null ) {
            throw new NotFoundException("Community not found");
        }

        return mapToDTO(community);
    }

    @Override
    public CommunityResponse createCommunity(CommunityRequest communityRequest, Account account) {

        if (!isNameAvailable(communityRequest.getName())) {
            throw new RuntimeException("Community name already exists");
        }

        Community community = new Community();
        community.setName(communityRequest.getName());
        community.setImageUrl(communityRequest.getImageUrl());
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
    public CommunityResponse editCommunity(CommunityRequest communityRequest, Long communityId) {
        Community community = communityRepository.findById(communityId).orElseThrow();
        community.setName(communityRequest.getName());
        community.setImageUrl(communityRequest.getImageUrl());
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
