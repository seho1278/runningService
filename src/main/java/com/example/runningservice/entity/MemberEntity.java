package com.example.runningservice.entity;

import com.example.runningservice.dto.MemberResponseDto;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Role;
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
    private boolean emailVerified;
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

    @Builder
    MemberEntity(
            String email,
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
        this.emailVerified = emailVerified;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.nickName = nickName;
        this.birthYear = birthYear;
        this.gender = gender;
        this.activityRegion = activityRegion;
        this.roles = roles != null ? roles : List.of(Role.ROLE_USER);
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
}
