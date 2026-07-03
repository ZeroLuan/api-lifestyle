package br.com.sysmap.backend.repository;

import br.com.sysmap.backend.entity.ActivityParticipants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ActivityParticipantsRepository extends JpaRepository<ActivityParticipants, UUID> {
    List<ActivityParticipants> findByActivityId(UUID activityId);
    boolean existsByActivityIdAndUserId(UUID activityId, UUID userId);
    Optional<ActivityParticipants> findByActivityIdAndUserId(UUID activityId, UUID userId);
}
