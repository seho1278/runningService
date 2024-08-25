package com.example.runningservice.entity;

import com.example.runningservice.dto.regular_run.RegularRunRequestDto;
import com.example.runningservice.enums.Region;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.envers.AuditOverride;
import org.hibernate.type.SqlTypes;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "regular_run_meeting")
@AuditOverride(forClass = BaseEntity.class)
public class RegularRunMeetingEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "crew_id")
    private CrewEntity crew;
    private int count;
    private int week;
    @Enumerated(EnumType.STRING)
    private Region activityRegion;
    @Column(columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> dayOfWeek;
    private LocalTime time;

    public static RegularRunMeetingEntity toEntity(RegularRunRequestDto regularDto,
        CrewEntity crewEntity) {
        return RegularRunMeetingEntity.builder()
            .crew(crewEntity)
            .week(regularDto.getWeek())
            .count(regularDto.getCount())
            .time(regularDto.getTime())
            .dayOfWeek(regularDto.getDayOfWeek())
            .activityRegion(regularDto.getActivityRegion())
            .build();
    }

    public List<String> getDayOfWeek() {
        return Collections.unmodifiableList(this.dayOfWeek);
    }

    public void updateRegularRunInfo(int count, int week, Region activityRegion, LocalTime time) {
        this.count = count;
        this.week = week;
        this.activityRegion = activityRegion;
        this.time = time;
    }

    public void addDayOfWeek(String dayOfWeek) {
        this.dayOfWeek.add(dayOfWeek);
    }

    public void clearDayOfWeek() {
        this.dayOfWeek.clear();
    }
}
