package com.example.runningservice.entity;

import com.example.runningservice.dto.crew.CrewRequestDto.Create;
import com.example.runningservice.dto.crew.CrewRequestDto.Update;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.util.converter.GenderConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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
@Table(name = "crew")
@AuditOverride(forClass = BaseEntity.class)
public class CrewEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "leader_id")
    private MemberEntity leader;
    private String crewName;
    private String crewImage;
    private String description;
    private Integer crewCapacity;
    @Enumerated(EnumType.STRING)
    private Region activityRegion;
    private Boolean runRecordOpen;
    private Integer minAge;
    private Integer maxAge;
    @Convert(converter = GenderConverter.class)
    private Gender gender;
    private Boolean leaderRequired;
    @OneToMany(mappedBy = "crew", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrewMemberEntity> crewMember;

    public void updateCrewImageUrl(String imageUrl) {
        this.crewImage = imageUrl;
    }

    public static CrewEntity toEntity(Create dto, MemberEntity memberEntity) {
        return CrewEntity.builder()
            .leader(memberEntity)
            .crewName(dto.getCrewName())
            .description(dto.getDescription())
            .crewCapacity(dto.getCrewCapacity())
            .activityRegion(dto.getActivityRegion())
            .runRecordOpen(dto.getRunRecordOpen())
            .minAge(dto.getMinAge())
            .maxAge(dto.getMaxAge())
            .gender(dto.getGender())
            .leaderRequired(dto.getLeaderRequired())
            .build();
    }

    public void updateFromDto(Update updateCrew) {
        this.description = updateCrew.getDescription();
        this.activityRegion = updateCrew.getActivityRegion();
        this.crewCapacity = updateCrew.getCrewCapacity();
        this.runRecordOpen = updateCrew.getRunRecordOpen();
        this.leaderRequired = updateCrew.getLeaderRequired();
        this.minAge = updateCrew.getMinAge();
        this.maxAge = updateCrew.getMaxAge();
        this.gender = updateCrew.getGender();
    }
}
