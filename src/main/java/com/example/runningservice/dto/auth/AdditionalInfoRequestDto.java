package com.example.runningservice.dto.auth;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.enums.Visibility;
import com.example.runningservice.util.validator.ValidYear;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AdditionalInfoRequestDto {
    //이메일 형식
    @NotBlank
    @Email
    @Size(max = 50)
    private String email;

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
}
