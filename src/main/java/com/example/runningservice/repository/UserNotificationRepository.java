package com.example.runningservice.repository;

import com.example.runningservice.entity.UserNotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationRepository extends JpaRepository<UserNotificationEntity, Long> {

    Page<UserNotificationEntity> findByMember_IdOrderByNotification_CreatedAtDesc(Long memberId,
        Pageable pageable);
}
