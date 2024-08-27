package com.example.runningservice.dto;

import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Role;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.util.AESUtil;
import com.example.runningservice.util.validator.PasswordMatches;
import com.example.runningservice.util.validator.ValidYear;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PasswordMatches
public class SignupRequestDto {

    //이메일 형식
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

    //영문+숫자+특수문자 포함, 8~50자
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,50}$", message = "비밀번호는 영문, 숫자, 특수문자를 포함하여 8 ~ 50자 이내여야 합니다.")
    private String password;

    //영문+숫자+특수문자 포함, 8~50자
    @NotBlank
    private String confirmPassword;

    //숫자만 포함
    @NotBlank
    @Pattern(regexp = "^01[016789]\\d{7,8}$", message = "휴대전화번호 형식이 올바르지 않습니다.")
    private String phoneNumber;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 2, max = 12)
    private String name;

    //2~12자
    @NotBlank
    @Size(min = 2, max = 12)
    private String nickName;

    @NotNull
    private Gender gender;

    @NotNull
    @ValidYear
    private Integer birthYear;
    private Region activityRegion;
    private MultipartFile profileImage;

    @NotNull
    private Visibility nameVisibility = Visibility.PRIVATE;
    @NotNull
    private Visibility phoneNumberVisibility = Visibility.PRIVATE;
    @NotNull
    private Visibility genderVisibility = Visibility.PRIVATE;
    @NotNull
    private Visibility birthYearVisibility = Visibility.PRIVATE;

    public MemberEntity toEntity(PasswordEncoder passwordEncoder, AESUtil aesUtil) throws Exception {
        return MemberEntity.builder()
            .email(email)
            .emailVerified(false)
            .password(passwordEncoder.encode(password))
            .phoneNumber(aesUtil.encrypt(phoneNumber))
            .phoneNumberHash(aesUtil.generateHash(phoneNumber))
            .name(name)
            .nickName(nickName)
            .birthYear(birthYear)
            .gender(gender)
            .activityRegion(activityRegion)
            .roles(new ArrayList<>(List.of(Role.ROLE_USER)))
            .nameVisibility(nameVisibility)
            .phoneNumberVisibility(phoneNumberVisibility)
            .genderVisibility(genderVisibility)
            .birthYearVisibility(birthYearVisibility)
            .build();
    }
}
