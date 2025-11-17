package com.example.ThreadHub.repository;

import com.example.ThreadHub.entity.Community;
import com.example.ThreadHub.entity.CommunityMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    Page<CommunityMember> findAllByCommunityId(Long communityId, Pageable pageable);

    CommunityMember findByCommunityIdAndAccountId(Long communityId, Long accountId);
}
