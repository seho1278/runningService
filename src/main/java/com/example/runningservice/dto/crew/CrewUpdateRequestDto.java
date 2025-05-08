package com.example.runningservice.dto.crew;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.util.validator.YearRange;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@YearRange(minYear = "minYear", maxYear = "maxYear")
public class CrewUpdateRequestDto {

    @NotBlank(message = "크루 소개를 입력해 주세요.")
    @Size(max = 500, message = "크루 소개는 500자 이내로 작성해야 합니다.")
    private final String description;
    @Min(value = 1, message = "크루 정원은 1 이상 입력해야 합니다.")
    private final Integer crewCapacity;
    private final Region activityRegion;
    private final MultipartFile crewImage;
    @NotNull(message = "러닝 공개 여부는 필수 선택입니다.")
    private final Boolean runRecordOpen;
    @NotNull(message = "리더 승인 여부는 필수 선택입니다.")
    private final Boolean leaderRequired;
    private final Integer minYear;
    private final Integer maxYear;
    private final Gender gender;
    private final Boolean deleteCrewImage;

    @ConstructorProperties({"description", "crewCapacity", "activityRegion", "crewImage",
        "runRecordOpen", "leaderRequired", "minYear", "maxYear", "gender"})
    public CrewUpdateRequestDto(String description, Integer crewCapacity, Region activityRegion,
        MultipartFile crewImage, Boolean runRecordOpen, Boolean leaderRequired,
        Integer minYear, Integer maxYear, Gender gender, Boolean deleteCrewImage) {
        this.description = description;
        this.crewCapacity = crewCapacity;
        this.activityRegion = activityRegion;
        this.crewImage = crewImage;
        this.runRecordOpen = runRecordOpen;
        this.leaderRequired = leaderRequired;
        this.minYear = minYear;
        this.maxYear = maxYear;
        this.gender = gender;
        this.deleteCrewImage = deleteCrewImage;
    }
}
