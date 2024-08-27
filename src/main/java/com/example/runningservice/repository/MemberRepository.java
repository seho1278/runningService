package com.example.runningservice.repository;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumberHash(String phoneNumberHash);

    default MemberEntity findMemberById(Long userId) {
        return findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    Boolean existsByNickName(String nickName);
}
