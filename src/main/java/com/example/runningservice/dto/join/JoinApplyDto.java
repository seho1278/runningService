package com.example.runningservice.dto.join;

import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.enums.JoinStatus;
import com.example.runningservice.enums.Region;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
public class JoinApplyDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @Size(max = 100)
        String message;
    }

    @Getter
    @Builder
    public static class SimpleResponse {
        private Long joinApplyId;
        private String crewName;
        private String crewImage;
        private JoinStatus status;
        private Integer capacity;
        private Integer currentMemberCount;
        private LocalDateTime appliedAt;
        private LocalDateTime updatedAt;

        public static SimpleResponse from(JoinApplyEntity joinApplyEntity) {
            return SimpleResponse.builder()
                .joinApplyId(joinApplyEntity.getId())
                .crewName(joinApplyEntity.getCrew().getCrewName())
                .crewImage(joinApplyEntity.getCrew().getCrewImage())
                .status(joinApplyEntity.getStatus())
                .capacity(joinApplyEntity.getCrew().getCrewCapacity())
                .currentMemberCount(joinApplyEntity.getCrew().getCrewMember().size())
                .appliedAt(joinApplyEntity.getCreatedAt())
                .updatedAt(joinApplyEntity.getUpdatedAt())
                .build();
        }
    }

    @Getter
    @Builder
    public static class DetailResponse {
        private Long joinApplyId;
        private String crewName;
        private String crewImage;
        private JoinStatus status;
        private Integer capacity;
        private Integer currentMemberCount;
        private String applyMessage;
        private String description;
        private Region activityRegion;
        private LocalDateTime appliedAt;
        private LocalDateTime updatedAt;

        public static DetailResponse from(
            JoinApplyEntity joinApplyEntity) {
            return DetailResponse.builder()
                .joinApplyId(joinApplyEntity.getId())
                .crewName(joinApplyEntity.getCrew().getCrewName())
                .crewImage(joinApplyEntity.getCrew().getCrewImage())
                .status(joinApplyEntity.getStatus())
                .capacity(joinApplyEntity.getCrew().getCrewCapacity())
                .currentMemberCount(joinApplyEntity.getCrew().getCrewMember().size())
                .applyMessage(joinApplyEntity.getMessage())
                .description(joinApplyEntity.getCrew().getDescription())
                .activityRegion(joinApplyEntity.getCrew().getActivityRegion())
                .appliedAt(joinApplyEntity.getCreatedAt())
                .updatedAt(joinApplyEntity.getUpdatedAt())
                .build();
        }
    }



}
