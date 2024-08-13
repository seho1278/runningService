package com.example.runningservice.entity;

import com.example.runningservice.dto.MemberResponseDto;
import com.example.runningservice.enums.*;
import com.example.runningservice.util.AESUtil;
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
import org.hibernate.envers.AuditOverride;

@Entity(name = "member")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AuditOverride(forClass = BaseEntity.class)
public class MemberEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String verificationCode;
    private boolean emailVerified;
    private LocalDateTime verifiedAt;
    private String password;
    private String phoneNumber;
    private String name;
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

    // 프로필 공개
    @Column(name = "username_visibility")
    private Visibility nameVisibility;
    @Column(name = "phone_number_visibility")
    private Visibility phoneNumberVisibility;
    @Column(name = "gender_visibility")
    private Visibility genderVisibility;
    @Column(name = "birth_year_visibility")
    private Visibility birthYearVisibility;

    // 알림 설정
    @Column(name = "post_noti")
    private Notification postNoti;
    @Column(name = "reply_noti")
    private Notification replyNoti;
    @Column(name = "mention_noti")
    private Notification mentionNoti;
    @Column(name = "chatting_noti")
    private Notification chattingNoti;

    @Builder
    MemberEntity(
            String email,
            String verificationCode,
            boolean emailVerified,
            String password,
            String phoneNumber,
            String name,
            String nickName,
            Integer birthYear,
            Gender gender,
            Region activityRegion,
            List<Role> roles
    ) {
        this.email = email;
        this.verificationCode = verificationCode;
        this.emailVerified = emailVerified;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.nickName = nickName;
        this.birthYear = birthYear;
        this.gender = gender;
        this.activityRegion = activityRegion;
        this.roles = roles == null ? new ArrayList<>() : roles;
    }

    public MemberResponseDto toResponseDto(AESUtil aesUtil) throws Exception {
        return MemberResponseDto.builder()
            .id(id)
            .email(email)
            .emailVerified(emailVerified)
            .phoneNumber(aesUtil.decrypt(phoneNumber))
            .name(name)
            .nickName(nickName)
            .birthYear(birthYear)
            .gender(gender)
            .roles(roles)
            .build();
    }

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

    public void updateProfileVisibility(
        int nameVisibility, int phoneNumberVisibility, int genderVisibility, int birthYearVisibility) {
        this.nameVisibility = Visibility.fromCode(nameVisibility);
        this.phoneNumberVisibility = Visibility.fromCode(phoneNumberVisibility);
        this.genderVisibility = Visibility.fromCode(genderVisibility);
        this.birthYearVisibility = Visibility.fromCode(birthYearVisibility);
    }
}
