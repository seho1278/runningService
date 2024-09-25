package com.example.runningservice.repository.post;

import com.example.runningservice.entity.post.ReplyEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<ReplyEntity, Long> {
    Optional<ReplyEntity> findByIdAndMember_Id(Long id, Long memberId);

    Page<ReplyEntity> findAllByPost_Id(Long postId, Pageable pageable);

    Page<ReplyEntity> findAllByMember_Id(Long userId, Pageable pageable);
}
