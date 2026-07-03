package br.com.sysmap.backend.repository;

import br.com.sysmap.backend.entity.UserAchievements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserAchievementsRepository extends JpaRepository<UserAchievements, UUID> {
    boolean existsByUserIdAndAchievementId(UUID userId, UUID achievementId);
}
