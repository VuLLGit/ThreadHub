package com.example.ThreadHub.repository;

import com.example.ThreadHub.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    @Query("""
        SELECT c FROM Community c
        WHERE (:keyword IS NULL
        OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
        """)
    Page<Community> findAllSearchedCommunities(@Param("keyword") String search, Pageable pageable);
}
