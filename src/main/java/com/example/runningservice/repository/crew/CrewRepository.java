package com.example.runningservice.repository.crew;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewRepository extends JpaRepository<CrewEntity, Long>, CrewRepositoryCustom {

    boolean existsByCrewName(String crewName);

    @Query("SELECT c "
        + "FROM CrewEntity c "
        + "WHERE (:region IS NULL OR c.activityRegion = :region OR c.activityRegion = 'NATIONWIDE') "
        + "AND (:minYear IS NULL OR c.minYear <= :minYear) "
        + "AND (:maxYear IS NULL OR c.maxYear >= :maxYear) "
        + "AND (:gender IS NULL OR c.gender = :gender) "
        + "AND (:runRecordPublic IS NULL OR c.runRecordOpen = :runRecordPublic) "
        + "AND (:leaderRequired IS NULL OR c.leaderRequired = :leaderRequired) "
        + "AND c.crewCapacity <= SIZE(c.crewMember) "
        + "ORDER BY SIZE(c.crewMember) DESC")
    Page<CrewEntity> findFullCrewList(@Param("region") Region activityRegion,
        @Param("minYear") Integer minYear, @Param("maxYear") Integer maxYear,
        @Param("gender") Gender gender, @Param("runRecordPublic") Boolean runRecordPublic,
        @Param("leaderRequired") Boolean leaderRequired, Pageable pageable);

    @Query("SELECT c "
        + "FROM CrewEntity c "
        + "WHERE (:region IS NULL OR c.activityRegion = :region OR c.activityRegion = 'NATIONWIDE') "
        + "AND (:minYear IS NULL OR c.minYear <= :minYear) "
        + "AND (:maxYear IS NULL OR c.maxYear >= :maxYear) "
        + "AND (:gender IS NULL OR c.gender = :gender) "
        + "AND (:runRecordPublic IS NULL OR c.runRecordOpen = :runRecordPublic) "
        + "AND (:leaderRequired IS NULL OR c.leaderRequired = :leaderRequired) "
        + "AND c.crewCapacity > SIZE(c.crewMember) "
        + "ORDER BY SIZE(c.crewMember) DESC")
    Page<CrewEntity> findAvailableCrewList(@Param("region") Region activityRegion,
        @Param("minYear") Integer minYear, @Param("maxYear") Integer maxYear,
        @Param("gender") Gender gender, @Param("runRecordPublic") Boolean runRecordPublic,
        @Param("leaderRequired") Boolean leaderRequired, Pageable pageable);

    @Query("SELECT c "
        + "FROM CrewEntity c "
        + "WHERE (:region IS NULL OR c.activityRegion = :region OR c.activityRegion = 'NATIONWIDE') "
        + "AND (:minYear IS NULL OR c.minYear <= :minYear) "
        + "AND (:maxYear IS NULL OR c.maxYear >= :maxYear) "
        + "AND (:gender IS NULL OR c.gender = :gender) "
        + "AND (:runRecordPublic IS NULL OR c.runRecordOpen = :runRecordPublic) "
        + "AND (:leaderRequired IS NULL OR c.leaderRequired = :leaderRequired) "
        + "ORDER BY SIZE(c.crewMember) DESC")
    Page<CrewEntity> findAllCrewList(@Param("region") Region activityRegion,
        @Param("minYear") Integer minYear, @Param("maxYear") Integer maxYear,
        @Param("gender") Gender gender, @Param("runRecordPublic") Boolean runRecordPublic,
        @Param("leaderRequired") Boolean leaderRequired, Pageable pageable);

    default CrewEntity findCrewById(Long crewId) {
        return findById(crewId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CREW));
    }
}
