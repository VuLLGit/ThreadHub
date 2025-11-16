package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.response.CommunityMemberResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.CommunityMember;
import com.example.ThreadHub.entity.enums.MemberRole;
import com.example.ThreadHub.entity.enums.MemberStatus;
import com.example.ThreadHub.repository.CommunityMemberRepository;
import com.example.ThreadHub.service.CommunityMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CommunityMemberServiceImpl implements CommunityMemberService {

    private final CommunityMemberRepository communityMemberRepository;

    @Autowired
    private CommunityMemberServiceImpl(CommunityMemberRepository communityMemberRepository) {
        this.communityMemberRepository = communityMemberRepository;
    }

    @Override
    public Page<CommunityMemberResponse> getCommunityMembers(int page, int size, String sortBy, Long CommunityId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<CommunityMember> communityMembers = communityMemberRepository.findAllByCommunityId(CommunityId, pageable);

        //map to dto
        return  communityMembers.map(entity ->{
            CommunityMemberResponse dto = new CommunityMemberResponse();
            dto.setId(entity.getId());
            dto.setMemberRole(entity.getMemberRole());
            dto.setMemberStatus(entity.getMemberStatus());
            dto.setAccountId(entity.getAccount().getId());
            dto.setCommunityId(entity.getCommunity().getId());
            return dto;
        });

    }

    @Override
    public void assignModerator(Community community, Account account) {
        CommunityMember communityMember = new CommunityMember();
        communityMember.setCommunity(community);
        communityMember.setAccount(account);
        communityMember.setMemberRole(MemberRole.MODERATOR);
        communityMember.setMemberStatus(MemberStatus.ACTIVE);
        communityMemberRepository.save(communityMember);
    }
}
