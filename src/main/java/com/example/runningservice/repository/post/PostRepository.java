package com.example.runningservice.repository.post;

import com.example.runningservice.entity.post.PostEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Optional<PostEntity> findByIdAndMemberId(Long id, Long userId);
}
