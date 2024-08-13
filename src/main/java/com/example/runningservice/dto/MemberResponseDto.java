package com.example.runningservice.dto;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Role;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import com.example.runningservice.util.AESUtil;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String email;
    private boolean emailVerified;
    private String phoneNumber;
    private String name;
    private String nickName;
    private Integer birthYear;
    private Gender gender;
    private List<Role> roles;

    private Region activityRegion;

    public static MemberResponseDto of(MemberEntity memberEntity, AESUtil aesUtil) throws Exception {

        return MemberResponseDto.builder()
                    .id(memberEntity.getId())
                    .email(memberEntity.getEmail())
                    .phoneNumber(aesUtil.decrypt(memberEntity.getPhoneNumber()))
                    .name(memberEntity.getName())
                    .nickName(memberEntity.getNickName())
                    .birthYear(memberEntity.getBirthYear())
                    .gender(memberEntity.getGender())
                    .roles(memberEntity.getRoles())
                    .activityRegion(memberEntity.getActivityRegion())
                    .build();
    }
}
