package com.example.runningservice.dto.crew;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.util.validator.UniqueCrewName;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

public class CrewRequestDto {

    @Getter
    @Setter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Base {

        @NotBlank(message = "크루 소개를 입력해 주세요.")
        @Size(max = 500, message = "크루 소개는 500자 이내로 작성해야 합니다.")
        protected String description;
        @Min(value = 1, message = "크루 정원은 1 이상 입력해야 합니다.")
        private Integer crewCapacity;
        private Region activityRegion;
        private MultipartFile crewImage;
        private String crewImageUrl;
        @NotNull(message = "러닝 공개 여부는 필수 선택입니다.")
        private Boolean runRecordOpen;
        private Boolean waitingAllowed;
        private Boolean leaderRequired;
        private Integer minAge;
        private Integer maxAge;
        private Gender gender;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @SuperBuilder
    public static class Create extends Base {

        @NotBlank(message = "크루명을 입력해 주세요.")
        @Size(max = 30, message = "30자 이내로 작성해야 합니다.")
        @UniqueCrewName
        private String crewName;
        private Long leaderId;

        public void setLoginUserId(Long userId) {
            this.leaderId = userId;
        }
    }

    @Getter
    @Setter
    @SuperBuilder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Update extends Base {

        private Long crewId;

        public void setUpdateCrewId(Long crewId) {
            this.crewId = crewId;
        }
    }
}
