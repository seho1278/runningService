package com.example.runningservice.entity;

import com.example.runningservice.enums.CrewRole;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "crew_member", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "crew_id"})
})
@EntityListeners(AuditingEntityListener.class)
public class CrewMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;
    @ManyToOne
    @JoinColumn(name = "crew_id")
    private CrewEntity crew;
    @Enumerated(EnumType.STRING)
    private CrewRole role;
    private Integer roleOrder;
    @CreatedDate
    private LocalDateTime joinedAt;

    @Builder
    public CrewMemberEntity(Long id, MemberEntity member, CrewEntity crew, CrewRole role,
        LocalDateTime joinedAt) {
        this.id = id;
        this.member = member;
        this.crew = crew;
        this.role = role;
        this.roleOrder = role != null ? role.getOrder() : null;
        this.joinedAt = joinedAt;
    }

    public static CrewMemberEntity of(MemberEntity member, CrewEntity crew) {
        return CrewMemberEntity.builder()
            .member(member)
            .crew(crew)
            .role(CrewRole.MEMBER)
            .roleOrder(CrewRole.MEMBER.getOrder())
            .build();
    }

    public void changeRoleTo(CrewRole newRole) {
        if (newRole == this.role) {
            throw new CustomException(ErrorCode.ROLE_NOT_CHANGED);
        }
        this.role = newRole;
        this.roleOrder = newRole.getOrder();
    }

    public void acceptLeaderRole() {
        this.role = CrewRole.LEADER;
        this.roleOrder = CrewRole.LEADER.getOrder();
    }
}
