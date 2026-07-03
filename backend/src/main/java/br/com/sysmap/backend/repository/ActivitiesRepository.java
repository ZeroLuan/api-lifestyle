package br.com.sysmap.backend.repository;

import br.com.sysmap.backend.entity.Activities;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivitiesRepository extends JpaRepository<Activities, UUID> {

    @Query("SELECT a FROM Activities a " +
           "LEFT JOIN Preferences p ON p.activityType.id = a.type.id AND p.user.id = :userId " +
           "WHERE a.deletedAt IS NULL AND a.completedAt IS NULL " +
           "AND (:typeId IS NULL OR a.type.id = :typeId) " +
           "ORDER BY (CASE WHEN p.id IS NOT NULL THEN 0 ELSE 1 END) ASC, a.createdAt DESC")
    Page<Activities> findAllActiveWithPriority(@Param("typeId") UUID typeId, @Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT a FROM Activities a WHERE a.deletedAt IS NULL AND a.completedAt IS NULL AND (:typeId IS NULL OR a.type.id = :typeId)")
    List<Activities> findAllActiveList(@Param("typeId") UUID typeId);

    @Query("SELECT a FROM Activities a WHERE a.creator.id = :creatorId AND a.deletedAt IS NULL")
    Page<Activities> findByCreatorId(@Param("creatorId") UUID creatorId, Pageable pageable);

    @Query("SELECT a FROM Activities a WHERE a.creator.id = :creatorId AND a.deletedAt IS NULL")
    List<Activities> findAllByCreatorId(@Param("creatorId") UUID creatorId);

    @Query("SELECT a FROM Activities a JOIN a.participants p WHERE p.user.id = :userId AND a.deletedAt IS NULL")
    Page<Activities> findByParticipantId(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT a FROM Activities a JOIN a.participants p WHERE p.user.id = :userId AND a.deletedAt IS NULL")
    List<Activities> findAllByParticipantId(@Param("userId") UUID userId);

    @Query("SELECT a FROM Activities a WHERE a.id = :id AND a.deletedAt IS NULL")
    Optional<Activities> findActiveById(@Param("id") UUID id);
}
