package com.example.runningservice.repository.post;

import com.example.runningservice.dto.post.GetPostSimpleResponseDto;
import com.example.runningservice.entity.post.PostEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>, PostRepositoryCustom {

    Optional<PostEntity> findByIdAndMember_Id(Long id, Long userId);

    Page<PostEntity> findAllByCrewIdAndIsNoticeOrderByCreatedAtDesc(Long crewId, Boolean isNotice,
        Pageable pageable);

    //Todo
    @Query(value = """
    SELECT p.id, p.title, m.nick_name, p.created_at, p.updated_at 
    FROM post p
    JOIN member m ON p.member_id = m.id
    WHERE p.crew_id = :crewId 
    AND (
        (:searchType = 'TITLE_CONTENT' 
        AND to_tsvector('korean', p.title || ' ' || p.content) @@ to_tsquery('korean', :keyword)
        OR to_tsvector('english', p.title || ' ' || p.content) @@ to_tsquery('english', :keyword))             
        OR (:searchType = 'AUTHOR' 
        AND m.nick_name LIKE CONCAT('%', :keyword, '%'))
    )
    """,
        countQuery = """
    SELECT COUNT(*)
    FROM post p
    JOIN member m ON p.member_id = m.id
    WHERE p.crew_id = :crewId
    AND (
        (:searchType = 'TITLE_CONTENT' 
        AND to_tsvector('korean', p.title || ' ' || p.content) @@ to_tsquery('korean', :keyword))
        OR to_tsvector('english', p.title || ' ' || p.content) @@ to_tsquery('english', :keyword))
        OR (:searchType = 'AUTHOR' 
        AND m.nick_name LIKE CONCAT('%', :keyword, '%'))
    )
    """,
        nativeQuery = true
    )
    Page<GetPostSimpleResponseDto> searchPostByFullText(@Param("crewId") Long crewId,
        @Param("searchType") String searchType, @Param("keyword") String keyword,
        Pageable pageable);
}
