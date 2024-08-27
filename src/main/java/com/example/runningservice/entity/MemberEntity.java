package com.example.runningservice.entity;

import com.example.runningservice.dto.SignupRequestDto;
import com.example.runningservice.dto.member.UpdateMemberRequestDto;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Notification;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Role;
import com.example.runningservice.enums.Visibility;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
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
    @Column(nullable = false, unique = true)
    private String phoneNumberHash;
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
    // 프로필 공개
    @Column(name = "username_visibility")
    private Visibility nameVisibility;
    @Column(name = "phone_number_visibility")
    private Visibility phoneNumberVisibility;
    @Column(name = "gender_visibility")
    private Visibility genderVisibility;
    @Column(name = "birth_year_visibility")
    private Visibility birthYearVisibility;
    @Column(name = "run_record_visibility")
    private Visibility runRecordVisibility;

    // 알림 설정
    @Column(name = "post_noti")
    private Notification postNoti;
    @Column(name = "reply_noti")
    private Notification replyNoti;
    @Column(name = "chatting_noti")
    private Notification chattingNoti;
    @Column(name = "activity_noti")
    private Notification activityNoti;

    //러닝 프로필
    @OneToMany
    @JoinColumn(name = "run_record_id")
    private List<RunRecordEntity> runRecordEntities = new ArrayList<>();
    @OneToOne
    @JoinColumn(name = "run_goal_id")
    private RunGoalEntity runGoalEntity;


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

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateMemberProfile(UpdateMemberRequestDto form){
        this.nickName = form.getNickName();
        this.birthYear = form.getBirthYear();
        this.gender = form.getGender();
        this.activityRegion = form.getActivityRegion();
        this.nameVisibility = form.getNameVisibility();
        this.phoneNumberVisibility = form.getPhoneNumberVisibility();
        this.genderVisibility = form.getGenderVisibility();
        this.birthYearVisibility = form.getBirthYearVisibility();
    }

    public void updateAdditionalInfo(SignupRequestDto form, AESUtil aesUtil) {
        this.name = form.getName();
        this.phoneNumber = form.getPhoneNumber();
        this.phoneNumberHash = aesUtil.generateHash(form.getPhoneNumber());
        this.gender = form.getGender();
        this.birthYear = form.getBirthYear();
        this.activityRegion = form.getActivityRegion();
        this.nameVisibility = form.getNameVisibility();
        this.genderVisibility = form.getGenderVisibility();
        this.birthYearVisibility = form.getBirthYearVisibility();
        this.phoneNumberVisibility = form.getPhoneNumberVisibility();
    }
}
