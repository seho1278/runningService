package com.example.runningservice.dto.crew;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.util.validator.UniqueCrewName;
import com.example.runningservice.util.validator.YearRange;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.beans.ConstructorProperties;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class CrewRequestDto {

    @Getter
    @YearRange(minYear = "minAge", maxYear = "maxAge")
    public static class Base {

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
        private final Integer minAge;
        private final Integer maxAge;
        private final Gender gender;

        public Base(String description, Integer crewCapacity, Region activityRegion,
            MultipartFile crewImage, Boolean runRecordOpen, Boolean leaderRequired,
            Integer minAge, Integer maxAge, Gender gender) {
            this.description = description;
            this.crewCapacity = crewCapacity;
            this.activityRegion = activityRegion;
            this.crewImage = crewImage;
            this.runRecordOpen = runRecordOpen;
            this.leaderRequired = leaderRequired;
            this.minAge = minAge;
            this.maxAge = maxAge;
            this.gender = gender;
        }
    }

    @Getter
    public static class Create extends Base {

        @NotBlank(message = "크루명을 입력해 주세요.")
        @Size(max = 30, message = "30자 이내로 작성해야 합니다.")
        @UniqueCrewName
        private final String crewName;
        private Long leaderId;

        public void setLoginUserId(Long userId) {
            this.leaderId = userId;
        }

        @ConstructorProperties({"description", "crewCapacity", "activityRegion", "crewImage",
            "runRecordOpen", "leaderRequired", "minAge", "maxAge", "gender", "crewName"})
        public Create(String description, Integer crewCapacity, Region activityRegion,
            MultipartFile crewImage, Boolean runRecordOpen, Boolean leaderRequired,
            Integer minAge, Integer maxAge, Gender gender, String crewName) {

            super(description, crewCapacity, activityRegion, crewImage, runRecordOpen,
                leaderRequired, minAge, maxAge, gender);
            this.crewName = crewName;
        }
    }

    @Getter
    public static class Update extends Base {

        private Long crewId;

        public void setUpdateCrewId(Long crewId) {
            this.crewId = crewId;
        }

        @ConstructorProperties({"description", "crewCapacity", "activityRegion", "crewImage",
            "runRecordOpen", "leaderRequired", "minAge", "maxAge", "gender"})
        public Update(String description, Integer crewCapacity, Region activityRegion,
            MultipartFile crewImage, Boolean runRecordOpen, Boolean leaderRequired,
            Integer minAge, Integer maxAge, Gender gender) {

            super(description, crewCapacity, activityRegion, crewImage, runRecordOpen,
                leaderRequired, minAge, maxAge, gender);
        }
    }
}
