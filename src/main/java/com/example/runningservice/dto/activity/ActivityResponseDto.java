package com.example.runningservice.dto.activity;

import com.example.runningservice.entity.ActivityEntity;
import com.example.runningservice.enums.ActivityCategory;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ActivityResponseDto {

    private Long activityId;
    private String author;
    private ActivityCategory category;
    private Long regularId;
    private String title;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    private String memo;
    private String location;
    private int participant;
    private boolean isAuthor;

    public static ActivityResponseDto fromEntity(ActivityEntity activityEntity, Long loginId) {
        return ActivityResponseDto.builder()
            .activityId(activityEntity.getId())
            .author(activityEntity.getAuthor().getNickName())
            .category(activityEntity.getCategory())
            .regularId((activityEntity.getRegularRun() != null) ?
                activityEntity.getRegularRun().getId() : null)
            .title(activityEntity.getTitle())
            .date(activityEntity.getDate())
            .startTime(activityEntity.getStartTime())
            .endTime(activityEntity.getEndTime())
            .memo(activityEntity.getMemo())
            .location(activityEntity.getLocation())
            .participant(
                (activityEntity.getParticipant() != null) ?
                    activityEntity.getParticipant().size() : 0)
            .isAuthor(activityEntity.getAuthor().getId().equals(loginId))
            .build();
    }

    public boolean getIsAuthor() {
        return isAuthor;
    }
}
