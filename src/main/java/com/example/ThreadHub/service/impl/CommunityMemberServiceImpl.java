package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.response.CommunityMemberResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.CommunityMember;
import com.example.ThreadHub.entity.enums.MemberRole;
import com.example.ThreadHub.entity.enums.MemberStatus;
import com.example.ThreadHub.repository.AccountRepository;
import com.example.ThreadHub.repository.CommunityMemberRepository;
import com.example.ThreadHub.repository.CommunityRepository;
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
    private final CommunityRepository communityRepository;
    private final AccountRepository accountRepository;

    @Autowired
    private CommunityMemberServiceImpl(CommunityMemberRepository communityMemberRepository, CommunityRepository communityRepository, AccountRepository accountRepository) {
        this.communityMemberRepository = communityMemberRepository;
        this.communityRepository = communityRepository;
        this.accountRepository = accountRepository;
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
    public boolean IsModerator(Long communityId, Long accountId) {
        CommunityMember communityMember = communityMemberRepository.findByCommunityIdAndAccountId(communityId, accountId);
        return communityMember.getMemberRole().equals(MemberRole.MODERATOR);
    }

    @Override
    public void assignModerator(Long CommunityMemberId) {
        CommunityMember communityMember = communityMemberRepository.findById(CommunityMemberId).orElseThrow();
        communityMember.setMemberRole(MemberRole.MODERATOR);
        communityMemberRepository.save(communityMember);
    }

    @Override
    public void removeModerator(Long CommunityMemberId) {
        CommunityMember communityMember = communityMemberRepository.findById(CommunityMemberId).orElseThrow();
        communityMember.setMemberRole(MemberRole.MEMBER);
        communityMemberRepository.save(communityMember);
    }

    @Override
    public void joinCommunity(Long communityId, Long accountId) {
        CommunityMember communityMember = new CommunityMember();
        Account account = accountRepository.findById(accountId).orElseThrow();
        Community community = communityRepository.findById(communityId).orElseThrow();
        communityMember.setCommunity(community);
        communityMember.setAccount(account);
        communityMember.setMemberRole(MemberRole.MEMBER);
        communityMember.setMemberStatus(MemberStatus.ACTIVE);
        communityMemberRepository.save(communityMember);
    }
}
