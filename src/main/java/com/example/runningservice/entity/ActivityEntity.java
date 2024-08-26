package com.example.runningservice.entity;

import com.example.runningservice.dto.activity.ActivityRequestDto.Update;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.envers.AuditOverride;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "activity")
@AuditOverride(forClass = BaseEntity.class)
public class ActivityEntity extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private MemberEntity author;
    @ManyToOne
    @JoinColumn(name = "crew_id")
    private CrewEntity crew;
    @ManyToOne
    @JoinColumn(name = "regular_run_id")
    private RegularRunMeetingEntity regularRun;
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String notes;
    @OneToMany(mappedBy = "activity", cascade = CascadeType.REMOVE,  orphanRemoval = true)
    private List<ParticipantEntity> participant;

    public void update(Update activityDto) {
        this.title = activityDto.getTitle();
        this.date = activityDto.getDate();
        this.startTime = activityDto.getStartTime();
        this.endTime = activityDto.getEndTime();
        this.location = activityDto.getLocation();
        this.notes = activityDto.getMemo();
    }
}
