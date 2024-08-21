package com.example.runningservice.dto.join;

import com.example.runningservice.entity.JoinApplyEntity;
import com.example.runningservice.enums.JoinStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
public class JoinApplyDto {

    @Getter
    @Builder
    public static class Request {
        @NotBlank
        Long userId;
        @Size(max = 100)
        String message;
    }

    @Getter
    @Builder
    public static class SimpleResponse {
        private String nickname;
        private String crewName;
        private JoinStatus status;
        private LocalDateTime appliedAt;
        private LocalDateTime updatedAt;

        public static SimpleResponse from(
            JoinApplyEntity joinApplyEntity) {
            return SimpleResponse.builder()
                .nickname(joinApplyEntity.getMember().getNickName())
                .crewName(joinApplyEntity.getCrew().getCrewName())
                .status(joinApplyEntity.getStatus())
                .appliedAt(joinApplyEntity.getCreatedAt())
                .updatedAt(joinApplyEntity.getUpdatedAt())
                .build();
        }
    }

    @Getter
    @Builder
    public static class DetailResponse {
        private String nickname;
        private String crewName;
        private JoinStatus status;
        private String applyMessage;
        private LocalDateTime appliedAt;
        private LocalDateTime updatedAt;

        public static DetailResponse from(
            JoinApplyEntity joinApplyEntity) {
            return DetailResponse.builder()
                .nickname(joinApplyEntity.getMember().getNickName())
                .crewName(joinApplyEntity.getCrew().getCrewName())
                .status(joinApplyEntity.getStatus())
                .applyMessage(joinApplyEntity.getMessage())
                .appliedAt(joinApplyEntity.getCreatedAt())
                .updatedAt(joinApplyEntity.getUpdatedAt())
                .build();
        }
    }



}
