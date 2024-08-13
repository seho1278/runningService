package com.example.runningservice.entity;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Role;
import com.example.runningservice.util.converter.GenderConverter;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditOverride;

@Entity(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@AuditOverride(forClass = BaseEntity.class)
public class MemberEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String email;
    private String verificationCode;
    private boolean emailVerified;
    private LocalDateTime verifiedAt;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String phoneNumber;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String nickName;
    private Integer birthYear;
    //성별값을 코드로 변환
    @Convert(converter = GenderConverter.class)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private Region activityRegion;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "member_roles", joinColumns = @JoinColumn(name = "member_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private List<Role> roles;

    private String profileImageUrl;  // 프로필 이미지 URL 추가

    public void markEmailVerified() {
        this.emailVerified = true;
        this.verifiedAt = LocalDateTime.now();
    }

    public void saveVerificationCode(String code) {
        this.verificationCode = code;
    }
  
    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateMemberProfile(
        String nickName, Integer birthYear, Gender gender, Region activityRegion) {
        this.nickName = nickName;
        this.birthYear = birthYear;
        this.gender = gender;
        this.activityRegion = activityRegion;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

}
