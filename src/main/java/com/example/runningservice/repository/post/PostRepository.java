package com.example.runningservice.repository.post;

import com.example.runningservice.entity.post.PostEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long>, PostRepositoryCustom {

    Optional<PostEntity> findByIdAndMember_Id(Long id, Long userId);

    Page<PostEntity> findAllByCrewIdAndIsNoticeOrderByCreatedAtDesc(Long crewId, Boolean isNotice,
        Pageable pageable);

}
