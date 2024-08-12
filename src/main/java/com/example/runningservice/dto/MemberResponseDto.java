package com.example.runningservice.dto;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
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
}
