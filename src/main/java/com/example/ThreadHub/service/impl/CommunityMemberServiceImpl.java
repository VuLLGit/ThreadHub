package com.example.ThreadHub.service.impl;

import com.example.ThreadHub.dto.response.CommunityMemberResponse;
import com.example.ThreadHub.entity.Account;
import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.CommunityMember;
import com.example.ThreadHub.entity.enums.CommunityStatus;
import com.example.ThreadHub.entity.enums.MemberRole;
import com.example.ThreadHub.entity.enums.MemberStatus;
import com.example.ThreadHub.exception.NotFoundException;
import com.example.ThreadHub.repository.AccountRepository;
import com.example.ThreadHub.repository.CommunityMemberRepository;
import com.example.ThreadHub.repository.CommunityRepository;
import com.example.ThreadHub.service.CommunityMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommunityMemberServiceImpl implements CommunityMemberService {

    /* ===================== CONSTANT MESSAGES ===================== */
    private static final String MSG_MEMBER_NOT_FOUND = "Cannot find member";
    private static final String MSG_COMMUNITY_NOT_FOUND = "Community not found";
    private static final String MSG_COMMUNITY_INACTIVE = "Community is inactive";
    private static final String MSG_ALREADY_MEMBER = "You are already a member of this community";
    private static final String MSG_NOT_A_MEMBER = "You are not a member of this community";
    private static final String MSG_OWNER_MUST_TRANSFER = "You need to transfer Owner before leaving community";
    /* ============================================================= */

    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityRepository communityRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public CommunityMemberServiceImpl(CommunityMemberRepository communityMemberRepository, CommunityRepository communityRepository, AccountRepository accountRepository) {
        this.communityMemberRepository = communityMemberRepository;
        this.communityRepository = communityRepository;
        this.accountRepository = accountRepository;
    }

    private CommunityMemberResponse mapToDto(CommunityMember communityMember) {
        CommunityMemberResponse dto = new CommunityMemberResponse();
        dto.setId(communityMember.getId());
        dto.setMemberRole(communityMember.getMemberRole());
        dto.setMemberStatus(communityMember.getMemberStatus());
        dto.setAccountId(communityMember.getAccount().getId());
        dto.setCommunityId(communityMember.getCommunity().getId());
        return dto;
    }

    public boolean isMemberExist(Long communityMemberId) {
        CommunityMember communityMember = communityMemberRepository.findById(communityMemberId).orElse(null);
        return communityMember != null;
    }

    @Override
    public Page<CommunityMemberResponse> getCommunityMembers(int page, int size, String sortBy, Long CommunityId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        Page<CommunityMember> communityMembers = communityMemberRepository.findAllByCommunityId(CommunityId, pageable);

        return communityMembers.map(this::mapToDto);

    }

    @Override
    public CommunityMember findByCommunityIdAndAccountId(Long CommunityId, Long accountId) {
        return communityMemberRepository.findByCommunityIdAndAccountId(CommunityId, accountId);
    }


    public CommunityMemberResponse assignModerator(Long communityMemberId) {
        if(!isMemberExist(communityMemberId)){
            throw new NotFoundException(MSG_MEMBER_NOT_FOUND);
        }
        CommunityMember communityMember = communityMemberRepository.findById(communityMemberId).orElseThrow();
        communityMember.setMemberRole(MemberRole.MODERATOR);
        communityMemberRepository.save(communityMember);
        return mapToDto(communityMember);
    }

    @Override
    public CommunityMemberResponse removeModerator(Long communityMemberId) {
        if(!isMemberExist(communityMemberId)){
            throw new NotFoundException(MSG_MEMBER_NOT_FOUND);
        }
        CommunityMember communityMember = communityMemberRepository.findById(communityMemberId).orElseThrow();
        communityMember.setMemberRole(MemberRole.MEMBER);
        communityMemberRepository.save(communityMember);

        return mapToDto(communityMember);
    }

    @Override
    public CommunityMemberResponse transferOwner(Long actorCommunityMemberId, Long newOwnerCommunityMemberId) {
        if(!isMemberExist(newOwnerCommunityMemberId)){
            throw new NotFoundException(MSG_MEMBER_NOT_FOUND);
        }
        CommunityMember newOwnerCommunityMember = communityMemberRepository.findById(newOwnerCommunityMemberId).orElseThrow();
        newOwnerCommunityMember.setMemberRole(MemberRole.OWNER);
        communityMemberRepository.save(newOwnerCommunityMember);

        CommunityMember actorCommunityMember = communityMemberRepository.findById(actorCommunityMemberId).orElseThrow();
        actorCommunityMember.setMemberRole(MemberRole.MODERATOR);
        communityMemberRepository.save(actorCommunityMember);

        return mapToDto(newOwnerCommunityMember);
    }

    @Override
    public void joinCommunity(Long communityId, Long accountId) {
        CommunityMember actorCommunityMember = communityMemberRepository.findByCommunityIdAndAccountId(communityId, accountId);
        Community community = communityRepository.findById(communityId).orElse(null);
        Account account = accountRepository.findById(accountId).orElse(null);

        if (community == null) {
            throw new RuntimeException(MSG_COMMUNITY_NOT_FOUND);
        }

        if (community.getCommunityStatus() == CommunityStatus.INACTIVE) {
            throw new RuntimeException(MSG_COMMUNITY_INACTIVE);
        }

        if (actorCommunityMember != null) {
            throw new RuntimeException(MSG_ALREADY_MEMBER);
        }

        CommunityMember communityMember = new CommunityMember();
        communityMember.setCommunity(community);
        communityMember.setAccount(account);
        communityMember.setMemberRole(MemberRole.MEMBER);
        communityMember.setMemberStatus(MemberStatus.ACTIVE);
        communityMemberRepository.save(communityMember);
    }

    @Override
    @Transactional
    public void leaveCommunity(Long communityId, Long accountId) {
        CommunityMember communityMember = communityMemberRepository.findByCommunityIdAndAccountId(communityId, accountId);

        if (communityMember == null) {
            throw new RuntimeException(MSG_NOT_A_MEMBER);
        }

        if (communityMember.getMemberRole() == MemberRole.OWNER) {
            throw new RuntimeException(MSG_OWNER_MUST_TRANSFER);
        }

        communityMemberRepository.delete(communityMember);
        communityMemberRepository.flush();
    }
}
