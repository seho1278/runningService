package com.example.runningservice.repository;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.enums.Gender;
import com.example.runningservice.enums.Region;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CrewRepository extends JpaRepository<CrewEntity, Long> {

    boolean existsByCrewName(String crewName);

    @Query("SELECT c, m.nickName, COUNT(cm.crew.crewId) AS crewOccupancy "
        + "FROM CrewEntity c LEFT JOIN CrewMemberEntity cm ON c.crewId = cm.crew.crewId "
        + "JOIN member m ON c.leader.id = m.id "
        + "WHERE (:region IS NULL OR c.activityRegion = :region) "
        + "AND (:minAge IS NULL OR c.minAge <= :minAge) "
        + "AND (:maxAge IS NULL OR c.maxAge >= :maxAge) "
        + "AND (:gender IS NULL OR c.gender = :gender) "
        + "AND (:runRecordPublic IS NULL OR c.runRecordOpen = :runRecordPublic) "
        + "AND (:leaderRequired IS NULL OR c.leaderRequired = :leaderRequired) "
        + "GROUP BY c.crewId, c.crewCapacity, c.createdAt, m.nickName "
        + "HAVING c.crewCapacity <= COUNT(cm.crew.crewId) "
        + "ORDER BY c.createdAt DESC")
    Page<Object[]> findFullCrewList(@Param("region") Region activityRegion,
        @Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge,
        @Param("gender") Gender gender, @Param("runRecordPublic") Boolean runRecordPublic,
        @Param("leaderRequired") Boolean leaderRequired, Pageable pageable);

    @Query("SELECT c, m.nickName, COUNT(cm.crew.crewId) AS crewOccupancy "
        + "FROM CrewEntity c LEFT JOIN CrewMemberEntity cm ON c.crewId = cm.crew.crewId "
        + "JOIN member m ON c.leader.id = m.id "
        + "WHERE (:region IS NULL OR c.activityRegion = :region) "
        + "AND (:minAge IS NULL OR c.minAge <= :minAge) "
        + "AND (:maxAge IS NULL OR c.maxAge >= :maxAge) "
        + "AND (:gender IS NULL OR c.gender = :gender) "
        + "AND (:runRecordPublic IS NULL OR c.runRecordOpen = :runRecordPublic) "
        + "AND (:leaderRequired IS NULL OR c.leaderRequired = :leaderRequired) "
        + "GROUP BY c.crewId, c.crewCapacity, c.createdAt, m.nickName "
        + "HAVING c.crewCapacity > COUNT(cm.crew.crewId) "
        + "ORDER BY c.createdAt DESC")
    Page<Object[]> findAvailableCrewList(@Param("region") Region activityRegion,
        @Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge,
        @Param("gender") Gender gender, @Param("runRecordPublic") Boolean runRecordPublic,
        @Param("leaderRequired") Boolean leaderRequired, Pageable pageable);

    @Query("SELECT c, m.nickName, COUNT(cm.crew.crewId) AS crewOccupancy "
        + "FROM CrewEntity c LEFT JOIN CrewMemberEntity cm ON c.crewId = cm.crew.crewId "
        + "JOIN member m ON c.leader.id = m.id "
        + "WHERE (:region IS NULL OR c.activityRegion = :region) "
        + "AND (:minAge IS NULL OR c.minAge <= :minAge) "
        + "AND (:maxAge IS NULL OR c.maxAge >= :maxAge) "
        + "AND (:gender IS NULL OR c.gender = :gender) "
        + "AND (:runRecordPublic IS NULL OR c.runRecordOpen = :runRecordPublic) "
        + "AND (:leaderRequired IS NULL OR c.leaderRequired = :leaderRequired) "
        + "GROUP BY c.crewId, c.crewCapacity, c.createdAt, m.nickName "
        + "ORDER BY c.createdAt DESC")
    Page<Object[]> findAllCrewList(@Param("region") Region activityRegion,
        @Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge,
        @Param("gender") Gender gender, @Param("runRecordPublic") Boolean runRecordPublic,
        @Param("leaderRequired") Boolean leaderRequired);
}
