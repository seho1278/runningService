package com.example.runningservice.entity;

import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "crew")
@EntityListeners(AuditingEntityListener.class)
public class CrewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long crewId;
    @ManyToOne
    @JoinColumn(name = "leader_id")
    private MemberEntity member;
    private String crewName;
    private String crewImage;
    private String description;
    private Integer crewCapacity;
    @Enumerated(EnumType.STRING)
    private Region activityRegion;
    private Boolean waitingAllowed;
    private Boolean runRecordOpen;
    private Integer minAge;
    private Integer maxAge;
    private Gender gender;
    private Boolean leaderRequired;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
