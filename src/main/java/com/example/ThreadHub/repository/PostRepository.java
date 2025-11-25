package com.example.ThreadHub.repository;

import com.example.ThreadHub.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
       SELECT p FROM Post p
       WHERE (:keyword IS NULL
          OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
          OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
       """)
    Page<Post> findAllSearchedPosts(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
       SELECT p FROM Post p
       WHERE p.community.id = :communityId
         AND (:keyword IS NULL
              OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
       """)
    Page<Post> findAllSearchedPostsInCommunity(@Param("communityId") Long communityId, @Param("keyword") String keyword, Pageable pageable);

    Page<Post> findAllByCommunityId(Long communityId, Pageable pageable);
}
